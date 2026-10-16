namespace CONUNI_SOAP_DOTNET_CLIESC_G09.modelo
{
    /// <summary>
    /// Modelo para las respuestas de conversión
    /// </summary>
    public class ConversionResponse
    {
        public double ValorOriginal { get; set; }
        public double ValorConvertido { get; set; }
        public string UnidadOrigen { get; set; }
        public string UnidadDestino { get; set; }
        public string Categoria { get; set; }
        public bool Exito { get; set; }
        public string Mensaje { get; set; }
        public long Timestamp { get; set; }
    }
}


