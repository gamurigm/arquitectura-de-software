using System.ComponentModel.DataAnnotations;

namespace EurekaBank.ClienteWeb.Modelos;

public sealed class LoginViewModel
{
    [Required, StringLength(30, MinimumLength = 1)]
    public string Usuario { get; set; } = string.Empty;

    [Required, StringLength(1024, MinimumLength = 1)]
    public string Contrasena { get; set; } = string.Empty;
}
