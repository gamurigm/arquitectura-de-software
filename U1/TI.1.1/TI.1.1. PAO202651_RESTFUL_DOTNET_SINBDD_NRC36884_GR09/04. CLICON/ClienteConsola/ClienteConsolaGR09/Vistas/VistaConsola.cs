using System.Globalization;
using ClienteConsolaGR09.Modelos;

namespace ClienteConsolaGR09.Vistas;

public sealed class VistaConsola
{
    public SolicitudInicioSesion SolicitarCredenciales()
    {
        string usuario;
        do
        {
            EscribirPrompt("Usuario [Monster]: ");
            usuario = Console.ReadLine()?.Trim() ?? "";
            if (string.IsNullOrWhiteSpace(usuario)) usuario = "Monster";
        } while (string.IsNullOrWhiteSpace(usuario));

        return new SolicitudInicioSesion(usuario, LeerClaveOculta("Monster9"));
    }

    public string MostrarMenu()
    {
        Console.WriteLine();
        EscribirColor("────────────── MENU PRINCIPAL ──────────────", ConsoleColor.Cyan);
        EscribirColor("1. Convertir unidades", ConsoleColor.Green);
        EscribirColor("2. Consultar catálogo", ConsoleColor.Green);
        EscribirColor("0. Salir", ConsoleColor.Gray);
        EscribirPrompt("Opción: ");
        return Console.ReadLine() ?? "";
    }

    public ConversionSolicitud? SolicitarConversion(CatalogoRespuesta catalogo, out string magnitud)
    {
        magnitud = "";
        var opciones = catalogo.Magnitudes.ToArray();
        if (opciones.Length == 0)
        {
            MostrarError("El catálogo no contiene magnitudes.");
            return null;
        }
        Console.WriteLine();
        EscribirColor("Seleccione una magnitud", ConsoleColor.Cyan);
        for (var i = 0; i < opciones.Length; i++)
        {
            EscribirColor($"{i + 1}. {opciones[i].Key}", ConsoleColor.Green);
        }

        int indice;
        while (true)
        {
            EscribirPrompt("Magnitud: ");
            if (int.TryParse(Console.ReadLine(), out indice) && indice >= 1 && indice <= opciones.Length) break;
            MostrarError("Seleccione una magnitud de la lista.");
        }

        magnitud = opciones[indice - 1].Key;
        var unidades = opciones[indice - 1].Value;
        if (unidades.Count == 0)
        {
            MostrarError("El catálogo no contiene unidades para esta magnitud.");
            return null;
        }
        double valor;
        while (true)
        {
            EscribirPrompt("Valor: ");
            if (double.TryParse(Console.ReadLine(), NumberStyles.Float, CultureInfo.CurrentCulture, out valor)
                && double.IsFinite(valor)
                && (!magnitud.Equals("masa", StringComparison.OrdinalIgnoreCase) || valor >= 0)) break;
            MostrarError(magnitud.Equals("masa", StringComparison.OrdinalIgnoreCase)
                ? "Ingrese un número finito; la masa no puede ser negativa."
                : "Ingrese un número finito.");
        }

        var origen = LeerUnidad("Unidad de origen", unidades);
        var destino = LeerUnidad("Unidad de destino", unidades);
        return new ConversionSolicitud(valor, origen, destino);
    }

    public void MostrarCatalogo(CatalogoRespuesta catalogo)
    {
        EscribirColor(catalogo.Mensaje, ConsoleColor.Cyan);
        foreach (var (magnitud, unidades) in catalogo.Magnitudes)
        {
            EscribirColor($"• {magnitud}: {string.Join(", ", unidades)}", ConsoleColor.Gray);
        }
    }

    public void MostrarResultado(string mensaje) => EscribirColor(mensaje, ConsoleColor.Green);

    public void MostrarError(string mensaje) => EscribirColor($"Error: {mensaje}", ConsoleColor.Red);

    public void MostrarInicio(string mensaje)
    {
        EscribirColor("CONVERSOR DE UNIDADES MONSTER — GR09", ConsoleColor.Cyan);
        EscribirColor("────────────────────────────────────────────", ConsoleColor.DarkCyan);
        EscribirColor("Servicio REST .NET  ·  Inicio de sesión", ConsoleColor.Gray);
        EscribirColor(mensaje, ConsoleColor.Yellow);
    }

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

    private string LeerClaveOculta(string? predeterminada = null)
    {
        while (true)
        {
            var caracteres = new List<char>();
            while (true)
            {
                var tecla = Console.ReadKey(intercept: true);
                if (tecla.Key == ConsoleKey.Enter)
                {
                    Console.WriteLine();
                    break;
                }

                if (tecla.Key == ConsoleKey.Backspace && caracteres.Count > 0)
                {
                    caracteres.RemoveAt(caracteres.Count - 1);
                    Console.Write("\b \b");
                }
                else if (!char.IsControl(tecla.KeyChar))
                {
                    caracteres.Add(tecla.KeyChar);
                    Console.Write('*');
                }
            }

            if (caracteres.Count > 0) return new string(caracteres.ToArray());
            if (predeterminada is not null) return predeterminada;
            MostrarError("La contraseña no puede quedar vacía.");
        }
    }

    private string LeerUnidad(string etiqueta, IReadOnlyList<string> unidades)
    {
        if (unidades.Count == 0) throw new InvalidOperationException("No hay unidades disponibles.");
        for (var i = 0; i < unidades.Count; i++) EscribirColor($"{i + 1}. {unidades[i]}", ConsoleColor.Green);
        while (true)
        {
            EscribirPrompt($"{etiqueta}: ");
            if (int.TryParse(Console.ReadLine(), out var indice) && indice >= 1 && indice <= unidades.Count)
            {
                return unidades[indice - 1];
            }

            MostrarError("Seleccione una unidad de la lista.");
        }
    }
}
