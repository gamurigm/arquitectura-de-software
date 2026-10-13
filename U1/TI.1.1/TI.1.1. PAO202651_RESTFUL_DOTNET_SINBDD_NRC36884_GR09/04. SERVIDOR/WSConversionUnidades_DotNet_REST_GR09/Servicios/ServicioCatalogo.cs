using WSConversionUnidades_DotNet_REST_GR09.Modelos;

namespace WSConversionUnidades_DotNet_REST_GR09.Servicios;

public sealed class ServicioCatalogo
{
    private static readonly Dictionary<Magnitud, List<string>> Unidades = new()
    {
        [Magnitud.Longitud] = ["centimetros", "metros", "pies", "yardas", "millas"],
        [Magnitud.Masa] = ["gramos", "kilogramos", "onzas", "libras", "quintales"],
        [Magnitud.Temperatura] = ["fahrenheit", "kelvin", "celsius", "newton", "reaumur"]
    };

    public CatalogoRespuesta ObtenerCatalogo() =>
        new(
            true,
            new Dictionary<string, List<string>>(StringComparer.Ordinal)
            {
                ["longitud"] = [.. Unidades[Magnitud.Longitud]],
                ["masa"] = [.. Unidades[Magnitud.Masa]],
                ["temperatura"] = [.. Unidades[Magnitud.Temperatura]]
            },
            "Catálogo de unidades disponibles.");

    public bool EsUnidadValida(Magnitud magnitud, string unidad) =>
        Unidades[magnitud].Contains(unidad, StringComparer.OrdinalIgnoreCase);

    public IReadOnlyList<string> UnidadesDe(Magnitud magnitud) => Unidades[magnitud];
}
