namespace CONUNI_SOAP_DOTNET_CLIWEB_G09.Models
{
    /// <summary>
    /// Modelo para las respuestas de conversión
    /// </summary>
    public class ConversionResponse
    {
        public double ValorOriginal { get; set; }
        public double ValorConvertido { get; set; }
        public string UnidadOrigen { get; set; } = string.Empty;
        public string UnidadDestino { get; set; } = string.Empty;
        public string Categoria { get; set; } = string.Empty;
        public bool Exito { get; set; }
        public string Mensaje { get; set; } = string.Empty;
        public long Timestamp { get; set; }
    }
}


