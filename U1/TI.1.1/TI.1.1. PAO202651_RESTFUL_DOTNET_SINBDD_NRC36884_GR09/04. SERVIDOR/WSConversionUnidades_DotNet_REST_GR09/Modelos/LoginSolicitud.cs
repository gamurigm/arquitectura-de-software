using System.ComponentModel.DataAnnotations;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record LoginSolicitud(
    [property: Required(ErrorMessage = "El usuario es obligatorio.")]
    string? Usuario,
    [property: Required(ErrorMessage = "La clave es obligatoria.")]
    string? Clave);
