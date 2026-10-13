using System.Globalization;
using System.Text.Json;
using ClienteWebGR09.Models;
using ClienteWebGR09.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;

namespace ClienteWebGR09.Controllers;

public sealed class HomeController : Controller
{
    private const string ClaveToken = "token";
    private readonly ClienteSoap _servicio;

    public HomeController(ClienteSoap servicio)
    {
        _servicio = servicio;
    }

    [HttpGet]
    public IActionResult Index()
    {
        var modelo = new ConversionViewModel();
        PrepararVista(modelo.Tipo);
        return View(modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> IniciarSesion(LoginViewModel modelo)
    {
        if (!ModelState.IsValid)
        {
            TempData["Error"] = "Completa usuario y contraseña.";
            return RedirectToAction(nameof(Index));
        }

        try
        {
            var token = await _servicio.LoginAsync(modelo.Usuario.Trim(), modelo.Contrasenia);
            HttpContext.Session.SetString(ClaveToken, token);
            TempData["Mensaje"] = "Sesión iniciada.";
        }
        catch (Exception excepcion)
        {
            TempData["Error"] = excepcion.Message;
        }

        return RedirectToAction(nameof(Index));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Convertir(ConversionViewModel modelo)
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        if (string.IsNullOrWhiteSpace(token))
        {
            TempData["Error"] = "Inicia sesión antes de convertir.";
            return RedirectToAction(nameof(Index));
        }

        if (!double.TryParse(modelo.Valor.Trim().Replace(',', '.'), NumberStyles.Float,
                CultureInfo.InvariantCulture, out var valor)
            || !double.IsFinite(valor))
        {
            ModelState.AddModelError(nameof(modelo.Valor), "Ingresa un número finito.");
        }

        if (!ModelState.IsValid)
        {
            PrepararVista(modelo.Tipo);
            return View(nameof(Index), modelo);
        }

        try
        {
            var resultado = await _servicio.ConvertirAsync(
                token, modelo.Tipo, valor, modelo.UnidadInicial, modelo.UnidadFinal);
            modelo.Resultado = $"{resultado.ToString("G", CultureInfo.CurrentCulture)} {modelo.UnidadFinal}";
            PrepararVista(modelo.Tipo);
            return View(nameof(Index), modelo);
        }
        catch (Exception excepcion)
        {
            TempData["Error"] = excepcion.Message;
            return RedirectToAction(nameof(Index));
        }
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> CambiarContrasenia(CambiarContraseniaViewModel modelo)
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        if (string.IsNullOrWhiteSpace(token))
        {
            TempData["Error"] = "Inicia sesión antes de cambiar la contraseña.";
            return RedirectToAction(nameof(Index));
        }

        if (!ModelState.IsValid)
        {
            TempData["Error"] = "Completa ambas contraseñas.";
            return RedirectToAction(nameof(Index));
        }

        try
        {
            TempData["Mensaje"] = await _servicio.CambiarContraseniaAsync(
                token, modelo.ContraseniaActual, modelo.ContraseniaNueva);
        }
        catch (Exception excepcion)
        {
            TempData["Error"] = excepcion.Message;
        }

        return RedirectToAction(nameof(Index));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public IActionResult CerrarSesion()
    {
        HttpContext.Session.Remove(ClaveToken);
        TempData["Mensaje"] = "Sesión cerrada.";
        return RedirectToAction(nameof(Index));
    }

    private void PrepararVista(TipoConversion tipo)
    {
        ViewBag.Autenticado = !string.IsNullOrWhiteSpace(HttpContext.Session.GetString(ClaveToken));
        ViewBag.Unidades = new SelectList(
            CatalogoUnidades.Obtener(tipo),
            nameof(OpcionUnidad.Codigo),
            nameof(OpcionUnidad.Etiqueta));
        ViewBag.CatalogoJson = JsonSerializer.Serialize(
            Enum.GetValues<TipoConversion>().ToDictionary(
                magnitud => magnitud.ToString(),
                magnitud => CatalogoUnidades.Obtener(magnitud)),
            new JsonSerializerOptions(JsonSerializerDefaults.Web));
    }
}
