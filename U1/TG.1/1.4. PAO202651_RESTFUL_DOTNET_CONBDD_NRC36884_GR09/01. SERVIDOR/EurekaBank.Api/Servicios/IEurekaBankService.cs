using EurekaBank.Api.Modelos;

namespace EurekaBank.Api.Servicios;

public interface IEurekaBankService
{
    Task<LoginResponse> IniciarSesionAsync(string usuario, string contrasena, CancellationToken cancellationToken);
    Task<IReadOnlyList<CuentaResponse>?> ConsultarCuentasAsync(string? token, CancellationToken cancellationToken);
    Task<IReadOnlyList<MovimientoResponse>?> ConsultarMovimientosAsync(string? token, string cuenta, CancellationToken cancellationToken);
    Task<bool?> RegistrarDepositoAsync(string? token, string cuenta, decimal importe, CancellationToken cancellationToken);
    Task<TransferenciaResponse?> TransferirAsync(string? token, TransferenciaRequest solicitud, CancellationToken cancellationToken);
    void CerrarSesion(string? token);
}
