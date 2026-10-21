using System.Text;
using System.Xml.Linq;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorAutenticacionDotNetSoap : IAdaptadorAutenticacion
{
    public TipoBackend Tipo => TipoBackend.DotNetSoap;
    private readonly HttpClient _httpClient;

    public AdaptadorAutenticacionDotNetSoap(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<string> IniciarSesionAsync(string usuario, string contrasenia)
    {
        string soapXml = $@"<soapenv:Envelope xmlns:soapenv=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:tem=""http://tempuri.org/"">
                               <soapenv:Header/>
                               <soapenv:Body>
                                  <tem:login>
                                     <tem:usuario>{usuario}</tem:usuario>
                                     <tem:contrasenia>{contrasenia}</tem:contrasenia>
                                  </tem:login>
                               </soapenv:Body>
                            </soapenv:Envelope>";

        var request = new HttpRequestMessage(HttpMethod.Post, ConstantesRed.UrlDotNetSoap)
        {
            Content = new StringContent(soapXml, Encoding.UTF8, "text/xml")
        };

        request.Headers.Add("SOAPAction", "http://tempuri.org/WSConversorUnidades/login");

        var response = await _httpClient.SendAsync(request);
        string responseXml = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
        {
            try
            {
                var docFault = XDocument.Parse(responseXml);
                var faultString = docFault.Descendants().FirstOrDefault(x => x.Name.LocalName == "faultstring")?.Value;
                throw new Exception($"Error SOAP .NET: {faultString ?? response.StatusCode.ToString()}");
            }
            catch (System.Xml.XmlException)
            {
                // Si falla al leer el XML, es porque el servidor devolvió HTML (Ej. Error de IIS)
                throw new Exception($"Error de Servidor HTTP {response.StatusCode}. (Revisa la IP y el Firewall)");
            }
        }

        var doc = XDocument.Parse(responseXml);

        var resultElement = doc.Descendants().FirstOrDefault(x => x.Name.LocalName == "loginResult");

        if (resultElement == null)
            throw new Exception("No se encontró el token en la respuesta SOAP de .NET.");

        return resultElement.Value;
    }

    public Task<string> CambiarContraseniaAsync(string contraseniaActual, string contraseniaNueva, string token)
    {
        throw new NotImplementedException();
    }
}