using System.Text;
using System.Xml.Linq;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorAutenticacionJavaSoap : IAdaptadorAutenticacion
{
    public TipoBackend Tipo => TipoBackend.JavaSoap;
    private readonly HttpClient _httpClient;

    public AdaptadorAutenticacionJavaSoap(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<string> IniciarSesionAsync(string usuario, string contrasenia)
    {
        // 1. Construir el sobre SOAP (Envelope)
        // Nota: El namespace "http://ws.monster.edu.ec/" es el que Java genera por defecto 
        string soapXml = $@"<soapenv:Envelope xmlns:soapenv=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:ws=""http://ws.controladores.monster.edu.ec/"">
                               <soapenv:Header/>
                               <soapenv:Body>
                                  <ws:login>
                                     <usuario>{usuario}</usuario>
                                     <contrasenia>{contrasenia}</contrasenia>
                                  </ws:login>
                               </soapenv:Body>
                            </soapenv:Envelope>";

        var content = new StringContent(soapXml, Encoding.UTF8, "text/xml");


        // 2. Enviar la petición a Payara
        var response = await _httpClient.PostAsync(ConstantesRed.UrlJavaSoap, content);

        if (!response.IsSuccessStatusCode)
            throw new Exception($"Error HTTP: {response.StatusCode}");

        // 3. Leer e interpretar la respuesta XML
        string responseXml = await response.Content.ReadAsStringAsync();
        var doc = XDocument.Parse(responseXml);

        // Buscamos la etiqueta <return> que contiene el token (comportamiento estándar de JAX-WS)
        var tokenElement = doc.Descendants().FirstOrDefault(x => x.Name.LocalName == "return");

        if (tokenElement == null)
            throw new Exception("No se encontró el token en la respuesta SOAP.");

        return tokenElement.Value;
    }

    public async Task<string> CambiarContraseniaAsync(string contraseniaActual, string contraseniaNueva, string token)
    {
        // Construimos el XML respetando los @WebParam de tu Java
        string soapXml = $@"<soapenv:Envelope xmlns:soapenv=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:ws=""http://ws.controladores.monster.edu.ec/"">
                               <soapenv:Header/>
                               <soapenv:Body>
                                  <ws:cambiarContrasenia>
                                     <contraseniaActual>{contraseniaActual}</contraseniaActual>
                                     <contraseniaNueva>{contraseniaNueva}</contraseniaNueva>
                                  </ws:cambiarContrasenia>
                               </soapenv:Body>
                            </soapenv:Envelope>";

        var request = new HttpRequestMessage(HttpMethod.Post, ConstantesRed.UrlJavaSoap)
        {
            Content = new StringContent(soapXml, System.Text.Encoding.UTF8, "text/xml")
        };
        request.Headers.ExpectContinue = false;
        request.Headers.Add("SOAPAction", "\"\"");

        var response = await _httpClient.SendAsync(request);
        string responseXml = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
        {
            var docFault = System.Xml.Linq.XDocument.Parse(responseXml);
            var faultString = docFault.Descendants().FirstOrDefault(x => x.Name.LocalName == "faultstring")?.Value;
            throw new Exception($"Error: {faultString ?? "No se pudo cambiar la contraseña"}");
        }

        var doc = System.Xml.Linq.XDocument.Parse(responseXml);
        var resultElement = doc.Descendants().FirstOrDefault(x => x.Name.LocalName == "return");

        return resultElement?.Value ?? "Contraseña actualizada correctamente";
    }
}