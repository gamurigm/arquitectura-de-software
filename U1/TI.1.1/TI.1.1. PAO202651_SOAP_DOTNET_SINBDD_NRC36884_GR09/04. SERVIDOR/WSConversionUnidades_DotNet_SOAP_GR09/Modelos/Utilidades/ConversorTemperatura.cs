using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Unidades;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Utilidades;

public sealed class ConversorTemperatura : IConversor<UnidadTemperatura>
{
    public double Convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino)
    {
        if (origen == destino)
        {
            return valor;
        }

        var celsius = origen switch
        {
            UnidadTemperatura.CELSIUS => valor,
            UnidadTemperatura.FAHRENHEIT => (valor - 32d) * 5d / 9d,
            UnidadTemperatura.KELVIN => valor - 273.15d,
            UnidadTemperatura.RANKINE => valor * 5d / 9d - 273.15d,
            UnidadTemperatura.NEWTON => valor * 100d / 33d,
            UnidadTemperatura.REAUMUR => valor * 5d / 4d,
            _ => throw new ArgumentOutOfRangeException(nameof(origen))
        };

        return destino switch
        {
            UnidadTemperatura.CELSIUS => celsius,
            UnidadTemperatura.FAHRENHEIT => celsius * 9d / 5d + 32d,
            UnidadTemperatura.KELVIN => celsius + 273.15d,
            UnidadTemperatura.RANKINE => (celsius + 273.15d) * 9d / 5d,
            UnidadTemperatura.NEWTON => celsius * 33d / 100d,
            UnidadTemperatura.REAUMUR => celsius * 4d / 5d,
            _ => throw new ArgumentOutOfRangeException(nameof(destino))
        };
    }
}
