using System.ComponentModel.DataAnnotations;

namespace ClienteWebGR09.Models;

public sealed class ConversionViewModel
{
    public Dictionary<string, List<string>> Catalogo { get; set; } = new();

    [Required]
    public string Magnitud { get; set; } = "longitud";

    [Required(ErrorMessage = "Ingrese un valor.")]
    public double? Valor { get; set; }

    [Required(ErrorMessage = "Seleccione la unidad de origen.")]
    public string UnidadOrigen { get; set; } = "";

    [Required(ErrorMessage = "Seleccione la unidad de destino.")]
    public string UnidadDestino { get; set; } = "";

    public string? Resultado { get; set; }
    public string? MensajeError { get; set; }
}
