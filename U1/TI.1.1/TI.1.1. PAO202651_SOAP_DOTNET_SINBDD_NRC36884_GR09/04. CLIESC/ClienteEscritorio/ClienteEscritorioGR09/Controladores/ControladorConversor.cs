using ClienteEscritorioGR09.Modelos;
using ClienteEscritorioGR09.Servicios;

namespace ClienteEscritorioGR09.Controladores;

public sealed class ControladorConversor
{
    private readonly ClienteSOAP _servicio = new();
    private readonly ModeloSesion _sesion = new();

    public async Task IniciarSesionAsync(string usuario, string contrasenia)
    {
        var token = await _servicio.IniciarSesionAsync(usuario, contrasenia);
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
}
