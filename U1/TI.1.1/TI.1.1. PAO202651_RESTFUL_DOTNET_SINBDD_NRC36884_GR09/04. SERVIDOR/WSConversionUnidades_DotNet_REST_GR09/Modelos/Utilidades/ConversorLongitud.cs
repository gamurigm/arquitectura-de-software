using WSConversionUnidades_DotNet_REST_GR09.Modelos.Unidades;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos.Utilidades;

public sealed class ConversorLongitud : IConversor<UnidadLongitud>
{
    public double Convertir(double valor, UnidadLongitud unidadOrigen, UnidadLongitud unidadDestino) =>
        valor * Factor(unidadOrigen) / Factor(unidadDestino);

    private static double Factor(UnidadLongitud unidad) => unidad switch
    {
        UnidadLongitud.Milimetros => 0.001,
        UnidadLongitud.Centimetros => 0.01,
        UnidadLongitud.Metros => 1,
        UnidadLongitud.Kilometros => 1000,
        UnidadLongitud.Pies => 0.3048,
        UnidadLongitud.Yardas => 0.9144,
        UnidadLongitud.Millas => 1609.344,
        _ => throw new ArgumentOutOfRangeException(nameof(unidad))
    };
}
