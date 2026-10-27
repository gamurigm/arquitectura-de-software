using System.Globalization;
using ec.edu.monster.Controladores;
using ec.edu.monster.Servicios;

using var servicio = new EurekaSoapClient();
var controlador = new EurekaBankController(servicio);
Console.WriteLine("EurekaBank | Cliente de consola");

while (true)
{
    if (!controlador.SesionActiva)
    {
        Console.Write("Usuario (vacío para salir): ");
        var usuario = Console.ReadLine();
        if (string.IsNullOrWhiteSpace(usuario)) break;
        Console.Write("Contraseña: ");
        var contrasena = LeerContrasena();
        try
        {
            if (!await controlador.IniciarSesionAsync(usuario, contrasena))
            {
                Console.WriteLine("Credenciales inválidas.");
                continue;
            }
            MostrarCuentas(controlador);
            Console.WriteLine("Sesión iniciada.");
        }
        catch (Exception error) { Console.WriteLine($"No se pudo iniciar sesión: {error.Message}"); continue; }
    }

    var cuenta = ElegirCuenta(controlador);
    if (cuenta is null) { await controlador.CerrarSesionAsync(); continue; }
    try { await controlador.ConsultarMovimientosAsync(cuenta); MostrarMovimientos(controlador); }
    catch (Exception error) { Console.WriteLine($"No se pudieron consultar movimientos: {error.Message}"); }

    while (controlador.SesionActiva)
    {
        Console.WriteLine("1. Actualizar movimientos  2. Depositar  3. Transferir  4. Cambiar cuenta  5. Cerrar sesión  0. Salir");
        Console.Write("Opción: ");
        switch (Console.ReadLine())
        {
            case "1":
                try { await controlador.ConsultarMovimientosAsync(cuenta); MostrarMovimientos(controlador); }
                catch (Exception error) { Console.WriteLine($"Error: {error.Message}"); }
                break;
            case "2":
                Console.Write("Importe: ");
                if (!decimal.TryParse(Console.ReadLine(), NumberStyles.Number, CultureInfo.CurrentCulture, out var importe)
                    || decimal.Round(importe, 2) != importe || importe <= 0)
                {
                    Console.WriteLine("Ingrese un importe positivo con hasta dos decimales.");
                    break;
                }
                try
                {
                    var registrado = await controlador.RegistrarDepositoAsync(importe);
                    if (!registrado)
                    {
                        Console.WriteLine("No se pudo registrar el depósito.");
                        break;
                    }
                    Console.WriteLine("Depósito registrado.");
                    try
                    {
                        await controlador.ConsultarMovimientosAsync(cuenta);
                        MostrarMovimientos(controlador);
                    }
                    catch (Exception error) { Console.WriteLine($"El depósito se registró, pero no se pudieron actualizar los movimientos: {error.Message}"); }
                }
                catch (Exception error) { Console.WriteLine($"Error al registrar el depósito: {error.Message}"); }
                break;
            case "3":
                Console.Write("Cuenta destino: "); var destino = Console.ReadLine() ?? "";
                Console.Write("Importe: ");
                if (!decimal.TryParse(Console.ReadLine(), NumberStyles.Number, CultureInfo.CurrentCulture, out var importeTransferencia)
                    || importeTransferencia <= 0 || decimal.Round(importeTransferencia, 2) != importeTransferencia)
                { Console.WriteLine("Ingrese un importe válido."); break; }
                try
                {
                    var registrada = await controlador.TransferirAsync(cuenta, destino.Trim(), importeTransferencia);
                    Console.WriteLine(registrada ? "Transferencia registrada: débito y crédito confirmados." : "Transferencia rechazada. Revise cuentas, moneda e importe.");
                }
                catch (Exception error) { Console.WriteLine($"No se confirmó. Reenvíe la misma solicitud. {error.Message}"); }
                break;
            case "4":
                cuenta = ElegirCuenta(controlador) ?? cuenta;
                try { await controlador.ConsultarMovimientosAsync(cuenta); MostrarMovimientos(controlador); }
                catch (Exception error) { Console.WriteLine($"Error: {error.Message}"); }
                break;
            case "5": await controlador.CerrarSesionAsync(); Console.WriteLine("Sesión cerrada."); break;
            case "0": await controlador.CerrarSesionAsync(); return;
            default: Console.WriteLine("Opción inválida."); break;
        }
    }
}

static void MostrarMovimientos(EurekaBankController controlador)
{
    Console.WriteLine("Cuenta       Fecha       Tipo                 Acción   Importe");
    foreach (var movimiento in controlador.Movimientos)
        Console.WriteLine($"{movimiento.Cuenta,-12} {movimiento.Fecha:yyyy-MM-dd} {movimiento.Tipo,-20} {movimiento.Accion,-8} {movimiento.Importe,10:N2}");
    Console.WriteLine($"Saldo calculado: {controlador.Saldo:N2}");
}

static void MostrarCuentas(EurekaBankController controlador)
{
    Console.WriteLine(controlador.Cuentas.Count == 0 ? "No hay cuentas activas asignadas." : "Cuentas activas:");
    foreach (var cuenta in controlador.Cuentas) Console.WriteLine($"  {cuenta.Etiqueta}");
}

static string? ElegirCuenta(EurekaBankController controlador)
{
    if (controlador.Cuentas.Count == 0) { Console.WriteLine("No hay cuentas activas asignadas."); return null; }
    for (var i = 0; i < controlador.Cuentas.Count; i++) Console.WriteLine($"{i + 1}. {controlador.Cuentas[i].Etiqueta}");
    Console.Write("Cuenta (número de opción): ");
    return int.TryParse(Console.ReadLine(), out var opcion) && opcion >= 1 && opcion <= controlador.Cuentas.Count
        ? controlador.Cuentas[opcion - 1].Numero : null;
}

static string LeerContrasena()
{
    var caracteres = new List<char>();
    while (true)
    {
        var tecla = Console.ReadKey(intercept: true);
        if (tecla.Key == ConsoleKey.Enter) break;
        if (tecla.Key == ConsoleKey.Backspace && caracteres.Count > 0) caracteres.RemoveAt(caracteres.Count - 1);
        else if (!char.IsControl(tecla.KeyChar)) caracteres.Add(tecla.KeyChar);
    }
    Console.WriteLine();
    var contrasena = new string(caracteres.ToArray());
    caracteres.Clear();
    return contrasena;
}
