using ClienteWebGR09.Models;
using ClienteWebGR09.Services;
using Microsoft.AspNetCore.Mvc;

namespace ClienteWebGR09.Controllers;

public sealed class HomeController(ClienteRest servicio) : Controller
{
    private const string ClaveToken = "Token";
    private const string ClaveUsuario = "Usuario";

    [HttpGet]
    public IActionResult Index() =>
        HttpContext.Session.GetString(ClaveToken) is null
            ? RedirectToAction(nameof(Login))
            : RedirectToAction(nameof(Convertir));

    [HttpGet]
    public IActionResult Login() => View(new LoginViewModel());

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Login(LoginViewModel modelo)
    {
        if (!ModelState.IsValid)
        {
            return View(modelo);
        }

        try
        {
            var sesion = await servicio.IniciarSesionAsync(
                new LoginSolicitud(modelo.Usuario, modelo.Clave));
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
            return RedirectToAction(nameof(Login));
        }

        var modelo = new ConversionViewModel();
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
    public async Task<IActionResult> Convertir(ConversionViewModel modelo)
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        if (token is null)
        {
            return RedirectToAction(nameof(Login));
        }

        try
        {
            await CargarCatalogoAsync(modelo);
            if (!ModelState.IsValid)
            {
                return View(modelo);
            }

            var respuesta = await servicio.ConvertirAsync(
                modelo.Magnitud,
                new ConversionSolicitud(modelo.Valor!.Value, modelo.UnidadOrigen, modelo.UnidadDestino),
                token);
            modelo.Resultado = respuesta.Mensaje;
        }
        catch (ErrorServicioException ex) when (ex.Codigo == StatusCodes.Status401Unauthorized)
        {
            HttpContext.Session.Clear();
            TempData["Mensaje"] = "La sesión expiró. Inicie sesión nuevamente.";
            return RedirectToAction(nameof(Login));
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

        return RedirectToAction(nameof(Login));
    }

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error() =>
        View("Error", new ErrorViewModel { RequestId = HttpContext.TraceIdentifier });

    private async Task CargarCatalogoAsync(ConversionViewModel modelo)
    {
        var catalogo = await servicio.ObtenerCatalogoAsync();
        modelo.Catalogo = catalogo.Magnitudes;
        if (!modelo.Catalogo.ContainsKey(modelo.Magnitud))
        {
            modelo.Magnitud = "longitud";
        }
    }
}
