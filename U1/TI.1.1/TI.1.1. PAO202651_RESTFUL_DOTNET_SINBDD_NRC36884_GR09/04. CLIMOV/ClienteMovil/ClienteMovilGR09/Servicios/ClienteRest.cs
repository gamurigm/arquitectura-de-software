using System.Net.Http;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using ClienteMovilGR09.Modelos;

namespace ClienteMovilGR09.Servicios;

public sealed class ClienteRest
{
    private static readonly JsonSerializerOptions OpcionesJson = new(JsonSerializerDefaults.Web);
    private readonly HttpClient _clienteHttp;

    public ClienteRest(HttpClient? clienteHttp = null)
    {
        _clienteHttp = clienteHttp ?? new HttpClient();
        _clienteHttp.BaseAddress ??= new Uri(ObtenerUrlBase());
        _clienteHttp.Timeout = TimeSpan.FromSeconds(15);
    }

    public Task<TokenRespuesta> IniciarSesionAsync(SolicitudInicioSesion solicitud) =>
        EnviarAsync<TokenRespuesta>(HttpMethod.Post, "api/auth/login", solicitud);

    public Task<CatalogoRespuesta> ObtenerCatalogoAsync() =>
        EnviarAsync<CatalogoRespuesta>(HttpMethod.Get, "api/catalogo");

    public Task<ConversionRespuesta> ConvertirAsync(
        string magnitud,
        ConversionSolicitud solicitud,
        string token) =>
        EnviarAsync<ConversionRespuesta>(
            HttpMethod.Post,
            $"api/conversiones/{Uri.EscapeDataString(magnitud)}",
            solicitud,
            token);

    public Task<RespuestaOperacion> CerrarSesionAsync(string token) =>
        EnviarAsync<RespuestaOperacion>(HttpMethod.Post, "api/auth/logout", token: token);

    private async Task<T> EnviarAsync<T>(
        HttpMethod metodo,
        string ruta,
        object? contenido = null,
        string? token = null)
    {
        using var solicitud = new HttpRequestMessage(metodo, ruta);
        if (contenido is not null)
        {
            solicitud.Content = JsonContent.Create(contenido, options: OpcionesJson);
        }

        if (!string.IsNullOrWhiteSpace(token))
        {
            solicitud.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        }

        HttpResponseMessage respuesta;
        try
        {
            respuesta = await _clienteHttp.SendAsync(solicitud);
        }
        catch (HttpRequestException ex) when (ex.StatusCode is null)
        {
            throw new InvalidOperationException(
                $"No se pudo conectar con la API en {_clienteHttp.BaseAddress}. Revise que el servidor esté iniciado.",
                ex);
        }

        using (respuesta)
        {
            var json = await respuesta.Content.ReadAsStringAsync();
            if (!respuesta.IsSuccessStatusCode)
            {
                var error = JsonSerializer.Deserialize<ErrorRespuesta>(json, OpcionesJson);
                throw new ExcepcionServicio(
                    (int)respuesta.StatusCode,
                    error?.Mensaje ?? $"La API respondió HTTP {(int)respuesta.StatusCode}.");
            }

            return JsonSerializer.Deserialize<T>(json, OpcionesJson)
                ?? throw new InvalidOperationException("La API devolvió una respuesta vacía.");
        }
    }

    private static string ObtenerUrlBase()
    {
        var configurada = Environment.GetEnvironmentVariable("CONVERSOR_API_URL");
        if (!string.IsNullOrWhiteSpace(configurada))
        {
            return configurada;
        }

#if ANDROID
        return "http://10.0.2.2:19082/";
#else
        return "http://127.0.0.1:19082/";
#endif
    }
}
