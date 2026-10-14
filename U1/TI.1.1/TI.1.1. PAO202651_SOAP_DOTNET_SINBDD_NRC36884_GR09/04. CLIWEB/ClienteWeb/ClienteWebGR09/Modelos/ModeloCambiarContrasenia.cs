using System.ComponentModel.DataAnnotations;

namespace ClienteWebGR09.Modelos;

public sealed class ModeloCambiarContrasenia
{
    [Required(ErrorMessage = "Ingresa la contraseña actual.")]
    [DataType(DataType.Password)]
    public string ContraseniaActual { get; set; } = string.Empty;

    [Required(ErrorMessage = "Ingresa la contraseña nueva.")]
    [DataType(DataType.Password)]
    public string ContraseniaNueva { get; set; } = string.Empty;
}
