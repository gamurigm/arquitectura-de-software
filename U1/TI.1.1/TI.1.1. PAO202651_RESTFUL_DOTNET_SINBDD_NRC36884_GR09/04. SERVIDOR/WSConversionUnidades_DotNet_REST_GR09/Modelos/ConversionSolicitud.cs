using System.ComponentModel.DataAnnotations;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record ConversionSolicitud(
    [property: Required(ErrorMessage = "El valor es obligatorio.")]
    double? Valor,
    [property: Required(ErrorMessage = "La unidad de origen es obligatoria.")]
    string? UnidadOrigen,
    [property: Required(ErrorMessage = "La unidad de destino es obligatoria.")]
    string? UnidadDestino);
