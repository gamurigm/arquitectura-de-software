using ClienteConsolaGR09.Modelos;
using ClienteConsolaGR09.Servicios;
using ClienteConsolaGR09.Vistas;

namespace ClienteConsolaGR09.Controladores;

public sealed class ControladorConsola
{
    private readonly ClienteSOAP _servicio = new();
    private readonly ModeloSesion _sesion = new();
    private readonly VistaConsola _vista = new();

    public async Task EjecutarAsync()
    {
        _vista.MostrarInicio();
        while (!_sesion.Autenticado)
        {
            try
            {
                var usuario = _vista.LeerTexto("Usuario [Monster]: ");
                if (usuario.Length == 0) usuario = "Monster";
                var contrasenia = _vista.LeerContrasenia("Contraseña [Monster9]: ", "Monster9");
                if (string.IsNullOrWhiteSpace(usuario))
                {
                    _vista.MostrarError("El usuario no puede quedar vacío.");
                    continue;
                }
                var token = await _servicio.IniciarSesionAsync(usuario, contrasenia);
                if (!Guid.TryParse(token, out _))
                {
                    throw new InvalidOperationException("El servidor rechazó las credenciales.");
                }

                _sesion.Iniciar(token);
                _vista.MostrarMensaje("Sesión iniciada.");
            }
            catch (Exception excepcion)
            {
                _vista.MostrarError(excepcion.Message);
                _vista.MostrarMensaje("Inicio de sesión fallido. Vuelve a ingresar tus credenciales.");
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
                    if (tipo == TipoConversion.Masa && valor < 0)
                    {
                        _vista.MostrarError("La masa no puede ser negativa.");
                        continue;
                    }
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
