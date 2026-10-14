using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Unidades;
using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Utilidades;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Servicios;

public sealed class ServicioConversor(
    IConversor<UnidadLongitud> conversorLongitud,
    IConversor<UnidadMasa> conversorMasa,
    IConversor<UnidadTemperatura> conversorTemperatura)
{
    public double ConvertirLongitud(double valor, string unidadInicial, string unidadFinal)
    {
        ValidarValor(valor);
        return ValidarResultado(conversorLongitud.Convertir(
            valor,
            ParsearUnidad<UnidadLongitud>(unidadInicial),
            ParsearUnidad<UnidadLongitud>(unidadFinal)));
    }

    public double ConvertirMasa(double valor, string unidadInicial, string unidadFinal)
    {
        ValidarValor(valor);
        if (valor < 0)
        {
            throw new ArgumentException("La masa no puede ser negativa");
        }

        return ValidarResultado(conversorMasa.Convertir(
            valor,
            ParsearUnidad<UnidadMasa>(unidadInicial),
            ParsearUnidad<UnidadMasa>(unidadFinal)));
    }

    public double ConvertirTemperatura(double valor, string unidadInicial, string unidadFinal)
    {
        ValidarValor(valor);
        return ValidarResultado(conversorTemperatura.Convertir(
            valor,
            ParsearUnidad<UnidadTemperatura>(unidadInicial),
            ParsearUnidad<UnidadTemperatura>(unidadFinal)));
    }

    private static T ParsearUnidad<T>(string unidad) where T : struct, Enum
    {
        if (string.IsNullOrWhiteSpace(unidad))
        {
            throw new ArgumentException("La unidad es obligatoria");
        }

        var nombre = unidad.Trim().ToUpperInvariant() switch
        {
            "MILIMETROS" => "MILIMETRO",
            "CENTIMETROS" => "CENTIMETRO",
            "METROS" => "METRO",
            "KILOMETROS" => "KILOMETRO",
            "YARDAS" => "YARDA",
            "MILIGRAMOS" => "MILIGRAMO",
            "GRAMOS" => "GRAMO",
            "KILOGRAMOS" => "KILOGRAMO",
            "TONELADAS" => "TONELADA",
            "ONZAS" => "ONZA",
            var otro => otro
        };
        foreach (var nombreValido in Enum.GetNames<T>())
        {
            if (string.Equals(nombre, nombreValido, StringComparison.OrdinalIgnoreCase))
            {
                return Enum.Parse<T>(nombreValido);
            }
        }

        throw new ArgumentException($"Unidad no soportada: {unidad}");
    }

    private static void ValidarValor(double valor)
    {
        if (!double.IsFinite(valor))
        {
            throw new ArgumentException("El valor debe ser un número finito");
        }
    }

    private static double ValidarResultado(double resultado)
    {
        if (!double.IsFinite(resultado))
        {
            throw new ArgumentException("El resultado excede el rango permitido");
        }

        return resultado;
    }
}
