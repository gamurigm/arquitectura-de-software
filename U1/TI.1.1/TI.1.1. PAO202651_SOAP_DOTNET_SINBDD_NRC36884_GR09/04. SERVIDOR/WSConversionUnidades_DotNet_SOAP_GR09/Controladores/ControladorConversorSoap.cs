using CoreWCF;
using WSConversionUnidades_DotNet_SOAP_GR09.Seguridad;
using WSConversionUnidades_DotNet_SOAP_GR09.Servicios;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Controladores;

[ServiceBehavior(
    Name = ContratoSoap.NombreServicio,
    Namespace = ContratoSoap.EspacioNombres,
    InstanceContextMode = InstanceContextMode.PerCall)]
public sealed class ControladorConversorSoap : IContratoConversorSoap
{
    private readonly ServicioAutenticacion _autenticacion;
    private readonly ServicioConversor _conversor;

    public ControladorConversorSoap(ServicioAutenticacion autenticacion, ServicioConversor conversor)
    {
        _autenticacion = autenticacion;
        _conversor = conversor;
    }

    public string Login(string usuario, string contrasenia) => Ejecutar(() => _autenticacion.IniciarSesion(usuario, contrasenia));

    public string CambiarContrasenia(string contraseniaActual, string contraseniaNueva)
    {
        ExigirToken();
        return Ejecutar(() => _autenticacion.CambiarContrasenia(contraseniaActual, contraseniaNueva));
    }

    public double ConvertirLongitud(double valor, string unidadInicial, string unidadFinal)
    {
        ExigirToken();
        return Ejecutar(() => _conversor.ConvertirLongitud(valor, unidadInicial, unidadFinal));
    }

    public double ConvertirMasa(double valor, string unidadInicial, string unidadFinal)
    {
        ExigirToken();
        return Ejecutar(() => _conversor.ConvertirMasa(valor, unidadInicial, unidadFinal));
    }

    public double ConvertirTemperatura(double valor, string unidadInicial, string unidadFinal)
    {
        ExigirToken();
        return Ejecutar(() => _conversor.ConvertirTemperatura(valor, unidadInicial, unidadFinal));
    }

    private void ExigirToken()
    {
        try
        {
            var encabezados = OperationContext.Current.IncomingMessageHeaders;
            var indice = encabezados.FindHeader("token", ContratoSoap.EspacioNombres);
            if (indice < 0)
            {
                throw new FaultException("Acceso denegado: no se encontró el token. Inicie sesión primero.");
            }

            using var lector = encabezados.GetReaderAtHeader(indice);
            lector.MoveToContent();
            var token = lector.ReadElementContentAsString().Trim();
            if (!_autenticacion.ValidarToken(token))
            {
                throw new FaultException("Acceso denegado: token inválido.");
            }
        }
        catch (FaultException)
        {
            throw;
        }
        catch (Exception)
        {
            throw new FaultException("No se pudo validar el token SOAP.");
        }
    }

    private static T Ejecutar<T>(Func<T> operacion)
    {
        try
        {
            return operacion();
        }
        catch (ArgumentException excepcion)
        {
            throw new FaultException(excepcion.Message);
        }
        catch (InvalidOperationException excepcion)
        {
            throw new FaultException(excepcion.Message);
        }
    }
}
