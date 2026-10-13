using WSConversionUnidades_DotNet_REST_GR09.Modelos.Unidades;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos.Utilidades;

public sealed class ConversorTemperatura : IConversor<UnidadTemperatura>
{
    public double Convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino)
    {
        var celsius = origen switch
        {
            UnidadTemperatura.Celsius => valor,
            UnidadTemperatura.Fahrenheit => (valor - 32d) * 5d / 9d,
            UnidadTemperatura.Kelvin => valor - 273.15d,
            UnidadTemperatura.Newton => valor * 100d / 33d,
            UnidadTemperatura.Reaumur => valor * 5d / 4d,
            _ => throw new ArgumentOutOfRangeException(nameof(origen))
        };

        return destino switch
        {
            UnidadTemperatura.Celsius => celsius,
            UnidadTemperatura.Fahrenheit => celsius * 9d / 5d + 32d,
            UnidadTemperatura.Kelvin => celsius + 273.15d,
            UnidadTemperatura.Newton => celsius * 33d / 100d,
            UnidadTemperatura.Reaumur => celsius * 4d / 5d,
            _ => throw new ArgumentOutOfRangeException(nameof(destino))
        };
    }
}
