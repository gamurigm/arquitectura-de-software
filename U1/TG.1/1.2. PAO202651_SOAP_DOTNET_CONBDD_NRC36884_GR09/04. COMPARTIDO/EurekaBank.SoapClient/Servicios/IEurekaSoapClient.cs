using ec.edu.monster.Modelos;

namespace ec.edu.monster.Servicios;

public interface IEurekaSoapClient
{
    Task<int> LoginAsync(string usuario, string contrasena, CancellationToken cancellationToken = default);
    Task<string?> CrearSesionAsync(string usuario, string contrasena, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<Cuenta>> TraerCuentasAsync(string token, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<Movimiento>> TraerMovimientosAsync(string token, string cuenta, CancellationToken cancellationToken = default);
    Task<int> RegistrarDepositoAsync(string token, string cuenta, decimal importe, CancellationToken cancellationToken = default);
    Task<int> TransferirAsync(string token, string origen, string destino, decimal importe, Guid claveOperacion, CancellationToken cancellationToken = default);
    Task<int> CerrarSesionAsync(string token, CancellationToken cancellationToken = default);
}
