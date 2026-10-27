using Celulares.Cliente.Controladores;

namespace Celulares.Consola.Vistas;

internal static class VistaConsola
{
    public static string Texto(string etiqueta)
    {
        Console.Write(etiqueta + ": ");
        return Console.ReadLine()?.Trim() ?? "";
    }

    public static int Entero(string etiqueta)
    {
        if (!int.TryParse(Texto(etiqueta), out var valor) || valor <= 0)
            throw new ArgumentException("Ingrese un número entero positivo.");
        return valor;
    }

    public static decimal Dinero(string etiqueta)
    {
        return ControladorTienda.InterpretarPrecio(Texto(etiqueta));
    }

    public static bool Confirmar(string mensaje) =>
        Texto(mensaje + " (s/n)").Equals("s", StringComparison.OrdinalIgnoreCase);
}
