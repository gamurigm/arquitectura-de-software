using EurekaBank.Cliente.Modelos;

namespace EurekaBank.Cliente.Servicios;

public interface IEurekaBankService
{
    Task<LoginResponse> IniciarSesionAsync(string usuario, string contrasena, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<CuentaDto>> ConsultarCuentasAsync(string token, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<MovimientoDto>> ConsultarMovimientosAsync(string cuenta, string token, CancellationToken cancellationToken = default);
    Task<bool> RegistrarDepositoAsync(string cuenta, decimal importe, string token, CancellationToken cancellationToken = default);
    Task<bool> TransferirAsync(string cuentaOrigen, string cuentaDestino, decimal importe, Guid claveOperacion, string token, CancellationToken cancellationToken = default);
    Task CerrarSesionAsync(string? token, CancellationToken cancellationToken = default);
}
