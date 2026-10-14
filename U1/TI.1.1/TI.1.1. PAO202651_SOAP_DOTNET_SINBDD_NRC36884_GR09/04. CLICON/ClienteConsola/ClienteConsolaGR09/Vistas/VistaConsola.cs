using System.Globalization;
using System.Text;
using ClienteConsolaGR09.Modelos;

namespace ClienteConsolaGR09.Vistas;

internal sealed class VistaConsola
{
    public void MostrarInicio()
    {
        Console.Clear();
        EscribirColor("CONVERSOR DE UNIDADES MONSTER — GR09", ConsoleColor.Cyan);
        EscribirColor("────────────────────────────────────────────", ConsoleColor.DarkCyan);
        EscribirColor("Servicio SOAP .NET  ·  Inicio de sesión", ConsoleColor.Gray);
        Console.WriteLine();
    }

    public int LeerOpcion()
    {
        Console.WriteLine();
        EscribirColor("────────────── MENU PRINCIPAL ──────────────", ConsoleColor.Cyan);
        EscribirColor("1. Longitud   2. Masa   3. Temperatura", ConsoleColor.Green);
        EscribirColor("4. Cambiar contraseña   0. Salir", ConsoleColor.Green);
        EscribirPrompt("Opción: ");
        return int.TryParse(Console.ReadLine(), out var opcion) ? opcion : -1;
    }

    public string LeerTexto(string etiqueta)
    {
        EscribirPrompt(etiqueta);
        return Console.ReadLine()?.Trim() ?? string.Empty;
    }

    public string LeerContrasenia(string etiqueta, string? predeterminada = null)
    {
        while (true)
        {
            EscribirPrompt(etiqueta);
            var valor = new StringBuilder();
            while (true)
            {
                var tecla = Console.ReadKey(intercept: true);
                if (tecla.Key == ConsoleKey.Enter)
                {
                    Console.WriteLine();
                    break;
                }

                if (tecla.Key == ConsoleKey.Backspace)
                {
                    if (valor.Length > 0)
                    {
                        valor.Length--;
                        Console.Write("\b \b");
                    }

                    continue;
                }

                if (!char.IsControl(tecla.KeyChar))
                {
                    valor.Append(tecla.KeyChar);
                    Console.Write('*');
                }
            }

            if (valor.Length == 0 && predeterminada is not null) return predeterminada;
            if (string.IsNullOrWhiteSpace(valor.ToString()))
            {
                MostrarError("La contraseña no puede quedar vacía.");
                continue;
            }

            return valor.ToString();
        }
    }

    public double LeerNumero(string etiqueta)
    {
        while (true)
        {
            EscribirPrompt(etiqueta);
            var texto = Console.ReadLine()?.Trim().Replace(',', '.');
            if (double.TryParse(texto, NumberStyles.Float, CultureInfo.InvariantCulture, out var valor)
                && double.IsFinite(valor))
            {
                return valor;
            }

            MostrarError("Ingresa un número finito.");
        }
    }

    public string LeerUnidad(string etiqueta, IReadOnlyList<OpcionUnidad> unidades)
    {
        EscribirColor($"{etiqueta}:", ConsoleColor.Cyan);
        for (var indice = 0; indice < unidades.Count; indice++)
        {
            EscribirColor($"{indice + 1}. {unidades[indice].Etiqueta}", ConsoleColor.Green);
        }

        while (true)
        {
            EscribirPrompt("Número de unidad: ");
            if (int.TryParse(Console.ReadLine(), out var indice)
                && indice >= 1 && indice <= unidades.Count)
            {
                return unidades[indice - 1].Codigo;
            }

            MostrarError("Selecciona una unidad de la lista.");
        }
    }

    public void MostrarResultado(double resultado, string unidad) =>
        EscribirColor(double.IsFinite(resultado)
            ? $"Resultado: {resultado.ToString("G", CultureInfo.CurrentCulture)} {unidad}"
            : "Error: el resultado excede el rango permitido.",
            double.IsFinite(resultado) ? ConsoleColor.Green : ConsoleColor.Red);

    public void MostrarMensaje(string mensaje) => EscribirColor(mensaje, ConsoleColor.Cyan);
    public void MostrarError(string mensaje) => EscribirColor($"Error: {mensaje}", ConsoleColor.Red);

    private static bool UsarColor => !Console.IsOutputRedirected && Environment.GetEnvironmentVariable("NO_COLOR") is null;

    private static void EscribirPrompt(string texto) => EscribirColor(texto, ConsoleColor.Yellow, nuevaLinea: false);

    private static void EscribirColor(string texto, ConsoleColor color, bool nuevaLinea = true)
    {
        if (!UsarColor)
        {
            if (nuevaLinea) Console.WriteLine(texto);
            else Console.Write(texto);
            return;
        }

        var anterior = Console.ForegroundColor;
        try
        {
            Console.ForegroundColor = color;
            if (nuevaLinea) Console.WriteLine(texto);
            else Console.Write(texto);
        }
        finally
        {
            Console.ForegroundColor = anterior;
        }
    }
}
