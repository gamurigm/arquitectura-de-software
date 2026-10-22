using System.Globalization;
using System.Text;
using System.Xml;
using System.Xml.Linq;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Servicios;

public sealed class EurekaSoapClient : IEurekaSoapClient, IDisposable
{
    private const string EspacioNombres = "http://ws.monster.edu.ec/";
    private readonly HttpClient _http;
    private readonly bool _disposeHttp;
    private readonly Uri _endpoint;

    public EurekaSoapClient() : this(new HttpClient(), ResolverEndpoint(), disposeHttp: true) { }

    public EurekaSoapClient(HttpClient http) : this(http, ResolverEndpoint(), disposeHttp: false) { }

    public EurekaSoapClient(string endpoint) : this(new HttpClient(), new Uri(endpoint), disposeHttp: true) { }

    private EurekaSoapClient(HttpClient http, Uri endpoint, bool disposeHttp)
    {
        _http = http;
        _endpoint = endpoint;
        _disposeHttp = disposeHttp;
    }

    public async Task<int> LoginAsync(string usuario, string contrasena, CancellationToken cancellationToken = default)
    {
        var response = await EnviarAsync("login", writer =>
        {
            EscribirParametro(writer, "usuario", usuario);
            EscribirParametro(writer, "contrasena", contrasena);
        }, cancellationToken);
        return LeerEntero(response, "login", "return");
    }

    public async Task<string?> CrearSesionAsync(string usuario, string contrasena, CancellationToken cancellationToken = default)
    {
        var response = await EnviarAsync("crearSesion", writer =>
        {
            EscribirParametro(writer, "usuario", usuario);
            EscribirParametro(writer, "contrasena", contrasena);
        }, cancellationToken);
        var token = response.Descendants().FirstOrDefault(elemento =>
            elemento.Name.LocalName.Equals("token", StringComparison.OrdinalIgnoreCase))?.Value;
        return string.IsNullOrWhiteSpace(token) ? null : token;
    }

    public async Task<IReadOnlyList<Movimiento>> TraerMovimientosAsync(string cuenta, CancellationToken cancellationToken = default)
    {
        var response = await EnviarAsync("traerMovimientos", writer => EscribirParametro(writer, "cuenta", cuenta), cancellationToken);
        var movimientos = response.Descendants()
            .Where(elemento => elemento.Name.LocalName.Equals("movimiento", StringComparison.OrdinalIgnoreCase)
                || elemento.Name.LocalName.Equals("Movimiento", StringComparison.OrdinalIgnoreCase))
            .Where(elemento => elemento.Elements().Any(hijo => hijo.Name.LocalName.Equals("cuenta", StringComparison.OrdinalIgnoreCase)))
            .Select(LeerMovimiento)
            .ToArray();
        return movimientos;
    }

    public async Task<int> RegistrarDepositoAsync(string cuenta, decimal importe, CancellationToken cancellationToken = default)
    {
        var response = await EnviarAsync("regDeposito", writer =>
        {
            EscribirParametro(writer, "cuenta", cuenta);
            EscribirParametro(writer, "importe", importe.ToString("0.00", CultureInfo.InvariantCulture));
        }, cancellationToken);
        return LeerEntero(response, "resultado", "regDeposito", "return");
    }

    public async Task<int> TransferirAsync(string token, string origen, string destino, decimal importe,
        Guid claveOperacion, CancellationToken cancellationToken = default)
    {
        var response = await EnviarAsync("transferir", writer =>
        {
            EscribirParametro(writer, "token", token);
            EscribirParametro(writer, "cuentaOrigen", origen);
            EscribirParametro(writer, "cuentaDestino", destino);
            EscribirParametro(writer, "importe", importe.ToString("0.00", CultureInfo.InvariantCulture));
            EscribirParametro(writer, "claveOperacion", claveOperacion.ToString());
        }, cancellationToken);
        return LeerEntero(response, "resultado", "transferir", "return");
    }

    public async Task<int> CerrarSesionAsync(string token, CancellationToken cancellationToken = default)
    {
        var response = await EnviarAsync("cerrarSesion", writer => EscribirParametro(writer, "token", token), cancellationToken);
        return LeerEntero(response, "resultado", "cerrarSesion", "return");
    }

    private async Task<XDocument> EnviarAsync(string operacion, Action<XmlWriter> escribirParametros, CancellationToken cancellationToken)
    {
        var contenido = new StringBuilder();
        var configuracion = new XmlWriterSettings { OmitXmlDeclaration = true, Encoding = Encoding.UTF8 };
        using (var writer = XmlWriter.Create(contenido, configuracion))
        {
            writer.WriteStartElement("soap", "Envelope", "http://schemas.xmlsoap.org/soap/envelope/");
            writer.WriteStartElement("soap", "Body", "http://schemas.xmlsoap.org/soap/envelope/");
            writer.WriteStartElement("ns", operacion, EspacioNombres);
            escribirParametros(writer);
            writer.WriteEndElement();
            writer.WriteEndElement();
            writer.WriteEndElement();
        }

        using var request = new HttpRequestMessage(HttpMethod.Post, _endpoint)
        {
            Content = new StringContent(contenido.ToString(), Encoding.UTF8, "text/xml")
        };
        request.Headers.TryAddWithoutValidation("SOAPAction", "\"\"");
        using var response = await _http.SendAsync(request, HttpCompletionOption.ResponseHeadersRead, cancellationToken);
        var xml = await response.Content.ReadAsStringAsync(cancellationToken);
        var documento = LeerXml(xml);
        var fallo = documento.Descendants().FirstOrDefault(elemento => elemento.Name.LocalName == "Fault");
        if (fallo is not null)
            throw new InvalidOperationException(fallo.Descendants().FirstOrDefault(elemento => elemento.Name.LocalName is "faultstring" or "Text")?.Value ?? "Falló la operación SOAP.");
        if (!response.IsSuccessStatusCode)
            throw new HttpRequestException($"El servicio SOAP respondió HTTP {(int)response.StatusCode}.");
        return documento;
    }

    private static XDocument LeerXml(string xml)
    {
        using var stringReader = new StringReader(xml);
        using var reader = XmlReader.Create(stringReader, new XmlReaderSettings { DtdProcessing = DtdProcessing.Prohibit, XmlResolver = null });
        return XDocument.Load(reader);
    }

    private static void EscribirParametro(XmlWriter writer, string nombre, string valor)
    {
        writer.WriteElementString("ns", nombre, EspacioNombres, valor);
    }

    private static int LeerEntero(XDocument documento, params string[] nombres)
    {
        var resultado = documento.Descendants().FirstOrDefault(elemento => nombres.Contains(elemento.Name.LocalName, StringComparer.OrdinalIgnoreCase))
            ?? throw new InvalidDataException("La respuesta SOAP no contiene el resultado esperado.");
        return int.Parse(resultado.Value, CultureInfo.InvariantCulture);
    }

    private static Movimiento LeerMovimiento(XElement elemento) => new()
    {
        Cuenta = LeerTexto(elemento, "cuenta"),
        Nromov = LeerInt(elemento, "nromov"),
        Fecha = LeerFecha(elemento, "fecha"),
        Tipo = LeerTexto(elemento, "tipo"),
        Accion = LeerTexto(elemento, "accion"),
        Importe = LeerDecimal(elemento, "importe")
    };

    private static string LeerTexto(XElement elemento, string nombre) => elemento.Elements().FirstOrDefault(hijo => hijo.Name.LocalName.Equals(nombre, StringComparison.OrdinalIgnoreCase))?.Value ?? "";
    private static int LeerInt(XElement elemento, string nombre) => int.Parse(LeerTexto(elemento, nombre), CultureInfo.InvariantCulture);
    private static decimal LeerDecimal(XElement elemento, string nombre) => decimal.Parse(LeerTexto(elemento, nombre), NumberStyles.Number, CultureInfo.InvariantCulture);
    private static DateTime LeerFecha(XElement elemento, string nombre) => DateTime.Parse(LeerTexto(elemento, nombre), CultureInfo.InvariantCulture, DateTimeStyles.RoundtripKind);

    private static Uri ResolverEndpoint()
    {
        var valor = Environment.GetEnvironmentVariable("EUREKABANK_SOAP_DOTNET_URL");
        return new Uri(string.IsNullOrWhiteSpace(valor) ? "http://localhost:5080/WSEureka" : valor);
    }

    public void Dispose()
    {
        if (_disposeHttp)
            _http.Dispose();
    }
}
