namespace ClienteWebGR09.Models;

public enum TipoConversion
{
    Longitud,
    Masa,
    Temperatura
}

public sealed record OpcionUnidad(string Codigo, string Etiqueta);

public sealed class ModeloSesion
{
    public string? Token { get; private set; }
    public bool Autenticado => !string.IsNullOrWhiteSpace(Token);

    public void Iniciar(string token) => Token = token;
    public void Cerrar() => Token = null;
}

public static class CatalogoUnidades
{
    private static readonly OpcionUnidad[] Longitudes =
    [
        new("MILIMETRO", "Milímetro"),
        new("CENTIMETRO", "Centímetro"),
        new("METRO", "Metro"),
        new("KILOMETRO", "Kilómetro"),
        new("YARDA", "Yarda")
    ];

    private static readonly OpcionUnidad[] Masas =
    [
        new("MILIGRAMO", "Miligramo"),
        new("GRAMO", "Gramo"),
        new("KILOGRAMO", "Kilogramo"),
        new("TONELADA", "Tonelada"),
        new("ONZA", "Onza")
    ];

    private static readonly OpcionUnidad[] Temperaturas =
    [
        new("CELSIUS", "Celsius"),
        new("FAHRENHEIT", "Fahrenheit"),
        new("KELVIN", "Kelvin"),
        new("RANKINE", "Rankine")
    ];

    public static IReadOnlyList<OpcionUnidad> Obtener(TipoConversion tipo) => tipo switch
    {
        TipoConversion.Longitud => Longitudes,
        TipoConversion.Masa => Masas,
        TipoConversion.Temperatura => Temperaturas,
        _ => throw new ArgumentOutOfRangeException(nameof(tipo))
    };

    public static string Operacion(TipoConversion tipo) => tipo switch
    {
        TipoConversion.Longitud => "convertirLongitud",
        TipoConversion.Masa => "convertirMasa",
        TipoConversion.Temperatura => "convertirTemperatura",
        _ => throw new ArgumentOutOfRangeException(nameof(tipo))
    };
}
