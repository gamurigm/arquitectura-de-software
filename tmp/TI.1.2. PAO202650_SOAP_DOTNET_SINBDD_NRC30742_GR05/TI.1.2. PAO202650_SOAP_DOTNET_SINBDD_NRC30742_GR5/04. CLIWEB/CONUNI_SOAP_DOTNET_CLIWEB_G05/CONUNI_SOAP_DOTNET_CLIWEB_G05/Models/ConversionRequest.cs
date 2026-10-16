namespace CONUNI_SOAP_DOTNET_CLIWEB_G09.Models
{
    /// <summary>
    /// Modelo para las peticiones de conversión
    /// </summary>
    public class ConversionRequest
    {
        public double Valor { get; set; }
        public string UnidadOrigen { get; set; } = string.Empty;
        public string UnidadDestino { get; set; } = string.Empty;
        public string Categoria { get; set; } = string.Empty;
    }
}


