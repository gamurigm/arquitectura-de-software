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

        return origen switch
        {
            UnidadTemperatura.CELSIUS => destino switch
            {
                UnidadTemperatura.FAHRENHEIT => valor * 9 / 5 + 32,
                UnidadTemperatura.KELVIN => valor + 273.15,
                UnidadTemperatura.RANKINE => (valor + 273.15) * 9 / 5,
                _ => throw new ArgumentOutOfRangeException(nameof(destino))
            },
            UnidadTemperatura.FAHRENHEIT => destino switch
            {
                UnidadTemperatura.CELSIUS => (valor - 32) * 5 / 9,
                UnidadTemperatura.KELVIN => (valor - 32) * 5 / 9 + 273.15,
                UnidadTemperatura.RANKINE => valor + 459.67,
                _ => throw new ArgumentOutOfRangeException(nameof(destino))
            },
            UnidadTemperatura.KELVIN => destino switch
            {
                UnidadTemperatura.CELSIUS => valor - 273.15,
                UnidadTemperatura.FAHRENHEIT => (valor - 273.15) * 9 / 5 + 32,
                UnidadTemperatura.RANKINE => valor * 9 / 5,
                _ => throw new ArgumentOutOfRangeException(nameof(destino))
            },
            UnidadTemperatura.RANKINE => destino switch
            {
                UnidadTemperatura.CELSIUS => (valor - 491.67) * 5 / 9,
                UnidadTemperatura.FAHRENHEIT => valor - 459.67,
                UnidadTemperatura.KELVIN => valor * 5 / 9,
                _ => throw new ArgumentOutOfRangeException(nameof(destino))
            },
            _ => throw new ArgumentOutOfRangeException(nameof(origen))
        };
    }
}
