using System.Globalization;
using System.Net.Http;
using System.Text;
using System.Xml.Linq;
using ClienteEscritorioGR09.Modelos;

namespace ClienteEscritorioGR09.Servicios;

public sealed class ClienteSoap
{
    private const string EspacioNombres = "http://ws.controladores.monster.edu.ec/";
    private const string NombreContrato = "WSConversionUnidades_Java_SOAP_GR09";
    private const string EspacioSoap = "http://schemas.xmlsoap.org/soap/envelope/";
    private static readonly HttpClient ClienteHttp = new() { Timeout = TimeSpan.FromSeconds(20) };
    private readonly Uri _endpoint;

    public ClienteSoap(string? endpoint = null)
    {
        var direccion = endpoint
            ?? Environment.GetEnvironmentVariable("CONVERSOR_SOAP_URL")
            ?? "http://127.0.0.1:19081/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09";
        _endpoint = new Uri(direccion, UriKind.Absolute);
    }

    public Task<string> LoginAsync(string usuario, string contrasenia, CancellationToken cancellationToken = default) =>
        EnviarAsync("login", null, cancellationToken,
            ("usuario", usuario),
            ("contrasenia", contrasenia));

    public Task<string> CambiarContraseniaAsync(string token, string contraseniaActual, string contraseniaNueva, CancellationToken cancellationToken = default) =>
        EnviarAsync("cambiarContrasenia", token, cancellationToken,
            ("contraseniaActual", contraseniaActual),
            ("contraseniaNueva", contraseniaNueva));

    public async Task<double> ConvertirAsync(
        string token,
        TipoConversion tipo,
        double valor,
        string unidadInicial,
        string unidadFinal,
        CancellationToken cancellationToken = default)
    {
        var operacion = CatalogoUnidades.Operacion(tipo);
        var resultado = await EnviarAsync(operacion, token, cancellationToken,
            ("valor", valor.ToString("R", CultureInfo.InvariantCulture)),
            ("unidadInicial", unidadInicial),
            ("unidadFinal", unidadFinal));
        return double.Parse(resultado, NumberStyles.Float, CultureInfo.InvariantCulture);
    }

    private async Task<string> EnviarAsync(
        string operacion,
        string? token,
        CancellationToken cancellationToken,
        params (string Nombre, string Valor)[] parametros)
    {
        XNamespace soap = EspacioSoap;
        XNamespace servicio = EspacioNombres;
        var encabezado = new XElement(soap + "Header");
        if (!string.IsNullOrWhiteSpace(token))
        {
            encabezado.Add(new XElement(servicio + "token", token));
        }

        var solicitud = new XElement(servicio + operacion,
            parametros.Select(parametro => new XElement(servicio + parametro.Nombre, parametro.Valor)));
        var sobre = new XDocument(new XElement(soap + "Envelope",
            new XAttribute(XNamespace.Xmlns + "soap", soap),
            encabezado,
            new XElement(soap + "Body", solicitud)));

        using var peticion = new HttpRequestMessage(HttpMethod.Post, _endpoint);
        var accion = $"{EspacioNombres}{NombreContrato}/{operacion}";
        peticion.Headers.TryAddWithoutValidation("SOAPAction", $"\"{accion}\"");
        peticion.Content = new StringContent(
            sobre.ToString(SaveOptions.DisableFormatting),
            Encoding.UTF8,
            "text/xml");

        using var respuesta = await ClienteHttp.SendAsync(peticion, cancellationToken);
        var xml = await respuesta.Content.ReadAsStringAsync(cancellationToken);
        XDocument documento;
        try
        {
            documento = XDocument.Parse(xml);
        }
        catch (Exception excepcion) when (excepcion is System.Xml.XmlException or ArgumentException)
        {
            throw new HttpRequestException(
                $"El servidor devolvió HTTP {(int)respuesta.StatusCode} sin una respuesta SOAP válida.",
                excepcion);
        }

        var fallo = documento.Descendants().FirstOrDefault(elemento => elemento.Name.LocalName == "Fault");
        if (fallo is not null)
        {
            var detalle = fallo.Descendants()
                .FirstOrDefault(elemento => elemento.Name.LocalName is "faultstring" or "Text")
                ?.Value;
            throw new InvalidOperationException(detalle ?? "El servidor rechazó la operación SOAP.");
        }

        if (!respuesta.IsSuccessStatusCode)
        {
            throw new HttpRequestException($"El servidor respondió HTTP {(int)respuesta.StatusCode}.");
        }

        return documento.Descendants()
            .FirstOrDefault(elemento => elemento.Name.LocalName == "return")
            ?.Value
            ?? throw new InvalidOperationException("La respuesta SOAP no contiene el resultado esperado.");
    }
}
