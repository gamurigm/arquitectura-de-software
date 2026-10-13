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
        if (!Enum.TryParse<Magnitud>(textoMagnitud, true, out var magnitud))
        {
            throw new ErrorApiException(
                StatusCodes.Status400BadRequest,
                "Magnitud inválida",
                $"La magnitud '{textoMagnitud}' no está soportada.");
        }

        if (solicitud.Valor is null || !double.IsFinite(solicitud.Valor.Value))
        {
            throw new ErrorApiException(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "El valor debe ser un número finito.");
        }

        var origen = solicitud.UnidadOrigen?.Trim().ToLowerInvariant();
        var destino = solicitud.UnidadDestino?.Trim().ToLowerInvariant();
        if (string.IsNullOrWhiteSpace(origen) || string.IsNullOrWhiteSpace(destino))
        {
            throw new ErrorApiException(
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
            throw new ErrorApiException(
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                "El resultado no se puede representar como un número finito.");
        }

        var redondeado = Math.Floor(resultado * 1_000_000d + 0.5d) / 1_000_000d;
        var mensaje = $"{Formatear(valor)} {origen} equivalen a {Formatear(redondeado)} {destino}";
        return new ConversionRespuesta(true, magnitud.ToString().ToLowerInvariant(), valor, origen, destino, redondeado, mensaje);
    }

    private void ValidarUnidad(Magnitud magnitud, string unidad)
    {
        if (!catalogo.EsUnidadValida(magnitud, unidad))
        {
            var disponibles = string.Join(", ", catalogo.UnidadesDe(magnitud));
            throw new ErrorApiException(
                StatusCodes.Status400BadRequest,
                "Unidad inválida",
                $"La unidad '{unidad}' no es válida para {magnitud.ToString().ToLowerInvariant()}. Disponibles: {disponibles}");
        }
    }

    private static string Formatear(double valor) =>
        valor.ToString("G", CultureInfo.InvariantCulture);
}
