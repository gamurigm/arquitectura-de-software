using ClienteConsolaGR09.Modelos;
using ClienteConsolaGR09.Servicios;
using ClienteConsolaGR09.Vistas;

namespace ClienteConsolaGR09.Controladores;

public sealed class ControladorConsola(ClienteRest servicio, VistaConsola vista)
{
    private readonly EstadoCliente _estado = new();

    public async Task EjecutarAsync()
    {
        vista.MostrarInicio("Conectando con http://127.0.0.1:19082");
        while (_estado.Token is null)
        {
            try
            {
                var respuesta = await servicio.IniciarSesionAsync(vista.SolicitarCredenciales());
                _estado.Token = respuesta.Token;
                _estado.Usuario = respuesta.Usuario;
                vista.MostrarResultado($"Sesión iniciada. Token válido por {respuesta.ExpiraEnSegundos} segundos.");
            }
            catch (InvalidOperationException ex)
            {
                vista.MostrarError(ex.Message);
            }
        }

        var salir = false;
        while (!salir)
        {
            switch (vista.MostrarMenu())
            {
                case "1":
                    await ConvertirAsync();
                    break;
                case "2":
                    await MostrarCatalogoAsync();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    vista.MostrarError("Seleccione 1, 2 o 0.");
                    break;
            }
        }

        await CerrarSesionAsync();
    }

    private async Task ConvertirAsync()
    {
        try
        {
            var catalogo = await servicio.ObtenerCatalogoAsync();
            var solicitud = vista.SolicitarConversion(catalogo, out var magnitud);
            if (solicitud is null)
            {
                vista.MostrarError("Datos de conversión inválidos.");
                return;
            }

            var resultado = await servicio.ConvertirAsync(magnitud, solicitud, _estado.Token!);
            if (!double.IsFinite(resultado.Resultado))
            {
                throw new InvalidOperationException("El servidor devolvió un resultado inválido.");
            }
            vista.MostrarResultado(resultado.Mensaje);
        }
        catch (InvalidOperationException ex)
        {
            vista.MostrarError(ex.Message);
        }
    }

    private async Task MostrarCatalogoAsync()
    {
        try
        {
            vista.MostrarCatalogo(await servicio.ObtenerCatalogoAsync());
        }
        catch (InvalidOperationException ex)
        {
            vista.MostrarError(ex.Message);
        }
    }

    private async Task CerrarSesionAsync()
    {
        if (_estado.Token is null)
        {
            return;
        }

        try
        {
            var respuesta = await servicio.CerrarSesionAsync(_estado.Token);
            vista.MostrarResultado(respuesta.Mensaje);
        }
        catch (InvalidOperationException ex)
        {
            vista.MostrarError(ex.Message);
        }
    }
}
