using System.ComponentModel.DataAnnotations;
using ClienteWebGR09.Modelos;

namespace ClienteWebGR09.Modelos;

public sealed class ModeloConversion
{
    public TipoConversion Tipo { get; set; } = TipoConversion.Longitud;

    [Required(ErrorMessage = "Ingresa un valor.")]
    public string Valor { get; set; } = string.Empty;

    [Required(ErrorMessage = "Selecciona la unidad inicial.")]
    public string UnidadInicial { get; set; } = "METRO";

    [Required(ErrorMessage = "Selecciona la unidad final.")]
    public string UnidadFinal { get; set; } = "KILOMETRO";

    public string? Resultado { get; set; }
}
