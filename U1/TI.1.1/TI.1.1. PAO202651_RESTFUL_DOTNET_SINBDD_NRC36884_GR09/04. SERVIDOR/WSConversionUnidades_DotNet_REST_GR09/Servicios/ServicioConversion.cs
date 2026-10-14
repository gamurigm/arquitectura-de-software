using System.Globalization;
using WSConversionUnidades_DotNet_REST_GR09.Excepciones;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;
using WSConversionUnidades_DotNet_REST_GR09.Modelos.Unidades;
using WSConversionUnidades_DotNet_REST_GR09.Modelos.Utilidades;

namespace WSConversionUnidades_DotNet_REST_GR09.Servicios;

public sealed class ServicioConversion(
    ServicioCatalogo catalogo,
    IConversor<UnidadLongitud> conversorLongitud,
    IConversor<UnidadMasa> conversorMasa,
    IConversor<UnidadTemperatura> conversorTemperatura)
{
    public ConversionRespuesta Convertir(string textoMagnitud, ConversionSolicitud solicitud)
    {
        if (solicitud is null)
        {
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "La solicitud de conversión es obligatoria.");
        }

        var nombreMagnitud = textoMagnitud?.Trim();
        if (string.IsNullOrWhiteSpace(nombreMagnitud)
            || !Enum.TryParse<Magnitud>(nombreMagnitud, true, out var magnitud)
            || !Enum.IsDefined(magnitud)
            || !Enum.GetNames<Magnitud>().Contains(nombreMagnitud, StringComparer.OrdinalIgnoreCase))
        {
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Magnitud inválida",
                $"La magnitud '{textoMagnitud}' no está soportada.");
        }

        if (solicitud.Valor is null || !double.IsFinite(solicitud.Valor.Value))
        {
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "El valor debe ser un número finito.");
        }

        if (magnitud == Magnitud.Masa && solicitud.Valor.Value < 0)
        {
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "La masa no puede ser negativa");
        }

        var origen = solicitud.UnidadOrigen?.Trim().ToLowerInvariant();
        var destino = solicitud.UnidadDestino?.Trim().ToLowerInvariant();
        if (string.IsNullOrWhiteSpace(origen) || string.IsNullOrWhiteSpace(destino))
        {
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "Las unidades de origen y destino son obligatorias.");
        }

        ValidarUnidad(magnitud, origen);
        ValidarUnidad(magnitud, destino);

        var valor = solicitud.Valor.Value;
        var resultado = magnitud switch
        {
            Magnitud.Longitud => conversorLongitud.Convertir(
                valor, Enum.Parse<UnidadLongitud>(origen, true), Enum.Parse<UnidadLongitud>(destino, true)),
            Magnitud.Masa => conversorMasa.Convertir(
                valor, Enum.Parse<UnidadMasa>(origen, true), Enum.Parse<UnidadMasa>(destino, true)),
            Magnitud.Temperatura => conversorTemperatura.Convertir(
                valor, Enum.Parse<UnidadTemperatura>(origen, true), Enum.Parse<UnidadTemperatura>(destino, true)),
            _ => throw new InvalidOperationException("Magnitud no implementada.")
        };

        if (!double.IsFinite(resultado))
        {
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "El resultado no se puede representar como un número finito.");
        }

        var redondeado = Math.Round(resultado, 6, MidpointRounding.AwayFromZero);
        var mensaje = $"{Formatear(valor)} {origen} equivalen a {Formatear(redondeado)} {destino}";
        return new ConversionRespuesta(true, magnitud.ToString().ToLowerInvariant(), valor, origen, destino, redondeado, mensaje);
    }

    private void ValidarUnidad(Magnitud magnitud, string unidad)
    {
        if (!catalogo.EsUnidadValida(magnitud, unidad))
        {
            var disponibles = string.Join(", ", catalogo.UnidadesDe(magnitud));
            throw new ExcepcionApi(
                StatusCodes.Status400BadRequest,
                "Unidad inválida",
                $"La unidad '{unidad}' no es válida para {magnitud.ToString().ToLowerInvariant()}. Disponibles: {disponibles}");
        }
    }

    private static string Formatear(double valor) =>
        valor.ToString("G", CultureInfo.InvariantCulture);
}
