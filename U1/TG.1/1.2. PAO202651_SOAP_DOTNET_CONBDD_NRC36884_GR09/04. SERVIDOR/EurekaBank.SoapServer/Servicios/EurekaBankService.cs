using ec.edu.monster.Repositorios;
using ec.edu.monster.Seguridad;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Servicios;

public sealed class EurekaBankService(EurekaBankRepository repositorio, SesionesActivas sesiones)
{
    public async Task<bool> IniciarSesionAsync(string? usuario, string? contrasena)
    {
        if (string.IsNullOrWhiteSpace(usuario) || usuario.Length > 30 || string.IsNullOrEmpty(contrasena) || contrasena.Length > 1024)
            return false;
        var hash = await repositorio.ObtenerHashAsync(usuario.Trim());
        return PasswordHasher.Verificar(contrasena, hash);
    }

    public async Task<string?> CrearSesionAsync(string? usuario, string? contrasena)
    {
        if (string.IsNullOrWhiteSpace(usuario) || usuario.Length > 30 || string.IsNullOrEmpty(contrasena) || contrasena.Length > 1024)
            return null;
        var credencial = await repositorio.ObtenerCredencialAsync(usuario.Trim());
        if (credencial is null || !PasswordHasher.Verificar(contrasena, credencial.Hash)) return null;
        return credencial.CodigoCliente is null ? null : sesiones.Crear(usuario.Trim(), credencial.CodigoCliente);
    }

    public Task<List<Cuenta>> TraerCuentasAsync(string? token)
    {
        if (!sesiones.TryObtener(token, out var sesion) || sesion.CodigoCliente is null)
            throw new UnauthorizedAccessException();
        return repositorio.TraerCuentasAsync(sesion.CodigoCliente);
    }

    public Task<List<Movimiento>> TraerMovimientosAsync(string? token, string? cuenta)
    {
        if (!sesiones.TryObtener(token, out var sesion) || sesion.CodigoCliente is null)
            throw new UnauthorizedAccessException();
        if (string.IsNullOrWhiteSpace(cuenta) || cuenta.Trim().Length > 8)
            return Task.FromResult(new List<Movimiento>());
        return repositorio.TraerMovimientosAsync(cuenta.Trim(), sesion.CodigoCliente);
    }

    public async Task<bool> RegistrarDepositoAsync(string? token, string? cuenta, decimal importe)
    {
        if (!sesiones.TryObtener(token, out var sesion) || sesion.CodigoCliente is null)
            throw new UnauthorizedAccessException();
        if (string.IsNullOrWhiteSpace(cuenta) || cuenta.Trim().Length > 8 || importe <= 0
            || importe > 9_999_999_999.99m || decimal.Round(importe, 2) != importe)
            return false;
        await repositorio.RegistrarDepositoAsync(cuenta.Trim(), importe, sesion.CodigoCliente);
        return true;
    }

    public async Task<bool> TransferirAsync(string? token, string? origen, string? destino,
        decimal importe, string? claveOperacion)
    {
        if (!sesiones.TryObtener(token, out var sesion)) throw new UnauthorizedAccessException();
        if (sesion.CodigoCliente is null
            || !TransferenciaValidator.TryValidate(origen, destino, importe, claveOperacion, out var solicitud))
            return false;
        return await repositorio.TransferirAsync(sesion.Usuario, sesion.CodigoCliente, solicitud);
    }

    public void CerrarSesion(string? token) => sesiones.Revocar(token);
}
