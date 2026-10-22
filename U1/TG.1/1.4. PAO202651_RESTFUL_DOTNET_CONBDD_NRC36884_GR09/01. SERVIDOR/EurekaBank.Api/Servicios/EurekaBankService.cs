using EurekaBank.Api.Modelos;
using EurekaBank.Api.Seguridad;

namespace EurekaBank.Api.Servicios;

public sealed class EurekaBankService(IEurekaBankRepository repositorio, ISecretHasher hasher, SesionesActivas sesiones) : IEurekaBankService
{
    public async Task<LoginResponse> IniciarSesionAsync(
        string usuario,
        string contrasena,
        CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(usuario) || usuario.Length > 30
            || string.IsNullOrEmpty(contrasena) || contrasena.Length > 1024)
        {
            return new LoginResponse(false, null);
        }

        var credencial = await repositorio.ObtenerCredencialAsync(usuario.Trim(), cancellationToken);
        if (credencial is null || !hasher.Verify(credencial.Hash, contrasena))
            return new LoginResponse(false, null);
        return new LoginResponse(true, sesiones.Crear(usuario.Trim(), credencial.CodigoCliente));
    }

    public Task<IReadOnlyList<MovimientoResponse>> ConsultarMovimientosAsync(
        string cuenta,
        CancellationToken cancellationToken)
    {
        var cuentaNormalizada = NormalizarCuenta(cuenta);
        return cuentaNormalizada is null
            ? Task.FromResult<IReadOnlyList<MovimientoResponse>>(Array.Empty<MovimientoResponse>())
            : repositorio.ConsultarMovimientosAsync(cuentaNormalizada, cancellationToken);
    }

    public Task<bool> RegistrarDepositoAsync(
        string cuenta,
        decimal importe,
        CancellationToken cancellationToken)
    {
        var bits = decimal.GetBits(importe);
        var escala = (bits[3] >> 16) & 0x7F;
        var cuentaNormalizada = NormalizarCuenta(cuenta);
        if (cuentaNormalizada is null || importe <= 0m || importe > 9_999_999_999.99m || escala > 2)
        {
            return Task.FromResult(false);
        }

        return repositorio.RegistrarDepositoAsync(cuentaNormalizada, importe, cancellationToken);
    }

    public async Task<TransferenciaResponse?> TransferirAsync(
        string? token,
        TransferenciaRequest solicitud,
        CancellationToken cancellationToken)
    {
        if (!sesiones.TryObtener(token, out var sesion)) return null;
        if (sesion.CodigoCliente is null) return new TransferenciaResponse(false);
        if (!TransferenciaValidator.TryValidate(solicitud, out var normalizada))
            return new TransferenciaResponse(false);
        return new TransferenciaResponse(await repositorio.TransferirAsync(
            sesion.Usuario, sesion.CodigoCliente, normalizada, cancellationToken));
    }

    public void CerrarSesion(string? token) => sesiones.Revocar(token);

    private static string? NormalizarCuenta(string? cuenta)
    {
        var valor = cuenta?.Trim();
        return string.IsNullOrEmpty(valor) || valor.Length > 8 ? null : valor;
    }
}
