using ClienteWebGR09.Modelos;
using ClienteWebGR09.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace ClienteWebGR09.Controladores;

[Controller]
public sealed class ControladorInicio(ClienteRest servicio) : Controller
{
    private const string ClaveToken = "Token";
    private const string ClaveUsuario = "Usuario";

    [HttpGet]
    public IActionResult Inicio() =>
        HttpContext.Session.GetString(ClaveToken) is null
            ? RedirectToAction(nameof(IniciarSesion))
            : RedirectToAction(nameof(Convertir));

    [HttpGet]
    public IActionResult IniciarSesion() => View(new ModeloInicioSesion());

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> IniciarSesion(ModeloInicioSesion modelo)
    {
        if (!ModelState.IsValid)
        {
            return View(modelo);
        }

        try
        {
            var sesion = await servicio.IniciarSesionAsync(
                new SolicitudInicioSesion(modelo.Usuario, modelo.Clave));
            HttpContext.Session.SetString(ClaveToken, sesion.Token);
            HttpContext.Session.SetString(ClaveUsuario, sesion.Usuario);
            return RedirectToAction(nameof(Convertir));
        }
        catch (InvalidOperationException ex)
        {
            ModelState.AddModelError("", ex.Message);
            return View(modelo);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Convertir()
    {
        if (HttpContext.Session.GetString(ClaveToken) is null)
        {
            return RedirectToAction(nameof(IniciarSesion));
        }

        var modelo = new ModeloConversion();
        try
        {
            await CargarCatalogoAsync(modelo);
            ViewBag.Usuario = HttpContext.Session.GetString(ClaveUsuario);
            return View(modelo);
        }
        catch (InvalidOperationException ex)
        {
            modelo.MensajeError = ex.Message;
            return View(modelo);
        }
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Convertir(ModeloConversion modelo)
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        if (token is null)
        {
            return RedirectToAction(nameof(IniciarSesion));
        }

        try
        {
            await CargarCatalogoAsync(modelo);
            if (string.IsNullOrWhiteSpace(modelo.Magnitud)
                || !modelo.Catalogo.TryGetValue(modelo.Magnitud, out var unidades))
            {
                ModelState.AddModelError(nameof(modelo.Magnitud), "Seleccione una magnitud válida.");
                modelo.Magnitud = "longitud";
            }
            else if (!unidades.Contains(modelo.UnidadOrigen, StringComparer.OrdinalIgnoreCase)
                || !unidades.Contains(modelo.UnidadDestino, StringComparer.OrdinalIgnoreCase))
            {
                ModelState.AddModelError(nameof(modelo.UnidadOrigen), "Seleccione unidades válidas para la magnitud.");
            }
            if (modelo.Valor is not double valor || !double.IsFinite(valor))
            {
                ModelState.AddModelError(nameof(modelo.Valor), "Ingrese un número finito.");
            }
            else if (string.Equals(modelo.Magnitud, "masa", StringComparison.OrdinalIgnoreCase) && valor < 0)
            {
                ModelState.AddModelError(nameof(modelo.Valor), "La masa no puede ser negativa.");
            }
            if (!ModelState.IsValid)
            {
                return View(modelo);
            }

            var respuesta = await servicio.ConvertirAsync(
                modelo.Magnitud,
                new ConversionSolicitud(modelo.Valor!.Value, modelo.UnidadOrigen, modelo.UnidadDestino),
                token);
            if (!double.IsFinite(respuesta.Resultado))
            {
                throw new InvalidOperationException("El servidor devolvió un resultado inválido.");
            }
            modelo.Resultado = respuesta.Mensaje;
        }
        catch (ExcepcionServicio ex) when (ex.Codigo == StatusCodes.Status401Unauthorized)
        {
            HttpContext.Session.Clear();
            TempData["Mensaje"] = "La sesión expiró. Inicie sesión nuevamente.";
            return RedirectToAction(nameof(IniciarSesion));
        }
        catch (InvalidOperationException ex)
        {
            modelo.MensajeError = ex.Message;
        }

        ViewBag.Usuario = HttpContext.Session.GetString(ClaveUsuario);
        return View(modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Salir()
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        HttpContext.Session.Clear();
        if (token is not null)
        {
            try
            {
                await servicio.CerrarSesionAsync(token);
            }
            catch (InvalidOperationException)
            {
                // La sesión local queda cerrada aunque el servidor ya haya vencido el token.
            }
        }

        return RedirectToAction(nameof(IniciarSesion));
    }

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error() =>
        View("Error", new ModeloError { IdSolicitud = HttpContext.TraceIdentifier });

    private async Task CargarCatalogoAsync(ModeloConversion modelo)
    {
        var catalogo = await servicio.ObtenerCatalogoAsync();
        modelo.Catalogo = catalogo.Magnitudes;
    }
}
