using ClienteConsolaGR09.Modelos;
using ClienteConsolaGR09.Servicios;
using ClienteConsolaGR09.Vistas;

namespace ClienteConsolaGR09.Controladores;

public sealed class ControladorConsola
{
    private readonly ClienteSoap _servicio = new();
    private readonly ModeloSesion _sesion = new();
    private readonly VistaConsola _vista = new();

    public async Task EjecutarAsync()
    {
        _vista.MostrarInicio();
        while (!_sesion.Autenticado)
        {
            try
            {
                var usuario = _vista.LeerTexto("Usuario: ");
                var contrasenia = _vista.LeerContrasenia("Contraseña: ");
                _sesion.Iniciar(await _servicio.LoginAsync(usuario, contrasenia));
                _vista.MostrarMensaje("Sesión iniciada.");
            }
            catch (Exception excepcion)
            {
                _vista.MostrarError(excepcion.Message);
            }
        }

        while (true)
        {
            var opcion = _vista.LeerOpcion();
            if (opcion == 0)
            {
                _sesion.Cerrar();
                _vista.MostrarMensaje("Sesión cerrada.");
                return;
            }

            try
            {
                if (opcion is >= 1 and <= 3)
                {
                    var tipo = (TipoConversion)(opcion - 1);
                    var unidades = CatalogoUnidades.Obtener(tipo);
                    var valor = _vista.LeerNumero("Valor: ");
                    var inicial = _vista.LeerUnidad("Unidad inicial", unidades);
                    var final = _vista.LeerUnidad("Unidad final", unidades);
                    var resultado = await _servicio.ConvertirAsync(
                        _sesion.Token!, tipo, valor, inicial, final);
                    _vista.MostrarResultado(resultado, final);
                }
                else if (opcion == 4)
                {
                    var actual = _vista.LeerContrasenia("Contraseña actual: ");
                    var nueva = _vista.LeerContrasenia("Contraseña nueva: ");
                    _vista.MostrarMensaje(await _servicio.CambiarContraseniaAsync(_sesion.Token!, actual, nueva));
                }
                else
                {
                    _vista.MostrarError("Opción no válida.");
                }
            }
            catch (Exception excepcion)
            {
                _vista.MostrarError(excepcion.Message);
            }
        }
    }
}
