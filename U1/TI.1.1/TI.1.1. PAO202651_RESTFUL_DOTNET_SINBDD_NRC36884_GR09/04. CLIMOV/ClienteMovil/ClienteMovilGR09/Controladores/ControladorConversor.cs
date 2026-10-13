using ClienteMovilGR09.Modelos;
using ClienteMovilGR09.Servicios;

namespace ClienteMovilGR09.Controladores;

public sealed class ControladorConversor(ClienteRest servicio)
{
    private readonly EstadoCliente _estado = new();

    public async Task<TokenRespuesta> IniciarSesionAsync(string usuario, string clave)
    {
        var respuesta = await servicio.IniciarSesionAsync(new LoginSolicitud(usuario, clave));
        _estado.Token = respuesta.Token;
        _estado.Usuario = respuesta.Usuario;
        return respuesta;
    }

    public Task<CatalogoRespuesta> ObtenerCatalogoAsync() => servicio.ObtenerCatalogoAsync();

    public Task<ConversionRespuesta> ConvertirAsync(
        string magnitud,
        double valor,
        string origen,
        string destino) =>
        servicio.ConvertirAsync(
            magnitud,
            new ConversionSolicitud(valor, origen, destino),
            _estado.Token ?? throw new InvalidOperationException("Inicie sesión antes de convertir."));

    public async Task CerrarSesionAsync()
    {
        if (_estado.Token is null)
        {
            return;
        }

        var token = _estado.Token;
        _estado.Token = null;
        await servicio.CerrarSesionAsync(token);
    }
}
