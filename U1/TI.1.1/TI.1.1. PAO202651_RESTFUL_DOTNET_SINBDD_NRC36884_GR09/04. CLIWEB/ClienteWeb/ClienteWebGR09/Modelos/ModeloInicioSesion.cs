using System.ComponentModel.DataAnnotations;

namespace ClienteWebGR09.Modelos;

public sealed class ModeloInicioSesion
{
    [Required(ErrorMessage = "Ingrese el usuario.")]
    public string Usuario { get; set; } = "Monster";

    [Required(ErrorMessage = "Ingrese la contraseña.")]
    [DataType(DataType.Password)]
    public string Clave { get; set; } = "Monster9";
}
