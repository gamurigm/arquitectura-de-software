using System.Data;
using EurekaBank.Api.Datos;
using EurekaBank.Api.Modelos;
using Microsoft.Data.SqlClient;

namespace EurekaBank.Api.Servicios;

public sealed class SqlEurekaBankRepository(ISqlConnectionFactory connectionFactory) : IEurekaBankRepository
{
    public async Task<CredencialUsuario?> ObtenerCredencialAsync(string usuario, CancellationToken cancellationToken)
    {
        await using var connection = connectionFactory.CreateConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new SqlCommand(
            "SELECT contrasena, chr_cliecodigo FROM dbo.Credenciales WHERE usuario = @usuario;", connection);
        command.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        return await reader.ReadAsync(cancellationToken)
            ? new CredencialUsuario(reader.GetString(0), reader.IsDBNull(1) ? null : reader.GetString(1).TrimEnd())
            : null;
    }

    public async Task<IReadOnlyList<CuentaResponse>> ConsultarCuentasAsync(string codigoCliente, CancellationToken cancellationToken)
    {
        const string sql = "SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo FROM dbo.Cuenta WHERE chr_cliecodigo = @cliente AND vch_cuenestado = N'ACTIVO' ORDER BY chr_cuencodigo;";
        var cuentas = new List<CuentaResponse>();
        await using var connection = connectionFactory.CreateConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.Add("@cliente", SqlDbType.Char, 5).Value = codigoCliente;
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        while (await reader.ReadAsync(cancellationToken))
            cuentas.Add(new CuentaResponse(reader.GetString(0).TrimEnd(), reader.GetString(1).TrimEnd(), reader.GetDecimal(2)));
        return cuentas;
    }

    public async Task<IReadOnlyList<MovimientoResponse>> ConsultarMovimientosAsync(
        string cuenta,
        string codigoCliente,
        CancellationToken cancellationToken)
    {
        const string sql = """
            SELECT m.int_movinumero, m.chr_cuencodigo, m.dtt_movifecha,
                   t.vch_tipodescripcion, t.vch_tipoaccion, m.dec_moviimporte
            FROM dbo.Movimiento AS m
            INNER JOIN dbo.TipoMovimiento AS t ON t.chr_tipocodigo = m.chr_tipocodigo
            INNER JOIN dbo.Cuenta AS c ON c.chr_cuencodigo = m.chr_cuencodigo
            WHERE m.chr_cuencodigo = @cuenta AND c.chr_cliecodigo = @cliente AND c.vch_cuenestado = N'ACTIVO'
            ORDER BY m.int_movinumero DESC;
            """;
        var movimientos = new List<MovimientoResponse>();

        await using var connection = connectionFactory.CreateConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        command.Parameters.Add("@cliente", SqlDbType.Char, 5).Value = codigoCliente;
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        while (await reader.ReadAsync(cancellationToken))
        {
            movimientos.Add(new MovimientoResponse(
                reader.GetInt32(0),
                reader.GetString(1).TrimEnd(),
                new DateTimeOffset(DateTime.SpecifyKind(reader.GetDateTime(2), DateTimeKind.Utc)),
                reader.GetString(3),
                reader.GetString(4),
                reader.GetDecimal(5)));
        }

        return movimientos;
    }

    public async Task<bool> RegistrarDepositoAsync(
        string cuenta,
        decimal importe,
        string codigoCliente,
        CancellationToken cancellationToken)
    {
        await using var connection = connectionFactory.CreateConnection();
        await connection.OpenAsync(cancellationToken);
        await using var transaction = (SqlTransaction)await connection.BeginTransactionAsync(
            IsolationLevel.Serializable, cancellationToken);

        try
        {
            await using var saldoCommand = new SqlCommand(
                "SELECT dec_cuensaldo FROM dbo.Cuenta WITH (UPDLOCK, HOLDLOCK) " +
                "WHERE chr_cuencodigo = @cuenta AND chr_cliecodigo = @cliente AND vch_cuenestado = N'ACTIVO';",
                connection,
                transaction);
            saldoCommand.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
            saldoCommand.Parameters.Add("@cliente", SqlDbType.Char, 5).Value = codigoCliente;
            var saldoResult = await saldoCommand.ExecuteScalarAsync(cancellationToken);
            if (saldoResult is null or DBNull)
            {
                await transaction.RollbackAsync(cancellationToken);
                return false;
            }

            await using var siguienteCommand = new SqlCommand(
                "SELECT ISNULL(MAX(int_movinumero), 0) + 1 FROM dbo.Movimiento WITH (UPDLOCK, HOLDLOCK) " +
                "WHERE chr_cuencodigo = @cuenta;",
                connection,
                transaction);
            siguienteCommand.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
            var siguiente = Convert.ToInt32(await siguienteCommand.ExecuteScalarAsync(cancellationToken));

            await using var updateCommand = new SqlCommand(
                "UPDATE dbo.Cuenta SET dec_cuensaldo = dec_cuensaldo + @importe, " +
                "int_cuencontmov = int_cuencontmov + 1 " +
                "WHERE chr_cuencodigo = @cuenta AND vch_cuenestado = N'ACTIVO';",
                connection,
                transaction);
            updateCommand.Parameters.Add("@importe", SqlDbType.Decimal).Precision = 12;
            updateCommand.Parameters["@importe"].Scale = 2;
            updateCommand.Parameters["@importe"].Value = importe;
            updateCommand.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
            if (await updateCommand.ExecuteNonQueryAsync(cancellationToken) != 1)
            {
                throw new InvalidOperationException("No se pudo actualizar el saldo de la cuenta.");
            }

            await using var insertCommand = new SqlCommand(
                "INSERT INTO dbo.Movimiento " +
                "(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) " +
                "VALUES (@cuenta, @numero, CONVERT(date, SYSUTCDATETIME()), @empleado, '003', @importe);",
                connection,
                transaction);
            insertCommand.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
            insertCommand.Parameters.Add("@numero", SqlDbType.Int).Value = siguiente;
            insertCommand.Parameters.Add("@empleado", SqlDbType.Char, 4).Value = "0001";
            insertCommand.Parameters.Add("@importe", SqlDbType.Decimal).Precision = 12;
            insertCommand.Parameters["@importe"].Scale = 2;
            insertCommand.Parameters["@importe"].Value = importe;
            await insertCommand.ExecuteNonQueryAsync(cancellationToken);

            await transaction.CommitAsync(cancellationToken);
            return true;
        }
        catch (Exception error)
        {
            try
            {
                await transaction.RollbackAsync(CancellationToken.None);
            }
            catch (Exception rollbackError)
            {
                error.Data["RollbackError"] = rollbackError.Message;
            }

            throw;
        }
    }

    public async Task<bool> TransferirAsync(
        string usuario,
        string codigoCliente,
        TransferenciaRequest solicitud,
        CancellationToken cancellationToken)
    {
        await using var connection = connectionFactory.CreateConnection();
        await connection.OpenAsync(cancellationToken);
        await using var transaction = (SqlTransaction)await connection.BeginTransactionAsync(
            IsolationLevel.Serializable, cancellationToken);

        try
        {
            await using (var existenteCommand = new SqlCommand(
                "SELECT chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe " +
                "FROM dbo.Transferencias WITH (UPDLOCK, HOLDLOCK) " +
                "WHERE usuario = @usuario AND clave_operacion = @clave;", connection, transaction))
            {
                existenteCommand.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
                existenteCommand.Parameters.Add("@clave", SqlDbType.UniqueIdentifier).Value = solicitud.ClaveOperacion;
                await using var reader = await existenteCommand.ExecuteReaderAsync(cancellationToken);
                if (await reader.ReadAsync(cancellationToken))
                {
                    var repetida = reader.GetString(0).TrimEnd() == solicitud.CuentaOrigen
                        && reader.GetString(1).TrimEnd() == solicitud.CuentaDestino
                        && reader.GetDecimal(2) == solicitud.Importe;
                    await reader.DisposeAsync();
                    await transaction.CommitAsync(cancellationToken);
                    return repetida;
                }
            }

            var codigos = new[] { solicitud.CuentaOrigen, solicitud.CuentaDestino }
                .Order(StringComparer.Ordinal);
            var cuentas = new Dictionary<string, CuentaTransferencia>(StringComparer.Ordinal);
            foreach (var codigo in codigos)
            {
                var cuenta = await LeerCuentaTransferenciaAsync(connection, transaction, codigo, cancellationToken);
                if (cuenta is null || cuenta.Estado != "ACTIVO")
                {
                    await transaction.RollbackAsync(cancellationToken);
                    return false;
                }
                cuentas[codigo] = cuenta;
            }

            var origen = cuentas[solicitud.CuentaOrigen];
            var destino = cuentas[solicitud.CuentaDestino];
            if (origen.CodigoCliente != codigoCliente || origen.Moneda != destino.Moneda
                || origen.Saldo < solicitud.Importe)
            {
                await transaction.RollbackAsync(cancellationToken);
                return false;
            }

            var idTransferencia = Guid.NewGuid();
            await using (var insertTransferencia = new SqlCommand(
                "INSERT INTO dbo.Transferencias " +
                "(id_transferencia, usuario, clave_operacion, chr_cliecodigo, chr_cuencodigo_origen, " +
                "chr_cuencodigo_destino, dec_importe, dtt_fecha) " +
                "VALUES (@id, @usuario, @clave, @cliente, @origen, @destino, @importe, SYSUTCDATETIME());",
                connection, transaction))
            {
                insertTransferencia.Parameters.Add("@id", SqlDbType.UniqueIdentifier).Value = idTransferencia;
                insertTransferencia.Parameters.Add("@usuario", SqlDbType.VarChar, 30).Value = usuario;
                insertTransferencia.Parameters.Add("@clave", SqlDbType.UniqueIdentifier).Value = solicitud.ClaveOperacion;
                insertTransferencia.Parameters.Add("@cliente", SqlDbType.Char, 5).Value = codigoCliente;
                insertTransferencia.Parameters.Add("@origen", SqlDbType.Char, 8).Value = solicitud.CuentaOrigen;
                insertTransferencia.Parameters.Add("@destino", SqlDbType.Char, 8).Value = solicitud.CuentaDestino;
                insertTransferencia.Parameters.Add("@importe", SqlDbType.Decimal).Value = solicitud.Importe;
                insertTransferencia.Parameters["@importe"].Precision = 12;
                insertTransferencia.Parameters["@importe"].Scale = 2;
                await insertTransferencia.ExecuteNonQueryAsync(cancellationToken);
            }

            await using (var update = new SqlCommand(
                "UPDATE dbo.Cuenta SET dec_cuensaldo = dec_cuensaldo + @delta, " +
                "int_cuencontmov = int_cuencontmov + 1 WHERE chr_cuencodigo = @cuenta AND vch_cuenestado = N'ACTIVO';",
                connection, transaction))
            {
                update.Parameters.Add("@delta", SqlDbType.Decimal).Precision = 12;
                update.Parameters["@delta"].Scale = 2;
                update.Parameters.Add("@cuenta", SqlDbType.Char, 8);
                foreach (var (cuenta, delta) in new[]
                {
                    (solicitud.CuentaOrigen, -solicitud.Importe),
                    (solicitud.CuentaDestino, solicitud.Importe)
                })
                {
                    update.Parameters["@delta"].Value = delta;
                    update.Parameters["@cuenta"].Value = cuenta;
                    if (await update.ExecuteNonQueryAsync(cancellationToken) != 1)
                        throw new InvalidOperationException("No se pudo actualizar el saldo de ambas cuentas.");
                }
            }

            var numeroOrigen = await SiguienteMovimientoAsync(connection, transaction, solicitud.CuentaOrigen, cancellationToken);
            var numeroDestino = await SiguienteMovimientoAsync(connection, transaction, solicitud.CuentaDestino, cancellationToken);
            await InsertarMovimientoTransferenciaAsync(connection, transaction, solicitud.CuentaOrigen,
                numeroOrigen, solicitud.CuentaDestino, "009", solicitud.Importe, idTransferencia, cancellationToken);
            await InsertarMovimientoTransferenciaAsync(connection, transaction, solicitud.CuentaDestino,
                numeroDestino, solicitud.CuentaOrigen, "008", solicitud.Importe, idTransferencia, cancellationToken);

            await transaction.CommitAsync(cancellationToken);
            return true;
        }
        catch (Exception error)
        {
            try { await transaction.RollbackAsync(CancellationToken.None); }
            catch (Exception rollbackError) { error.Data["RollbackError"] = rollbackError.Message; }
            throw;
        }
    }

    private static async Task<CuentaTransferencia?> LeerCuentaTransferenciaAsync(
        SqlConnection connection, SqlTransaction transaction, string codigo, CancellationToken cancellationToken)
    {
        await using var command = new SqlCommand(
            "SELECT chr_cliecodigo, chr_monecodigo, dec_cuensaldo, vch_cuenestado " +
            "FROM dbo.Cuenta WITH (UPDLOCK, HOLDLOCK) WHERE chr_cuencodigo = @cuenta;", connection, transaction);
        command.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = codigo;
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        return await reader.ReadAsync(cancellationToken)
            ? new CuentaTransferencia(reader.GetString(0).TrimEnd(), reader.GetString(1).TrimEnd(),
                reader.GetDecimal(2), reader.GetString(3).TrimEnd())
            : null;
    }

    private static async Task<int> SiguienteMovimientoAsync(
        SqlConnection connection, SqlTransaction transaction, string cuenta, CancellationToken cancellationToken)
    {
        await using var command = new SqlCommand(
            "SELECT ISNULL(MAX(int_movinumero), 0) + 1 FROM dbo.Movimiento WITH (UPDLOCK, HOLDLOCK) " +
            "WHERE chr_cuencodigo = @cuenta;", connection, transaction);
        command.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        return Convert.ToInt32(await command.ExecuteScalarAsync(cancellationToken));
    }

    private static async Task InsertarMovimientoTransferenciaAsync(
        SqlConnection connection, SqlTransaction transaction, string cuenta, int numero, string referencia,
        string tipo, decimal importe, Guid idTransferencia, CancellationToken cancellationToken)
    {
        await using var command = new SqlCommand(
            "INSERT INTO dbo.Movimiento " +
            "(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, " +
            "dec_moviimporte, chr_cuenreferencia, id_transferencia) " +
            "VALUES (@cuenta, @numero, CONVERT(date, SYSUTCDATETIME()), '0001', @tipo, @importe, @referencia, @transferencia);",
            connection, transaction);
        command.Parameters.Add("@cuenta", SqlDbType.Char, 8).Value = cuenta;
        command.Parameters.Add("@numero", SqlDbType.Int).Value = numero;
        command.Parameters.Add("@tipo", SqlDbType.Char, 3).Value = tipo;
        command.Parameters.Add("@importe", SqlDbType.Decimal).Value = importe;
        command.Parameters["@importe"].Precision = 12;
        command.Parameters["@importe"].Scale = 2;
        command.Parameters.Add("@referencia", SqlDbType.Char, 8).Value = referencia;
        command.Parameters.Add("@transferencia", SqlDbType.UniqueIdentifier).Value = idTransferencia;
        await command.ExecuteNonQueryAsync(cancellationToken);
    }

    private sealed record CuentaTransferencia(string CodigoCliente, string Moneda, decimal Saldo, string Estado);
}
