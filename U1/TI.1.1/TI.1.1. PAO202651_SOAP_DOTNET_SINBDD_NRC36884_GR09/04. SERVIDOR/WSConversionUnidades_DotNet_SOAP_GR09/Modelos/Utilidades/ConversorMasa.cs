using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Unidades;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Utilidades;

public sealed class ConversorMasa : IConversor<UnidadMasa>
{
    public double Convertir(double valor, UnidadMasa unidadOrigen, UnidadMasa unidadDestino) =>
        valor * Factor(unidadOrigen) / Factor(unidadDestino);

    private static double Factor(UnidadMasa unidad) => unidad switch
    {
        UnidadMasa.MILIGRAMO => 0.001,
        UnidadMasa.GRAMO => 1.0,
        UnidadMasa.KILOGRAMO => 1000.0,
        UnidadMasa.TONELADA => 1_000_000.0,
        UnidadMasa.ONZA => 28.3495,
        _ => throw new ArgumentOutOfRangeException(nameof(unidad))
    };
}
