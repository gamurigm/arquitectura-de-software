using ClienteMovilGR09.Modelos;
using ClienteMovilGR09.Servicios;

namespace ClienteMovilGR09.Controladores;

public sealed class ControladorConversor
{
    private const string RutaServicio = "/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09";
    private readonly ClienteSoap _servicio = new(ObtenerEndpoint());
    private readonly ModeloSesion _sesion = new();

    public async Task IniciarSesionAsync(string usuario, string contrasenia)
    {
        var token = await _servicio.LoginAsync(usuario, contrasenia);
        _sesion.Iniciar(token);
    }

    public Task<double> ConvertirAsync(TipoConversion tipo, double valor, string inicial, string final)
    {
        if (!_sesion.Autenticado)
        {
            throw new InvalidOperationException("Inicia sesión antes de convertir.");
        }

        return _servicio.ConvertirAsync(_sesion.Token!, tipo, valor, inicial, final);
    }

    public Task<string> CambiarContraseniaAsync(string actual, string nueva)
    {
        if (!_sesion.Autenticado)
        {
            throw new InvalidOperationException("La sesión no está activa.");
        }

        return _servicio.CambiarContraseniaAsync(_sesion.Token!, actual, nueva);
    }

    public void CerrarSesion() => _sesion.Cerrar();

    private static string ObtenerEndpoint()
    {
        var configurado = Environment.GetEnvironmentVariable("CONVERSOR_SOAP_URL");
        if (!string.IsNullOrWhiteSpace(configurado))
        {
            return configurado;
        }

        var host = DeviceInfo.Platform == DevicePlatform.Android ? "10.0.2.2" : "127.0.0.1";
        return $"http://{host}:19081{RutaServicio}";
    }
}
