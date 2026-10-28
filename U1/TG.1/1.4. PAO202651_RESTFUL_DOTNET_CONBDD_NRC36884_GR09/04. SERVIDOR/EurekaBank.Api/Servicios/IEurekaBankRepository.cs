using EurekaBank.Api.Modelos;

namespace EurekaBank.Api.Servicios;

public interface IEurekaBankRepository
{
    Task<CredencialUsuario?> ObtenerCredencialAsync(string usuario, CancellationToken cancellationToken);
    Task<IReadOnlyList<CuentaResponse>> ConsultarCuentasAsync(string codigoCliente, CancellationToken cancellationToken);
    Task<IReadOnlyList<MovimientoResponse>> ConsultarMovimientosAsync(string cuenta, string codigoCliente, CancellationToken cancellationToken);
    Task<bool> RegistrarDepositoAsync(string cuenta, decimal importe, string codigoCliente, CancellationToken cancellationToken);
    Task<bool> TransferirAsync(string usuario, string codigoCliente, TransferenciaRequest solicitud, CancellationToken cancellationToken);
}
