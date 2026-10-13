using System.ComponentModel.DataAnnotations;

namespace ClienteWebGR09.Models;

public sealed class LoginViewModel
{
    [Required(ErrorMessage = "Ingresa el usuario.")]
    public string Usuario { get; set; } = string.Empty;

    [Required(ErrorMessage = "Ingresa la contraseña.")]
    [DataType(DataType.Password)]
    public string Contrasenia { get; set; } = string.Empty;
}
