using CoreWCF;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Controladores;

public static class ContratoSoap
{
    public const string EspacioNombres = "http://ws.controladores.monster.edu.ec/";
    public const string NombreServicio = "WSConversionUnidades_Java_SOAP_GR09";
    public const string NombrePuerto = "WSConversionUnidades_Java_SOAP_GR09Port";
}

[ServiceContract(Name = ContratoSoap.NombreServicio, Namespace = ContratoSoap.EspacioNombres)]
public interface IContratoConversorSoap
{
    [OperationContract(Name = "login")]
    [return: MessageParameter(Name = "return")]
    string Login(
        [MessageParameter(Name = "usuario")] string usuario,
        [MessageParameter(Name = "contrasenia")] string contrasenia);

    [OperationContract(Name = "cambiarContrasenia")]
    [return: MessageParameter(Name = "return")]
    string CambiarContrasenia(
        [MessageParameter(Name = "contraseniaActual")] string contraseniaActual,
        [MessageParameter(Name = "contraseniaNueva")] string contraseniaNueva);

    [OperationContract(Name = "convertirLongitud")]
    [return: MessageParameter(Name = "return")]
    double ConvertirLongitud(
        [MessageParameter(Name = "valor")] double valor,
        [MessageParameter(Name = "unidadInicial")] string unidadInicial,
        [MessageParameter(Name = "unidadFinal")] string unidadFinal);

    [OperationContract(Name = "convertirMasa")]
    [return: MessageParameter(Name = "return")]
    double ConvertirMasa(
        [MessageParameter(Name = "valor")] double valor,
        [MessageParameter(Name = "unidadInicial")] string unidadInicial,
        [MessageParameter(Name = "unidadFinal")] string unidadFinal);

    [OperationContract(Name = "convertirTemperatura")]
    [return: MessageParameter(Name = "return")]
    double ConvertirTemperatura(
        [MessageParameter(Name = "valor")] double valor,
        [MessageParameter(Name = "unidadInicial")] string unidadInicial,
        [MessageParameter(Name = "unidadFinal")] string unidadFinal);
}
