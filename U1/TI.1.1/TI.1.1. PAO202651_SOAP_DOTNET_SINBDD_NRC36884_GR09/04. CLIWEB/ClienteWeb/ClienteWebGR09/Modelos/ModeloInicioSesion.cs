using System.ComponentModel.DataAnnotations;

namespace ClienteWebGR09.Modelos;

public sealed class ModeloInicioSesion
{
    [Required(ErrorMessage = "Ingresa el usuario.")]
    public string Usuario { get; set; } = "Monster";

    [Required(ErrorMessage = "Ingresa la contraseña.")]
    [DataType(DataType.Password)]
    public string Contrasenia { get; set; } = "Monster9";
}
