using CoreWCF;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Controladores;

public static class ContratoEureka
{
    public const string EspacioNombres = "http://ws.monster.edu.ec/";
    public const string NombreServicio = "WSEureka";
    public const string NombrePuerto = "WSEurekaPort";
}

[ServiceContract(Name = ContratoEureka.NombreServicio, Namespace = ContratoEureka.EspacioNombres)]
public interface IContratoEureka
{
    [OperationContract(Name = "login", Action = "")]
    [return: MessageParameter(Name = "login")]
    Task<int> LoginAsync(
        [MessageParameter(Name = "usuario")] string usuario,
        [MessageParameter(Name = "contrasena")] string contrasena);

    [OperationContract(Name = "crearSesion", Action = "")]
    [return: MessageParameter(Name = "token")]
    Task<string?> CrearSesionAsync(
        [MessageParameter(Name = "usuario")] string usuario,
        [MessageParameter(Name = "contrasena")] string contrasena);

    [OperationContract(Name = "traerMovimientos", Action = "")]
    [return: MessageParameter(Name = "movimiento")]
    Task<List<Movimiento>> TraerMovimientosAsync([MessageParameter(Name = "cuenta")] string cuenta);

    [OperationContract(Name = "regDeposito", Action = "")]
    [return: MessageParameter(Name = "resultado")]
    Task<int> RegistrarDepositoAsync(
        [MessageParameter(Name = "cuenta")] string cuenta,
        [MessageParameter(Name = "importe")] decimal importe);

    [OperationContract(Name = "transferir", Action = "")]
    [return: MessageParameter(Name = "resultado")]
    Task<int> TransferirAsync(
        [MessageParameter(Name = "token")] string token,
        [MessageParameter(Name = "cuentaOrigen")] string cuentaOrigen,
        [MessageParameter(Name = "cuentaDestino")] string cuentaDestino,
        [MessageParameter(Name = "importe")] decimal importe,
        [MessageParameter(Name = "claveOperacion")] string claveOperacion);

    [OperationContract(Name = "cerrarSesion", Action = "")]
    [return: MessageParameter(Name = "resultado")]
    Task<int> CerrarSesionAsync([MessageParameter(Name = "token")] string token);
}
