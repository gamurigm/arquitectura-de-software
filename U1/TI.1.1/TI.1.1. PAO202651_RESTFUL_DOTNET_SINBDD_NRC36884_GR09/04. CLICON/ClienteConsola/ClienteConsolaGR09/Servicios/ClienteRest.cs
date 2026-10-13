using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using ClienteConsolaGR09.Modelos;

namespace ClienteConsolaGR09.Servicios;

public sealed class ClienteRest
{
    private static readonly JsonSerializerOptions OpcionesJson = new(JsonSerializerDefaults.Web);
    private readonly HttpClient _http;

    public ClienteRest(HttpClient? http = null)
    {
        _http = http ?? new HttpClient();
        _http.BaseAddress ??= new Uri(
            Environment.GetEnvironmentVariable("CONVERSOR_API_URL") ?? "http://127.0.0.1:19082/");
        _http.Timeout = TimeSpan.FromSeconds(15);
    }

    public Task<TokenRespuesta> IniciarSesionAsync(LoginSolicitud solicitud) =>
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
            respuesta = await _http.SendAsync(solicitud);
        }
        catch (HttpRequestException ex) when (ex.StatusCode is null)
        {
            throw new InvalidOperationException(
                $"No se pudo conectar con la API en {_http.BaseAddress}. Inicie el servidor.",
                ex);
        }

        using (respuesta)
        {
            var json = await respuesta.Content.ReadAsStringAsync();
            if (!respuesta.IsSuccessStatusCode)
            {
                var error = JsonSerializer.Deserialize<ErrorRespuesta>(json, OpcionesJson);
                throw new InvalidOperationException(error?.Mensaje ?? $"La API respondió HTTP {(int)respuesta.StatusCode}.");
            }

            return JsonSerializer.Deserialize<T>(json, OpcionesJson)
                ?? throw new InvalidOperationException("La API devolvió una respuesta vacía.");
        }
    }
}
