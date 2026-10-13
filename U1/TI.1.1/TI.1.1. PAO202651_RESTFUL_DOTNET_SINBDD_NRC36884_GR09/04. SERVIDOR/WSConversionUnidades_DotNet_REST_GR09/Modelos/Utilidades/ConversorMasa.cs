using WSConversionUnidades_DotNet_REST_GR09.Modelos.Unidades;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos.Utilidades;

public sealed class ConversorMasa : IConversor<UnidadMasa>
{
    public double Convertir(double valor, UnidadMasa unidadOrigen, UnidadMasa unidadDestino) =>
        valor * Factor(unidadOrigen) / Factor(unidadDestino);

    private static double Factor(UnidadMasa unidad) => unidad switch
    {
        UnidadMasa.Miligramos => 0.000001,
        UnidadMasa.Gramos => 0.001,
        UnidadMasa.Kilogramos => 1,
        UnidadMasa.Toneladas => 1000,
        UnidadMasa.Onzas => 0.028349523125,
        UnidadMasa.Libras => 0.45359237,
        UnidadMasa.Quintales => 100,
        _ => throw new ArgumentOutOfRangeException(nameof(unidad))
    };
}
