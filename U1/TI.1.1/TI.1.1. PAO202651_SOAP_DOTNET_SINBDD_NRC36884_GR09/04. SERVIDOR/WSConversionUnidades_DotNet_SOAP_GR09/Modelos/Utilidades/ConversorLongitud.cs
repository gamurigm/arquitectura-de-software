using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Unidades;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Utilidades;

public sealed class ConversorLongitud : IConversor<UnidadLongitud>
{
    public double Convertir(double valor, UnidadLongitud unidadOrigen, UnidadLongitud unidadDestino) =>
        valor * Factor(unidadOrigen) / Factor(unidadDestino);

    private static double Factor(UnidadLongitud unidad) => unidad switch
    {
        UnidadLongitud.MILIMETRO => 0.001,
        UnidadLongitud.CENTIMETRO => 0.01,
        UnidadLongitud.METRO => 1.0,
        UnidadLongitud.KILOMETRO => 1000.0,
        UnidadLongitud.YARDA => 0.9144,
        _ => throw new ArgumentOutOfRangeException(nameof(unidad))
    };
}
