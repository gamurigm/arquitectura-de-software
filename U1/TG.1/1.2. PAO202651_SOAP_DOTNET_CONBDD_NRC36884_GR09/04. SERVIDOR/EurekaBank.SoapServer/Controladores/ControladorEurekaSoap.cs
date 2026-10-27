using CoreWCF;
using ec.edu.monster.Servicios;

namespace ec.edu.monster.Controladores;

[ServiceBehavior(Name = ContratoEureka.NombreServicio, Namespace = ContratoEureka.EspacioNombres, InstanceContextMode = InstanceContextMode.PerCall)]
public sealed class ControladorEurekaSoap(EurekaBankService servicio, ILogger<ControladorEurekaSoap> logger) : IContratoEureka
{
    private readonly EurekaBankService _servicio = servicio;
    private readonly ILogger<ControladorEurekaSoap> _logger = logger;

    public async Task<int> LoginAsync(string usuario, string contrasena)
    {
        try { return await _servicio.IniciarSesionAsync(usuario, contrasena) ? 1 : -1; }
        catch (Exception excepcion) { _logger.LogError(excepcion, "Falló la operación login."); return -1; }
    }

    public async Task<string?> CrearSesionAsync(string usuario, string contrasena)
    {
        try { return await _servicio.CrearSesionAsync(usuario, contrasena); }
        catch (Exception excepcion) { _logger.LogError(excepcion, "Falló la operación crearSesion."); return null; }
    }

    public async Task<List<ec.edu.monster.Modelos.Cuenta>> TraerCuentasAsync(string token)
    {
        try { return await _servicio.TraerCuentasAsync(token); }
        catch (UnauthorizedAccessException) { throw new FaultException("Token ausente, inválido o caducado.", new FaultCode("Sender")); }
        catch (Exception excepcion)
        {
            _logger.LogError(excepcion, "Falló la operación traerCuentas.");
            throw new FaultException("No se pudieron consultar las cuentas.", new FaultCode("Server"));
        }
    }

    public async Task<List<ec.edu.monster.Modelos.Movimiento>> TraerMovimientosAsync(string token, string cuenta)
    {
        try { return await _servicio.TraerMovimientosAsync(token, cuenta); }
        catch (UnauthorizedAccessException) { throw new FaultException("Token ausente, inválido o caducado.", new FaultCode("Sender")); }
        catch (Exception excepcion)
        {
            _logger.LogError(excepcion, "Falló la operación traerMovimientos.");
            throw new FaultException("No se pudieron consultar los movimientos.", new FaultCode("Server"));
        }
    }

    public async Task<int> RegistrarDepositoAsync(string token, string cuenta, decimal importe)
    {
        try { return await _servicio.RegistrarDepositoAsync(token, cuenta, importe) ? 1 : -1; }
        catch (UnauthorizedAccessException) { throw new FaultException("Token ausente, inválido o caducado.", new FaultCode("Sender")); }
        catch (Exception excepcion) { _logger.LogError(excepcion, "Falló la operación regDeposito."); return -1; }
    }

    public async Task<int> TransferirAsync(string token, string cuentaOrigen, string cuentaDestino,
        decimal importe, string claveOperacion)
    {
        try { return await _servicio.TransferirAsync(token, cuentaOrigen, cuentaDestino, importe, claveOperacion) ? 1 : -1; }
        catch (UnauthorizedAccessException) { throw new FaultException("Token ausente, inválido o caducado.", new FaultCode("Sender")); }
        catch (Exception excepcion) { _logger.LogError(excepcion, "Falló la operación transferir."); return -1; }
    }

    public Task<int> CerrarSesionAsync(string token)
    {
        _servicio.CerrarSesion(token);
        return Task.FromResult(1);
    }
}
