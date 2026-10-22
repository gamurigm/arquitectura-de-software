using EurekaBank.Cliente.Servicios;
using EurekaBank.ClienteWeb.Modelos;
using Microsoft.AspNetCore.Mvc;

namespace EurekaBank.ClienteWeb.Controllers;

public sealed class CuentaController(IEurekaBankService servicio) : Controller
{
    [HttpGet]
    public IActionResult Login() => HttpContext.Session.GetString("usuario") is null
        ? View(new LoginViewModel())
        : RedirectToAction(nameof(Index));

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
    public IActionResult Index() => HttpContext.Session.GetString("usuario") is null
        ? RedirectToAction(nameof(Login))
        : View(new CuentaViewModel { Usuario = HttpContext.Session.GetString("usuario")!, ClaveOperacion = Guid.NewGuid() });

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
        catch (HttpRequestException)
        {
            modelo.Mensaje = "No se pudieron consultar los movimientos.";
        }

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
        catch (HttpRequestException)
        {
            modelo.Mensaje = "No se pudo registrar el depósito.";
        }

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
        catch (HttpRequestException)
        {
            modelo.Mensaje = "No se confirmó el resultado. Reenvíe la misma solicitud para evitar duplicados.";
        }
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

    private bool ExigirSesion() => HttpContext.Session.GetString("usuario") is not null;
    private string? Token() => HttpContext.Session.GetString("token");
    private static bool CuentaValida(string? cuenta) => cuenta is { Length: 8 } && cuenta.All(char.IsAsciiDigit);
    private static bool ImporteValido(decimal importe) => importe > 0m
        && importe <= 9_999_999_999.99m && decimal.Round(importe, 2) == importe;
}
