using System.ComponentModel.DataAnnotations;

namespace ClienteWebGR09.Models;

public sealed class CambiarContraseniaViewModel
{
    [Required(ErrorMessage = "Ingresa la contraseña actual.")]
    [DataType(DataType.Password)]
    public string ContraseniaActual { get; set; } = string.Empty;

    [Required(ErrorMessage = "Ingresa la contraseña nueva.")]
    [DataType(DataType.Password)]
    public string ContraseniaNueva { get; set; } = string.Empty;
}
