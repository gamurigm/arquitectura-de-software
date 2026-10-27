using System.Data;
using ec.edu.monster.Conexion;
using ec.edu.monster.Modelos;
using Microsoft.Data.SqlClient;

namespace ec.edu.monster.Repositorios;

public sealed class EurekaBankRepository(ConexionDb conexiones)
{
    private const string TipoDeposito = "003";
    private const string EmpleadoDeposito = "0001";

    public async Task<string?> ObtenerHashAsync(string usuario)
    {
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var comando = new SqlCommand("SELECT contrasena FROM dbo.Credenciales WHERE usuario = @usuario", conexion);
        comando.Parameters.Add("@usuario", System.Data.SqlDbType.VarChar, 30).Value = usuario;
        return await comando.ExecuteScalarAsync() as string;
    }

    public async Task<CredencialUsuario?> ObtenerCredencialAsync(string usuario)
    {
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var comando = new SqlCommand(
            "SELECT contrasena, chr_cliecodigo FROM dbo.Credenciales WHERE usuario = @usuario", conexion);
        comando.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
        await using var lector = await comando.ExecuteReaderAsync();
        return await lector.ReadAsync()
            ? new CredencialUsuario(lector.GetString(0), lector.IsDBNull(1) ? null : lector.GetString(1).TrimEnd())
            : null;
    }

    public async Task<List<Cuenta>> TraerCuentasAsync(string codigoCliente)
    {
        const string sql = "SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo FROM dbo.Cuenta "
            + "WHERE chr_cliecodigo = @cliente AND vch_cuenestado = 'ACTIVO' ORDER BY chr_cuencodigo";
        var cuentas = new List<Cuenta>();
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var comando = new SqlCommand(sql, conexion);
        comando.Parameters.Add("@cliente", SqlDbType.Char, 5).Value = codigoCliente;
        await using var lector = await comando.ExecuteReaderAsync();
        while (await lector.ReadAsync())
            cuentas.Add(new Cuenta(lector.GetString(0).TrimEnd(), lector.GetString(1).TrimEnd(), lector.GetDecimal(2)));
        return cuentas;
    }

    public async Task<List<Movimiento>> TraerMovimientosAsync(string cuenta, string codigoCliente)
    {
        const string sql = "SELECT m.chr_cuencodigo, m.int_movinumero, m.dtt_movifecha, t.vch_tipodescripcion, t.vch_tipoaccion, m.dec_moviimporte "
            + "FROM dbo.Movimiento m INNER JOIN dbo.TipoMovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo "
            + "INNER JOIN dbo.Cuenta c ON c.chr_cuencodigo = m.chr_cuencodigo "
            + "WHERE m.chr_cuencodigo = @cuenta AND c.chr_cliecodigo = @cliente AND c.vch_cuenestado = 'ACTIVO' "
            + "ORDER BY m.int_movinumero DESC";
        var movimientos = new List<Movimiento>();
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var comando = new SqlCommand(sql, conexion);
        comando.Parameters.Add("@cuenta", System.Data.SqlDbType.Char, 8).Value = cuenta;
        comando.Parameters.Add("@cliente", System.Data.SqlDbType.Char, 5).Value = codigoCliente;
        await using var lector = await comando.ExecuteReaderAsync();
        while (await lector.ReadAsync())
        {
            movimientos.Add(new Movimiento
            {
                Cuenta = lector.GetString(0).TrimEnd(),
                Nromov = lector.GetInt32(1),
                Fecha = lector.GetDateTime(2),
                Tipo = lector.GetString(3),
                Accion = lector.GetString(4),
                Importe = lector.GetDecimal(5)
            });
        }
        return movimientos;
    }

    public async Task RegistrarDepositoAsync(string cuenta, decimal importe, string codigoCliente)
    {
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var transaccion = (SqlTransaction)await conexion.BeginTransactionAsync(IsolationLevel.Serializable);
        try
        {
            decimal saldo;
            await using (var comando = new SqlCommand("SELECT dec_cuensaldo FROM dbo.Cuenta WITH (UPDLOCK, HOLDLOCK) WHERE chr_cuencodigo = @cuenta AND chr_cliecodigo = @cliente AND vch_cuenestado = 'ACTIVO'", conexion, transaccion))
            {
                comando.Parameters.Add("@cuenta", System.Data.SqlDbType.Char, 8).Value = cuenta;
                comando.Parameters.Add("@cliente", System.Data.SqlDbType.Char, 5).Value = codigoCliente;
                var resultado = await comando.ExecuteScalarAsync();
                if (resultado is null or DBNull) throw new InvalidOperationException("La cuenta no existe o no está activa.");
                saldo = (decimal)resultado;
            }

            int siguienteMovimiento;
            await using (var comando = new SqlCommand("SELECT COALESCE(MAX(int_movinumero), 0) + 1 FROM dbo.Movimiento WHERE chr_cuencodigo = @cuenta", conexion, transaccion))
            {
                comando.Parameters.Add("@cuenta", System.Data.SqlDbType.Char, 8).Value = cuenta;
                siguienteMovimiento = Convert.ToInt32(await comando.ExecuteScalarAsync(), System.Globalization.CultureInfo.InvariantCulture);
            }

            await using (var comando = new SqlCommand("UPDATE dbo.Cuenta SET dec_cuensaldo = @saldo, int_cuencontmov = int_cuencontmov + 1 WHERE chr_cuencodigo = @cuenta AND vch_cuenestado = 'ACTIVO'", conexion, transaccion))
            {
                comando.Parameters.Add("@saldo", System.Data.SqlDbType.Decimal).Value = saldo + importe;
                comando.Parameters["@saldo"].Precision = 12;
                comando.Parameters["@saldo"].Scale = 2;
                comando.Parameters.Add("@cuenta", System.Data.SqlDbType.Char, 8).Value = cuenta;
                if (await comando.ExecuteNonQueryAsync() != 1) throw new InvalidOperationException("No se pudo actualizar el saldo.");
            }

            await using (var comando = new SqlCommand("INSERT INTO dbo.Movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) VALUES (@cuenta, @numero, CONVERT(date, GETDATE()), @empleado, @tipo, @importe)", conexion, transaccion))
            {
                comando.Parameters.Add("@cuenta", System.Data.SqlDbType.Char, 8).Value = cuenta;
                comando.Parameters.Add("@numero", System.Data.SqlDbType.Int).Value = siguienteMovimiento;
                comando.Parameters.Add("@empleado", System.Data.SqlDbType.Char, 4).Value = EmpleadoDeposito;
                comando.Parameters.Add("@tipo", System.Data.SqlDbType.Char, 3).Value = TipoDeposito;
                comando.Parameters.Add("@importe", System.Data.SqlDbType.Decimal).Value = importe;
                comando.Parameters["@importe"].Precision = 12;
                comando.Parameters["@importe"].Scale = 2;
                await comando.ExecuteNonQueryAsync();
            }

            await transaccion.CommitAsync();
        }
        catch (Exception excepcion)
        {
            try { await transaccion.RollbackAsync(); }
            catch (Exception errorRollback) { excepcion.Data["ErrorRollback"] = errorRollback; }
            throw;
        }
    }

    public async Task<bool> TransferirAsync(string usuario, string codigoCliente, TransferenciaRequest solicitud)
    {
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var transaccion = (SqlTransaction)await conexion.BeginTransactionAsync(IsolationLevel.Serializable);
        try
        {
            await using (var existente = new SqlCommand(
                "SELECT chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe " +
                "FROM dbo.Transferencias WITH (UPDLOCK, HOLDLOCK) WHERE usuario = @usuario AND clave_operacion = @clave;",
                conexion, transaccion))
            {
                existente.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
                existente.Parameters.Add("@clave", SqlDbType.UniqueIdentifier).Value = solicitud.ClaveOperacion;
                await using var lector = await existente.ExecuteReaderAsync();
                if (await lector.ReadAsync())
                {
                    var misma = lector.GetString(0).TrimEnd() == solicitud.CuentaOrigen
                        && lector.GetString(1).TrimEnd() == solicitud.CuentaDestino
                        && lector.GetDecimal(2) == solicitud.Importe;
                    await lector.DisposeAsync();
                    await transaccion.CommitAsync();
                    return misma;
                }
            }

            var cuentas = new Dictionary<string, CuentaTransferencia>(StringComparer.Ordinal);
            foreach (var codigo in new[] { solicitud.CuentaOrigen, solicitud.CuentaDestino }.Order(StringComparer.Ordinal))
            {
                var cuenta = await BloquearCuentaAsync(conexion, transaccion, codigo);
                if (cuenta is null || cuenta.Estado != "ACTIVO")
                {
                    await transaccion.RollbackAsync();
                    return false;
                }
                cuentas[codigo] = cuenta;
            }
            var origen = cuentas[solicitud.CuentaOrigen];
            var destino = cuentas[solicitud.CuentaDestino];
            if (origen.CodigoCliente != codigoCliente || origen.Moneda != destino.Moneda || origen.Saldo < solicitud.Importe)
            {
                await transaccion.RollbackAsync();
                return false;
            }

            var id = Guid.NewGuid();
            await using (var insertar = new SqlCommand(
                "INSERT INTO dbo.Transferencias (id_transferencia, usuario, clave_operacion, chr_cliecodigo, " +
                "chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe, dtt_fecha) " +
                "VALUES (@id, @usuario, @clave, @cliente, @origen, @destino, @importe, SYSUTCDATETIME());", conexion, transaccion))
            {
                insertar.Parameters.Add("@id", SqlDbType.UniqueIdentifier).Value = id;
                insertar.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
                insertar.Parameters.Add("@clave", SqlDbType.UniqueIdentifier).Value = solicitud.ClaveOperacion;
                insertar.Parameters.Add("@cliente", SqlDbType.Char, 5).Value = codigoCliente;
                insertar.Parameters.Add("@origen", SqlDbType.Char, 8).Value = solicitud.CuentaOrigen;
                insertar.Parameters.Add("@destino", SqlDbType.Char, 8).Value = solicitud.CuentaDestino;
                insertar.Parameters.Add("@importe", SqlDbType.Decimal).Value = solicitud.Importe;
                insertar.Parameters["@importe"].Precision = 12;
                insertar.Parameters["@importe"].Scale = 2;
                await insertar.ExecuteNonQueryAsync();
            }

            await ActualizarSaldoAsync(conexion, transaccion, solicitud.CuentaOrigen, -solicitud.Importe);
            await ActualizarSaldoAsync(conexion, transaccion, solicitud.CuentaDestino, solicitud.Importe);
            var numeroOrigen = await SiguienteMovimientoAsync(conexion, transaccion, solicitud.CuentaOrigen);
            var numeroDestino = await SiguienteMovimientoAsync(conexion, transaccion, solicitud.CuentaDestino);
            await InsertarMovimientoAsync(conexion, transaccion, solicitud.CuentaOrigen, numeroOrigen,
                solicitud.CuentaDestino, "009", solicitud.Importe, id);
            await InsertarMovimientoAsync(conexion, transaccion, solicitud.CuentaDestino, numeroDestino,
                solicitud.CuentaOrigen, "008", solicitud.Importe, id);
            await transaccion.CommitAsync();
            return true;
        }
        catch (SqlException error) when (error.Number is 2601 or 2627)
        {
            try { await transaccion.RollbackAsync(); } catch { }
            return await EsMismaTransferenciaAsync(usuario, solicitud);
        }
        catch (Exception error)
        {
            try { await transaccion.RollbackAsync(); }
            catch (Exception rollbackError) { error.Data["ErrorRollback"] = rollbackError; }
            throw;
        }
    }

    private static async Task<CuentaTransferencia?> BloquearCuentaAsync(
        SqlConnection conexion, SqlTransaction transaccion, string cuenta)
    {
        await using var comando = new SqlCommand(
            "SELECT chr_cliecodigo, chr_monecodigo, dec_cuensaldo, vch_cuenestado " +
            "FROM dbo.Cuenta WITH (UPDLOCK, HOLDLOCK) WHERE chr_cuencodigo = @cuenta;", conexion, transaccion);
        comando.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        await using var lector = await comando.ExecuteReaderAsync();
        return await lector.ReadAsync()
            ? new CuentaTransferencia(lector.GetString(0).TrimEnd(), lector.GetString(1).TrimEnd(),
                lector.GetDecimal(2), lector.GetString(3).TrimEnd())
            : null;
    }

    private static async Task ActualizarSaldoAsync(SqlConnection conexion, SqlTransaction transaccion, string cuenta, decimal delta)
    {
        await using var comando = new SqlCommand(
            "UPDATE dbo.Cuenta SET dec_cuensaldo = dec_cuensaldo + @delta, int_cuencontmov = int_cuencontmov + 1 " +
            "WHERE chr_cuencodigo = @cuenta AND vch_cuenestado = N'ACTIVO';", conexion, transaccion);
        comando.Parameters.Add("@delta", SqlDbType.Decimal).Value = delta;
        comando.Parameters["@delta"].Precision = 12;
        comando.Parameters["@delta"].Scale = 2;
        comando.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        if (await comando.ExecuteNonQueryAsync() != 1) throw new InvalidOperationException("No se actualizaron ambas cuentas.");
    }

    private static async Task<int> SiguienteMovimientoAsync(SqlConnection conexion, SqlTransaction transaccion, string cuenta)
    {
        await using var comando = new SqlCommand(
            "SELECT ISNULL(MAX(int_movinumero), 0) + 1 FROM dbo.Movimiento WITH (UPDLOCK, HOLDLOCK) WHERE chr_cuencodigo = @cuenta;",
            conexion, transaccion);
        comando.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        return Convert.ToInt32(await comando.ExecuteScalarAsync(), System.Globalization.CultureInfo.InvariantCulture);
    }

    private static async Task InsertarMovimientoAsync(SqlConnection conexion, SqlTransaction transaccion,
        string cuenta, int numero, string referencia, string tipo, decimal importe, Guid id)
    {
        await using var comando = new SqlCommand(
            "INSERT INTO dbo.Movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, " +
            "chr_tipocodigo, dec_moviimporte, chr_cuenreferencia, id_transferencia) " +
            "VALUES (@cuenta, @numero, CONVERT(date, SYSUTCDATETIME()), '0001', @tipo, @importe, @referencia, @id);",
            conexion, transaccion);
        comando.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        comando.Parameters.Add("@numero", SqlDbType.Int).Value = numero;
        comando.Parameters.Add("@tipo", SqlDbType.Char, 3).Value = tipo;
        comando.Parameters.Add("@importe", SqlDbType.Decimal).Value = importe;
        comando.Parameters["@importe"].Precision = 12;
        comando.Parameters["@importe"].Scale = 2;
        comando.Parameters.Add("@referencia", SqlDbType.Char, 8).Value = referencia;
        comando.Parameters.Add("@id", SqlDbType.UniqueIdentifier).Value = id;
        await comando.ExecuteNonQueryAsync();
    }

    private async Task<bool> EsMismaTransferenciaAsync(string usuario, TransferenciaRequest solicitud)
    {
        await using var conexion = conexiones.CrearConexion();
        await conexion.OpenAsync();
        await using var comando = new SqlCommand(
            "SELECT chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe FROM dbo.Transferencias " +
            "WHERE usuario = @usuario AND clave_operacion = @clave;", conexion);
        comando.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
        comando.Parameters.Add("@clave", SqlDbType.UniqueIdentifier).Value = solicitud.ClaveOperacion;
        await using var lector = await comando.ExecuteReaderAsync();
        return await lector.ReadAsync() && lector.GetString(0).TrimEnd() == solicitud.CuentaOrigen
            && lector.GetString(1).TrimEnd() == solicitud.CuentaDestino && lector.GetDecimal(2) == solicitud.Importe;
    }

    private sealed record CuentaTransferencia(string CodigoCliente, string Moneda, decimal Saldo, string Estado);
}
