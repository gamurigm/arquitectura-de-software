using System.Globalization;
using ClienteConsolaGR09.Modelos;

namespace ClienteConsolaGR09.Vistas;

public sealed class VistaConsola
{
    public LoginSolicitud SolicitarCredenciales()
    {
        Console.Write("Usuario: ");
        var usuario = Console.ReadLine() ?? "";
        Console.Write("Clave: ");
        return new LoginSolicitud(usuario, LeerClaveOculta());
    }

    public string MostrarMenu()
    {
        Console.WriteLine();
        Console.WriteLine("1. Convertir unidades");
        Console.WriteLine("2. Consultar catálogo");
        Console.WriteLine("0. Salir");
        Console.Write("Opción: ");
        return Console.ReadLine() ?? "";
    }

    public ConversionSolicitud? SolicitarConversion(CatalogoRespuesta catalogo, out string magnitud)
    {
        magnitud = "";
        var opciones = catalogo.Magnitudes.ToArray();
        Console.WriteLine();
        for (var i = 0; i < opciones.Length; i++)
        {
            Console.WriteLine($"{i + 1}. {opciones[i].Key}");
        }

        Console.Write("Magnitud: ");
        if (!int.TryParse(Console.ReadLine(), out var indice) || indice < 1 || indice > opciones.Length)
        {
            return null;
        }

        magnitud = opciones[indice - 1].Key;
        var unidades = opciones[indice - 1].Value;
        Console.WriteLine($"Unidades: {string.Join(", ", unidades)}");
        Console.Write("Valor: ");
        if (!double.TryParse(Console.ReadLine(), NumberStyles.Float, CultureInfo.CurrentCulture, out var valor))
        {
            return null;
        }

        Console.Write("Unidad de origen: ");
        var origen = Console.ReadLine() ?? "";
        Console.Write("Unidad de destino: ");
        var destino = Console.ReadLine() ?? "";
        return new ConversionSolicitud(valor, origen, destino);
    }

    public void MostrarCatalogo(CatalogoRespuesta catalogo)
    {
        Console.WriteLine(catalogo.Mensaje);
        foreach (var (magnitud, unidades) in catalogo.Magnitudes)
        {
            Console.WriteLine($"- {magnitud}: {string.Join(", ", unidades)}");
        }
    }

    public void MostrarResultado(string mensaje) => Console.WriteLine(mensaje);

    public void MostrarError(string mensaje) => Console.WriteLine($"Error: {mensaje}");

    public void MostrarInicio(string mensaje)
    {
        Console.WriteLine("CONVERSOR DE UNIDADES GR09 · REST .NET");
        Console.WriteLine(mensaje);
    }

    private static string LeerClaveOculta()
    {
        var caracteres = new List<char>();
        while (true)
        {
            var tecla = Console.ReadKey(intercept: true);
            if (tecla.Key == ConsoleKey.Enter)
            {
                Console.WriteLine();
                return new string(caracteres.ToArray());
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
    }
}
