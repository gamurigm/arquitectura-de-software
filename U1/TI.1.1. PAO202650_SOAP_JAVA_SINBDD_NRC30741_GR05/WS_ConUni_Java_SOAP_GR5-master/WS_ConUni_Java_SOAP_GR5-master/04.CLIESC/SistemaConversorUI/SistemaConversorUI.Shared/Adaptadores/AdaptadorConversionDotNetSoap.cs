using System.Globalization;
using System.Text;
using System.Xml.Linq;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorConversionDotNetSoap : IAdaptadorConversion
{
    public TipoBackend Tipo => TipoBackend.DotNetSoap;
    private readonly HttpClient _httpClient;

    public AdaptadorConversionDotNetSoap(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<double> ConvertirAsync(PeticionConversion peticion, string token)
    {
        string metodoWs = peticion.TipoConversion switch
        {
            "Masa" => "convertirMasa",
            "Temperatura" => "convertirTemperatura",
            _ => "convertirLongitud"
        };

        string valorFormateado = peticion.Valor.ToString(CultureInfo.InvariantCulture);

        string soapXml = $@"<soapenv:Envelope xmlns:soapenv=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:tem=""http://tempuri.org/"">
                               <soapenv:Header>
                                  <tem:token>{token}</tem:token>
                               </soapenv:Header>
                               <soapenv:Body>
                                  <tem:{metodoWs}>
                                     <tem:valor>{valorFormateado}</tem:valor>
                                     <tem:unidadInicial>{peticion.UnidadOrigen}</tem:unidadInicial>
                                     <tem:unidadFinal>{peticion.UnidadDestino}</tem:unidadFinal>
                                  </tem:{metodoWs}>
                               </soapenv:Body>
                            </soapenv:Envelope>";

        var request = new HttpRequestMessage(HttpMethod.Post, ConstantesRed.UrlDotNetSoap)
        {
            Content = new StringContent(soapXml, Encoding.UTF8, "text/xml")
        };
        request.Headers.Add("SOAPAction", $"http://tempuri.org/WSConversorUnidades/{metodoWs}");

        var response = await _httpClient.SendAsync(request);
        string responseXml = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
        {
            var docFault = XDocument.Parse(responseXml);
            var faultString = docFault.Descendants().FirstOrDefault(x => x.Name.LocalName == "faultstring")?.Value;
            throw new Exception($"Error SOAP .NET: {faultString ?? response.StatusCode.ToString()}");
        }

        var doc = XDocument.Parse(responseXml);
        var resultElement = doc.Descendants().FirstOrDefault(x => x.Name.LocalName == $"{metodoWs}Result");

        if (resultElement != null && double.TryParse(resultElement.Value, NumberStyles.Any, CultureInfo.InvariantCulture, out double resultado))
        {
            return resultado;
        }

        throw new Exception("No se pudo parsear el resultado matemático del XML de .NET.");
    }
}