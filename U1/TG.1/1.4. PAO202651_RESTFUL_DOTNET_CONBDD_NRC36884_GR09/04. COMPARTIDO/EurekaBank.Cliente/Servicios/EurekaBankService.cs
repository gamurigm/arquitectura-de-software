using EurekaBank.Cliente.Modelos;

namespace EurekaBank.Cliente.Servicios;

public sealed class EurekaBankService(IEurekaBankApiClient apiClient) : IEurekaBankService
{
    public Task<LoginResponse> IniciarSesionAsync(
        string usuario,
        string contrasena,
        CancellationToken cancellationToken = default) =>
        apiClient.LoginAsync(new LoginRequest(usuario, contrasena), cancellationToken);

    public Task<IReadOnlyList<CuentaDto>> ConsultarCuentasAsync(string token, CancellationToken cancellationToken = default) =>
        apiClient.ConsultarCuentasAsync(token, cancellationToken);

    public Task<IReadOnlyList<MovimientoDto>> ConsultarMovimientosAsync(
        string cuenta,
        string? token = null,
        CancellationToken cancellationToken = default) =>
        apiClient.ConsultarMovimientosAsync(cuenta, token, cancellationToken);

    public async Task<bool> RegistrarDepositoAsync(
        string cuenta,
        decimal importe,
        string? token = null,
        CancellationToken cancellationToken = default) =>
        (await apiClient.DepositarAsync(new DepositoRequest(cuenta, importe), token, cancellationToken)).Registrado;

    public async Task<bool> TransferirAsync(
        string cuentaOrigen, string cuentaDestino, decimal importe, Guid claveOperacion, string token,
        CancellationToken cancellationToken = default) =>
        (await apiClient.TransferirAsync(
            new TransferenciaRequest(cuentaOrigen, cuentaDestino, importe, claveOperacion), token, cancellationToken)).Registrada;

    public Task CerrarSesionAsync(string? token, CancellationToken cancellationToken = default) =>
        apiClient.CerrarSesionAsync(token, cancellationToken);
}
