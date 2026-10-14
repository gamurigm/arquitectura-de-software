using System.Globalization;
using System.Text.Json;
using ClienteWebGR09.Modelos;
using ClienteWebGR09.Servicios;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;

namespace ClienteWebGR09.Controladores;

[Controller]
public sealed class ControladorInicio : Controller
{
    private const string ClaveToken = "token";
    private readonly ClienteSOAP _servicio;

    public ControladorInicio(ClienteSOAP servicio)
    {
        _servicio = servicio;
    }

    [HttpGet]
    public IActionResult Inicio()
    {
        var modelo = new ModeloConversion();
        PrepararVista(modelo.Tipo);
        return View(modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> IniciarSesion(ModeloInicioSesion modelo)
    {
        if (!ModelState.IsValid)
        {
            TempData["Error"] = "Completa usuario y contraseña.";
            return RedirectToAction(nameof(Inicio));
        }

        try
        {
            var token = await _servicio.IniciarSesionAsync(modelo.Usuario.Trim(), modelo.Contrasenia);
            HttpContext.Session.SetString(ClaveToken, token);
            TempData["Mensaje"] = "Sesión iniciada.";
        }
        catch (Exception excepcion)
        {
            TempData["Error"] = excepcion.Message;
        }

        return RedirectToAction(nameof(Inicio));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Convertir(ModeloConversion modelo)
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        if (string.IsNullOrWhiteSpace(token))
        {
            TempData["Error"] = "Inicia sesión antes de convertir.";
            return RedirectToAction(nameof(Inicio));
        }

        if (!Enum.IsDefined(modelo.Tipo))
        {
            ModelState.AddModelError(nameof(modelo.Tipo), "Selecciona una magnitud válida.");
        }
        var valor = 0d;
        if (string.IsNullOrWhiteSpace(modelo.Valor)
            || !double.TryParse(modelo.Valor.Trim().Replace(',', '.'), NumberStyles.Float,
                CultureInfo.InvariantCulture, out valor)
            || !double.IsFinite(valor))
        {
            ModelState.AddModelError(nameof(modelo.Valor), "Ingresa un número finito.");
        }
        if (Enum.IsDefined(modelo.Tipo)
            && (!CatalogoUnidades.Obtener(modelo.Tipo).Any(unidad => unidad.Codigo == modelo.UnidadInicial)
                || !CatalogoUnidades.Obtener(modelo.Tipo).Any(unidad => unidad.Codigo == modelo.UnidadFinal)))
        {
            ModelState.AddModelError(nameof(modelo.UnidadInicial), "Selecciona unidades válidas para la magnitud.");
        }
        if (Enum.IsDefined(modelo.Tipo) && modelo.Tipo == TipoConversion.Masa && valor < 0)
        {
            ModelState.AddModelError(nameof(modelo.Valor), "La masa no puede ser negativa.");
        }

        if (!ModelState.IsValid)
        {
            PrepararVista(Enum.IsDefined(modelo.Tipo) ? modelo.Tipo : TipoConversion.Longitud);
            return View(nameof(Inicio), modelo);
        }

        try
        {
            var resultado = await _servicio.ConvertirAsync(
                token, modelo.Tipo, valor, modelo.UnidadInicial, modelo.UnidadFinal);
            if (!double.IsFinite(resultado))
            {
                throw new InvalidOperationException("El resultado excede el rango permitido.");
            }
            modelo.Resultado = $"{resultado.ToString("G", CultureInfo.CurrentCulture)} {modelo.UnidadFinal}";
            PrepararVista(modelo.Tipo);
            return View(nameof(Inicio), modelo);
        }
        catch (Exception excepcion)
        {
            TempData["Error"] = excepcion.Message;
            return RedirectToAction(nameof(Inicio));
        }
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> CambiarContrasenia(ModeloCambiarContrasenia modelo)
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        if (string.IsNullOrWhiteSpace(token))
        {
            TempData["Error"] = "Inicia sesión antes de cambiar la contraseña.";
            return RedirectToAction(nameof(Inicio));
        }

        if (!ModelState.IsValid)
        {
            TempData["Error"] = "Completa ambas contraseñas.";
            return RedirectToAction(nameof(Inicio));
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

        return RedirectToAction(nameof(Inicio));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public IActionResult CerrarSesion()
    {
        HttpContext.Session.Remove(ClaveToken);
        TempData["Mensaje"] = "Sesión cerrada.";
        return RedirectToAction(nameof(Inicio));
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
