using System.ComponentModel.DataAnnotations;

namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record LoginSolicitud(
    [param: Required(ErrorMessage = "El usuario es obligatorio.")]
    string? Usuario,
    [param: Required(ErrorMessage = "La clave es obligatoria.")]
    string? Clave);
