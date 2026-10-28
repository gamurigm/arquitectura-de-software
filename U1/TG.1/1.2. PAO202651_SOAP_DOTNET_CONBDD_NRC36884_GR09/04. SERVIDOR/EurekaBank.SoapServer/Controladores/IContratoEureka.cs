using CoreWCF;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Controladores;

public static class ContratoEureka
{
    public const string EspacioNombres = "http://ws.monster.edu.ec/";
    public const string NombreServicio = "WSEureka";
    public const string NombrePuerto = "WSEurekaPort";
    public const string PrefijoAccion = EspacioNombres + NombreServicio + "/";
    public const string AccionLogin = PrefijoAccion + "login";
    public const string AccionCrearSesion = PrefijoAccion + "crearSesion";
    public const string AccionTraerCuentas = PrefijoAccion + "traerCuentas";
    public const string AccionTraerMovimientos = PrefijoAccion + "traerMovimientos";
    public const string AccionRegistrarDeposito = PrefijoAccion + "regDeposito";
    public const string AccionTransferir = PrefijoAccion + "transferir";
    public const string AccionCerrarSesion = PrefijoAccion + "cerrarSesion";
}

[ServiceContract(Name = ContratoEureka.NombreServicio, Namespace = ContratoEureka.EspacioNombres)]
public interface IContratoEureka
{
    [OperationContract(Name = "login", Action = ContratoEureka.AccionLogin)]
    [return: MessageParameter(Name = "login")]
    Task<int> LoginAsync(
        [MessageParameter(Name = "usuario")] string usuario,
        [MessageParameter(Name = "contrasena")] string contrasena);

    [OperationContract(Name = "crearSesion", Action = ContratoEureka.AccionCrearSesion)]
    [return: MessageParameter(Name = "token")]
    Task<string?> CrearSesionAsync(
        [MessageParameter(Name = "usuario")] string usuario,
        [MessageParameter(Name = "contrasena")] string contrasena);

    [OperationContract(Name = "traerCuentas", Action = ContratoEureka.AccionTraerCuentas)]
    [return: MessageParameter(Name = "cuenta")]
    Task<List<Cuenta>> TraerCuentasAsync([MessageParameter(Name = "token")] string token);

    [OperationContract(Name = "traerMovimientos", Action = ContratoEureka.AccionTraerMovimientos)]
    [return: MessageParameter(Name = "movimiento")]
    Task<List<Movimiento>> TraerMovimientosAsync([MessageParameter(Name = "token")] string token, [MessageParameter(Name = "cuenta")] string cuenta);

    [OperationContract(Name = "regDeposito", Action = ContratoEureka.AccionRegistrarDeposito)]
    [return: MessageParameter(Name = "resultado")]
    Task<int> RegistrarDepositoAsync(
        [MessageParameter(Name = "token")] string token,
        [MessageParameter(Name = "cuenta")] string cuenta,
        [MessageParameter(Name = "importe")] decimal importe);

    [OperationContract(Name = "transferir", Action = ContratoEureka.AccionTransferir)]
    [return: MessageParameter(Name = "resultado")]
    Task<int> TransferirAsync(
        [MessageParameter(Name = "token")] string token,
        [MessageParameter(Name = "cuentaOrigen")] string cuentaOrigen,
        [MessageParameter(Name = "cuentaDestino")] string cuentaDestino,
        [MessageParameter(Name = "importe")] decimal importe,
        [MessageParameter(Name = "claveOperacion")] string claveOperacion);

    [OperationContract(Name = "cerrarSesion", Action = ContratoEureka.AccionCerrarSesion)]
    [return: MessageParameter(Name = "resultado")]
    Task<int> CerrarSesionAsync([MessageParameter(Name = "token")] string token);
}
