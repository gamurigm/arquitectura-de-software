using System.ComponentModel.DataAnnotations;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record ConversionSolicitud(
    [param: Required(ErrorMessage = "El valor es obligatorio.")]
    double? Valor,
    [param: Required(ErrorMessage = "La unidad de origen es obligatoria.")]
    string? UnidadOrigen,
    [param: Required(ErrorMessage = "La unidad de destino es obligatoria.")]
    string? UnidadDestino);
