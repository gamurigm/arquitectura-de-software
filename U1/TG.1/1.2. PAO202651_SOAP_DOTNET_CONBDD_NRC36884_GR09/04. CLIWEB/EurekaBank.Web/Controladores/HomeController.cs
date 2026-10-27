using ec.edu.monster.Modelos;
using ec.edu.monster.Servicios;
using Microsoft.AspNetCore.Mvc;
using ec.edu.monster.Vistas;

namespace ec.edu.monster.Controladores;

public sealed class HomeController(IEurekaSoapClient servicio) : Controller
{
    private const string ClaveUsuario = "eureka.usuario";
    private const string ClaveCuenta = "eureka.cuenta";
    private const string ClaveToken = "eureka.token";
    private readonly IEurekaSoapClient _servicio = servicio;

    [HttpGet]
    public async Task<IActionResult> Index()
    {
        var modelo = new InicioViewModel
        {
            Usuario = HttpContext.Session.GetString(ClaveUsuario),
            Cuenta = HttpContext.Session.GetString(ClaveCuenta) ?? "",
            ClaveOperacion = TempData["ClaveOperacion"] as string ?? Guid.NewGuid().ToString(),
            Mensaje = TempData["Mensaje"] as string,
            EsError = TempData["EsError"] as bool? ?? false
        };
        if (!string.IsNullOrEmpty(modelo.Usuario) && !string.IsNullOrEmpty(modelo.Cuenta))
        {
            try
            {
                modelo.Movimientos = await _servicio.TraerMovimientosAsync(
                    HttpContext.Session.GetString(ClaveToken)!, modelo.Cuenta);
                modelo.Saldo = modelo.Movimientos.Sum(movimiento =>
                    movimiento.Accion.Equals("SALIDA", StringComparison.OrdinalIgnoreCase) ? -movimiento.Importe : movimiento.Importe);
            }
            catch (Exception error)
            {
                if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Index)); }
                modelo.Mensaje ??= $"No se pudieron actualizar los movimientos: {error.Message}";
                modelo.EsError = true;
            }
        }
        if (!string.IsNullOrEmpty(modelo.Usuario))
        {
            try { modelo.Cuentas = await _servicio.TraerCuentasAsync(HttpContext.Session.GetString(ClaveToken)!); }
            catch (Exception error)
            {
                if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Index)); }
                modelo.Mensaje ??= $"No se pudieron consultar las cuentas: {error.Message}";
                modelo.EsError = true;
            }
        }
        return View(modelo);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Login(string usuario, string contrasena)
    {
        try
        {
            var token = await _servicio.CrearSesionAsync(usuario, contrasena);
            if (!string.IsNullOrWhiteSpace(token))
            {
                HttpContext.Session.SetString(ClaveUsuario, usuario.Trim());
                HttpContext.Session.SetString(ClaveToken, token);
                TempData["Mensaje"] = "Sesión iniciada.";
                return RedirectToAction(nameof(Index));
            }
            TempData["Mensaje"] = "Credenciales inválidas.";
        }
        catch (Exception error) { TempData["Mensaje"] = $"No se pudo iniciar sesión: {error.Message}"; }
        TempData["EsError"] = true;
        return RedirectToAction(nameof(Index));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public IActionResult Movimientos(string cuenta)
    {
        if (!TieneSesion()) return RedirigirSinSesion();
        if (string.IsNullOrWhiteSpace(cuenta) || cuenta.Trim().Length > 8)
        {
            TempData["Mensaje"] = "Ingrese una cuenta válida.";
            TempData["EsError"] = true;
            return RedirectToAction(nameof(Index));
        }
        HttpContext.Session.SetString(ClaveCuenta, cuenta.Trim());
        TempData["Mensaje"] = "Cuenta seleccionada.";
        return RedirectToAction(nameof(Index));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Deposito(decimal importe)
    {
        if (!TieneSesion()) return RedirigirSinSesion();
        var cuenta = HttpContext.Session.GetString(ClaveCuenta);
        if (string.IsNullOrWhiteSpace(cuenta))
        {
            TempData["Mensaje"] = "Consulte una cuenta antes de depositar.";
            TempData["EsError"] = true;
            return RedirectToAction(nameof(Index));
        }
        if (importe <= 0 || decimal.Round(importe, 2) != importe)
        {
            TempData["Mensaje"] = "El importe debe ser positivo y tener hasta dos decimales.";
            TempData["EsError"] = true;
            return RedirectToAction(nameof(Index));
        }
        try
        {
            var registrado = await _servicio.RegistrarDepositoAsync(
                HttpContext.Session.GetString(ClaveToken)!, cuenta, importe) == 1;
            TempData["Mensaje"] = registrado ? "Depósito registrado." : "No se pudo registrar el depósito.";
            TempData["EsError"] = !registrado;
        }
        catch (Exception error)
        {
            if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Index)); }
            TempData["Mensaje"] = $"No se pudo confirmar el depósito: {error.Message}";
            TempData["EsError"] = true;
        }
        return RedirectToAction(nameof(Index));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Transferencia(string cuentaOrigen, string cuentaDestino, decimal importe, string claveOperacion)
    {
        if (!TieneSesion()) return RedirigirSinSesion();
        if (!Guid.TryParse(claveOperacion, out var clave) || clave == Guid.Empty
            || !CuentaValida(cuentaOrigen) || !CuentaValida(cuentaDestino) || cuentaOrigen == cuentaDestino
            || importe <= 0 || importe > 9_999_999_999.99m || decimal.Round(importe, 2) != importe)
        {
            TempData["Mensaje"] = "Revise las cuentas y el importe ingresado.";
            TempData["EsError"] = true;
            TempData["ClaveOperacion"] = claveOperacion;
            return RedirectToAction(nameof(Index));
        }
        try
        {
            var token = HttpContext.Session.GetString(ClaveToken);
            var registrada = await _servicio.TransferirAsync(token!, cuentaOrigen.Trim(), cuentaDestino.Trim(), importe, clave) == 1;
            TempData["Mensaje"] = registrada ? "Transferencia registrada: débito y crédito confirmados." : "Transferencia rechazada. Revise cuentas, moneda e importe.";
            TempData["EsError"] = !registrada;
            if (registrada) HttpContext.Session.SetString(ClaveCuenta, cuentaOrigen.Trim());
            else TempData["ClaveOperacion"] = clave.ToString();
            if (registrada) TempData["ClaveOperacion"] = Guid.NewGuid().ToString();
        }
        catch (Exception error)
        {
            if (Expirada(error)) { HttpContext.Session.Clear(); return RedirectToAction(nameof(Index)); }
            TempData["Mensaje"] = $"No se confirmó. Reenvíe la misma solicitud. {error.Message}";
            TempData["EsError"] = true;
            TempData["ClaveOperacion"] = clave.ToString();
        }
        return RedirectToAction(nameof(Index));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Logout()
    {
        var token = HttpContext.Session.GetString(ClaveToken);
        try { if (token is not null) await _servicio.CerrarSesionAsync(token); }
        catch { }
        HttpContext.Session.Clear();
        TempData["Mensaje"] = "Sesión cerrada.";
        return RedirectToAction(nameof(Index));
    }

    private bool TieneSesion() => !string.IsNullOrEmpty(HttpContext.Session.GetString(ClaveUsuario))
        && !string.IsNullOrEmpty(HttpContext.Session.GetString(ClaveToken));
    private static bool Expirada(Exception error) =>
        error.ToString().Contains("Token ausente, inválido o caducado", StringComparison.OrdinalIgnoreCase);
    private static bool CuentaValida(string? cuenta) => cuenta is { Length: 8 } && cuenta.All(char.IsAsciiDigit);

    private IActionResult RedirigirSinSesion()
    {
        TempData["Mensaje"] = "Inicie sesión primero.";
        TempData["EsError"] = true;
        return RedirectToAction(nameof(Index));
    }
}
