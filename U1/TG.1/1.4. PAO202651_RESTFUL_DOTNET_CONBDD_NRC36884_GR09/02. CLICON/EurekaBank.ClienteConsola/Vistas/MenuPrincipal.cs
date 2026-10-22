using System.Globalization;
using EurekaBank.Cliente.Controladores;

namespace EurekaBank.ClienteConsola.Vistas;

public sealed class MenuPrincipal(EurekaBankController controlador)
{
    public async Task EjecutarAsync()
    {
        Console.WriteLine("EurekaBank | Monster");
        while (true)
        {
            if (!controlador.SesionActiva && !await IniciarSesionAsync())
            {
                return;
            }

            Console.WriteLine($"\nSesión: {controlador.UsuarioActivo}");
            Console.WriteLine("1. Consultar movimientos");
            Console.WriteLine("2. Registrar depósito");
            Console.WriteLine("3. Transferir entre cuentas");
            Console.WriteLine("4. Cerrar sesión");
            Console.WriteLine("0. Salir");
            Console.Write("Opción: ");

            switch (Console.ReadLine()?.Trim())
            {
                case "1": await ConsultarMovimientosAsync(); break;
                case "2": await RegistrarDepositoAsync(); break;
                case "3": await TransferirAsync(); break;
                case "4": await controlador.CerrarSesionAsync(); break;
                case "0": return;
                default: Console.WriteLine("Seleccione una opción válida."); break;
            }
        }
    }

    private async Task<bool> IniciarSesionAsync()
    {
        while (true)
        {
            Console.Write("Usuario (0 para salir): ");
            var usuario = Console.ReadLine()?.Trim() ?? string.Empty;
            if (usuario == "0") return false;

            Console.Write("Contraseña: ");
            var contrasena = LeerContrasena();
            try
            {
                if (await controlador.IniciarSesionAsync(usuario, contrasena)) return true;
                Console.WriteLine("Credenciales incorrectas o cuenta sin asociación habilitada.");
            }
            catch (HttpRequestException)
            {
                Console.WriteLine("No se pudo contactar la API. Revise EUREKABANK_REST_DOTNET_URL.");
            }
        }
    }

    private async Task ConsultarMovimientosAsync()
    {
        Console.Write("Cuenta: ");
        var cuenta = Console.ReadLine()?.Trim() ?? string.Empty;
        try
        {
            var movimientos = await controlador.ConsultarMovimientosAsync(cuenta);
            foreach (var movimiento in movimientos)
            {
                Console.WriteLine(
                    $"{movimiento.Numero,4}  {movimiento.Fecha:O}  {movimiento.Tipo} " +
                    $"{movimiento.Accion}  {movimiento.Importe.ToString("N2", CultureInfo.CurrentCulture)}");
            }
            if (movimientos.Count == 0) Console.WriteLine("No hay movimientos para esa cuenta.");
        }
        catch (HttpRequestException)
        {
            Console.WriteLine("No se pudo consultar la API. Revise la conexión.");
        }
    }

    private async Task RegistrarDepositoAsync()
    {
        Console.Write("Cuenta: ");
        var cuenta = Console.ReadLine()?.Trim() ?? string.Empty;
        Console.Write("Importe: ");
        var texto = Console.ReadLine();
        if (!decimal.TryParse(texto, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe))
        {
            Console.WriteLine("Ingrese un importe válido.");
            return;
        }

        try
        {
            Console.WriteLine(await controlador.RegistrarDepositoAsync(cuenta, importe)
                ? "Depósito registrado."
                : "Depósito rechazado. Revise cuenta e importe.");
        }
        catch (HttpRequestException)
        {
            Console.WriteLine("No se pudo registrar el depósito. Revise la conexión.");
        }
    }

    private async Task TransferirAsync()
    {
        Console.Write("Cuenta origen: ");
        var origen = Console.ReadLine()?.Trim() ?? string.Empty;
        Console.Write("Cuenta destino: ");
        var destino = Console.ReadLine()?.Trim() ?? string.Empty;
        Console.Write("Importe: ");
        if (!decimal.TryParse(Console.ReadLine(), NumberStyles.Number, CultureInfo.CurrentCulture, out var importe))
        {
            Console.WriteLine("Ingrese un importe válido.");
            return;
        }

        try
        {
            Console.WriteLine(await controlador.TransferirAsync(origen, destino, importe, Guid.NewGuid())
                ? "Transferencia registrada: débito y crédito confirmados."
                : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
        }
        catch (HttpRequestException)
        {
            Console.WriteLine("No se confirmó el resultado. Reintente con la misma clave de operación.");
        }
    }

    private static string LeerContrasena()
    {
        if (Console.IsInputRedirected) return Console.ReadLine() ?? string.Empty;

        var caracteres = new List<char>();
        while (true)
        {
            var tecla = Console.ReadKey(intercept: true);
            if (tecla.Key == ConsoleKey.Enter) break;
            if (tecla.Key == ConsoleKey.Backspace && caracteres.Count > 0) caracteres.RemoveAt(caracteres.Count - 1);
            else if (!char.IsControl(tecla.KeyChar)) caracteres.Add(tecla.KeyChar);
        }
        Console.WriteLine();
        return new string(caracteres.ToArray());
    }
}
