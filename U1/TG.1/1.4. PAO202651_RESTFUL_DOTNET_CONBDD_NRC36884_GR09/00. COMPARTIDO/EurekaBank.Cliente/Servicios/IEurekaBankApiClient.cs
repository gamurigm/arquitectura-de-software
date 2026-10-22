using EurekaBank.Cliente.Modelos;

namespace EurekaBank.Cliente.Servicios;

public interface IEurekaBankApiClient
{
    Task<LoginResponse> LoginAsync(LoginRequest request, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<MovimientoDto>> ConsultarMovimientosAsync(string cuenta, string? token = null, CancellationToken cancellationToken = default);
    Task<DepositoResponse> DepositarAsync(DepositoRequest request, string? token = null, CancellationToken cancellationToken = default);
    Task<TransferenciaResponse> TransferirAsync(TransferenciaRequest request, string token, CancellationToken cancellationToken = default);
    Task CerrarSesionAsync(string? token, CancellationToken cancellationToken = default);
}
