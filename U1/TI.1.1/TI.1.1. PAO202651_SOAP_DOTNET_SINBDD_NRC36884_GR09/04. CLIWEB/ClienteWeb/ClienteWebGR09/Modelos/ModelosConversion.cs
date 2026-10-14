namespace ClienteWebGR09.Modelos;

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
        new("MILIMETROS", "Milímetros"),
        new("CENTIMETROS", "Centímetros"),
        new("METROS", "Metros"),
        new("KILOMETROS", "Kilómetros"),
        new("PIES", "Pies"),
        new("YARDAS", "Yardas"),
        new("MILLAS", "Millas")
    ];

    private static readonly OpcionUnidad[] Masas =
    [
        new("MILIGRAMOS", "Miligramos"),
        new("GRAMOS", "Gramos"),
        new("KILOGRAMOS", "Kilogramos"),
        new("TONELADAS", "Toneladas"),
        new("ONZAS", "Onzas"),
        new("LIBRAS", "Libras"),
        new("QUINTALES", "Quintales")
    ];

    private static readonly OpcionUnidad[] Temperaturas =
    [
        new("CELSIUS", "Celsius"),
        new("FAHRENHEIT", "Fahrenheit"),
        new("KELVIN", "Kelvin"),
        new("RANKINE", "Rankine"),
        new("NEWTON", "Newton"),
        new("REAUMUR", "Réaumur")
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
