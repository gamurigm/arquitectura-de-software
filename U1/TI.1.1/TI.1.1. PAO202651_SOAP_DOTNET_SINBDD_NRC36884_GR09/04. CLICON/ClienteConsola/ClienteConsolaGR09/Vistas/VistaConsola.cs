using System.Globalization;
using System.Text;
using ClienteConsolaGR09.Modelos;

namespace ClienteConsolaGR09.Vistas;

internal sealed class VistaConsola
{
    public void MostrarInicio()
    {
        Console.Clear();
        Console.WriteLine("CONVERSOR DE UNIDADES MONSTER — GR09");
        Console.WriteLine("Servicio SOAP .NET | Inicio de sesión");
        Console.WriteLine();
    }

    public int LeerOpcion()
    {
        Console.WriteLine();
        Console.WriteLine("1. Longitud   2. Masa   3. Temperatura");
        Console.WriteLine("4. Cambiar contraseña   0. Salir");
        Console.Write("Opción: ");
        return int.TryParse(Console.ReadLine(), out var opcion) ? opcion : -1;
    }

    public string LeerTexto(string etiqueta)
    {
        Console.Write(etiqueta);
        return Console.ReadLine()?.Trim() ?? string.Empty;
    }

    public string LeerContrasenia(string etiqueta)
    {
        Console.Write(etiqueta);
        var valor = new StringBuilder();
        while (true)
        {
            var tecla = Console.ReadKey(intercept: true);
            if (tecla.Key == ConsoleKey.Enter)
            {
                Console.WriteLine();
                return valor.ToString();
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
    }

    public double LeerNumero(string etiqueta)
    {
        while (true)
        {
            Console.Write(etiqueta);
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
        Console.WriteLine($"{etiqueta}:");
        for (var indice = 0; indice < unidades.Count; indice++)
        {
            Console.WriteLine($"{indice + 1}. {unidades[indice].Etiqueta}");
        }

        while (true)
        {
            Console.Write("Número de unidad: ");
            if (int.TryParse(Console.ReadLine(), out var indice)
                && indice >= 1 && indice <= unidades.Count)
            {
                return unidades[indice - 1].Codigo;
            }

            MostrarError("Selecciona una unidad de la lista.");
        }
    }

    public void MostrarResultado(double resultado, string unidad) =>
        Console.WriteLine($"Resultado: {resultado.ToString("G", CultureInfo.CurrentCulture)} {unidad}");

    public void MostrarMensaje(string mensaje) => Console.WriteLine(mensaje);
    public void MostrarError(string mensaje) => Console.WriteLine($"Error: {mensaje}");
}
