using EurekaBank.Cliente.Servicios;
using EurekaBank.ClienteWeb.Modelos;
using Microsoft.AspNetCore.Mvc;

namespace EurekaBank.ClienteWeb.Controllers;

public sealed class CuentaController(IEurekaBankService servicio) : Controller
{
    [HttpGet]
    public IActionResult Login() => ExigirSesion()
        ? RedirectToAction(nameof(Index))
        : View(new LoginViewModel());

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Login(LoginViewModel modelo)
    {
        if (!ModelState.IsValid) return View(modelo);

        try
        {
            var respuesta = await servicio.IniciarSesionAsync(modelo.Usuario, modelo.Contrasena);
            if (!respuesta.Autenticado || string.IsNullOrWhiteSpace(respuesta.Token))
            {
                ModelState.AddModelError(string.Empty, "Credenciales incorrectas o cuenta sin asociación habilitada.");
                return View(modelo);
            }

            HttpContext.Session.SetString("usuario", modelo.Usuario);
            HttpContext.Session.SetString("token", respuesta.Token);
            return RedirectToAction(nameof(Index));
        }
        catch (HttpRequestException)
        {
            ModelState.AddModelError(string.Empty, "No se pudo contactar la API.");
            return View(modelo);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Index()
    {
        if (!ExigirSesion()) return RedirectToAction(nameof(Login));
        var modelo = new CuentaViewModel { Usuario = HttpContext.Session.GetString("usuario")!, ClaveOperacion = Guid.NewGuid() };
        if (!await CargarCuentasAsync(modelo)) return RedirectToAction(nameof(Login));
        return View(modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Movimientos(CuentaViewModel modelo)
    {
        if (!ExigirSesion()) return RedirectToAction(nameof(Login));
        try
        {
            modelo.Usuario = HttpContext.Session.GetString("usuario")!;
            modelo.Movimientos = await servicio.ConsultarMovimientosAsync(modelo.Cuenta, Token());
            modelo.Mensaje = "Consulta completada.";
        }
        catch (HttpRequestException error)
        {
            if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Login)); }
            modelo.Mensaje = "No se pudieron consultar los movimientos.";
        }

        if (!await CargarCuentasAsync(modelo)) return RedirectToAction(nameof(Login));
        return View("Index", modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Depositar(CuentaViewModel modelo)
    {
        if (!ExigirSesion()) return RedirectToAction(nameof(Login));
        modelo.Usuario = HttpContext.Session.GetString("usuario")!;
        if (!ModelState.IsValid || !ImporteValido(modelo.Importe))
        {
            modelo.Mensaje = "Revise el importe ingresado.";
            return View("Index", modelo);
        }

        try
        {
            var registrado = await servicio.RegistrarDepositoAsync(modelo.CuentaDeposito, modelo.Importe, Token());
            modelo.Mensaje = registrado ? "Depósito registrado." : "Depósito rechazado. Revise cuenta e importe.";
        }
        catch (HttpRequestException error)
        {
            if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Login)); }
            modelo.Mensaje = "No se pudo registrar el depósito.";
        }

        if (!await CargarCuentasAsync(modelo)) return RedirectToAction(nameof(Login));
        return View("Index", modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Transferir(CuentaViewModel modelo)
    {
        if (!ExigirSesion()) return RedirectToAction(nameof(Login));
        modelo.Usuario = HttpContext.Session.GetString("usuario")!;
        if (!ModelState.IsValid || modelo.ClaveOperacion == Guid.Empty
            || !CuentaValida(modelo.CuentaOrigenTransferencia)
            || !CuentaValida(modelo.CuentaDestinoTransferencia)
            || modelo.CuentaOrigenTransferencia == modelo.CuentaDestinoTransferencia
            || !ImporteValido(modelo.ImporteTransferencia))
        {
            modelo.Mensaje = "Revise las cuentas y el importe ingresado.";
            return View("Index", modelo);
        }

        try
        {
            var registrada = await servicio.TransferirAsync(modelo.CuentaOrigenTransferencia,
                modelo.CuentaDestinoTransferencia, modelo.ImporteTransferencia, modelo.ClaveOperacion, Token()!);
            modelo.Mensaje = registrada
                ? "Transferencia registrada: débito y crédito confirmados."
                : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.";
            if (registrada) modelo.ClaveOperacion = Guid.NewGuid();
        }
        catch (HttpRequestException error)
        {
            if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Login)); }
            modelo.Mensaje = "No se confirmó el resultado. Reenvíe la misma solicitud para evitar duplicados.";
        }
        if (!await CargarCuentasAsync(modelo)) return RedirectToAction(nameof(Login));
        return View("Index", modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Logout()
    {
        try { await servicio.CerrarSesionAsync(Token()); }
        catch (HttpRequestException) { }
        HttpContext.Session.Clear();
        return RedirectToAction(nameof(Login));
    }

    private bool ExigirSesion() => HttpContext.Session.GetString("usuario") is not null
        && HttpContext.Session.GetString("token") is not null;
    private async Task<bool> CargarCuentasAsync(CuentaViewModel modelo)
    {
        try { modelo.Cuentas = await servicio.ConsultarCuentasAsync(Token()!); }
        catch (HttpRequestException error)
        {
            if (Expirada(error)) { HttpContext.Session.Clear(); return false; }
            modelo.Cuentas = [];
            modelo.Mensaje ??= "No se pudieron cargar las cuentas.";
        }
        return true;
    }
    private static bool Expirada(HttpRequestException error) => error.StatusCode == System.Net.HttpStatusCode.Unauthorized;
    private string? Token() => HttpContext.Session.GetString("token");
    private static bool CuentaValida(string? cuenta) => cuenta is { Length: 8 } && cuenta.All(char.IsAsciiDigit);
    private static bool ImporteValido(decimal importe) => importe > 0m
        && importe <= 9_999_999_999.99m && decimal.Round(importe, 2) == importe;
}
