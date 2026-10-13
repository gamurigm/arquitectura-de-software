using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using ClienteWebGR09.Models;

namespace ClienteWebGR09.Services;

public sealed class ClienteRest(HttpClient http)
{
    private static readonly JsonSerializerOptions OpcionesJson = new(JsonSerializerDefaults.Web);

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
            respuesta = await http.SendAsync(solicitud);
        }
        catch (HttpRequestException ex) when (ex.StatusCode is null)
        {
            throw new InvalidOperationException(
                $"No se pudo conectar con la API en {http.BaseAddress}. Inicie el servidor.",
                ex);
        }

        using (respuesta)
        {
            var json = await respuesta.Content.ReadAsStringAsync();
            if (!respuesta.IsSuccessStatusCode)
            {
                var error = JsonSerializer.Deserialize<ErrorRespuesta>(json, OpcionesJson);
                throw new ErrorServicioException(
                    (int)respuesta.StatusCode,
                    error?.Mensaje ?? $"La API respondió HTTP {(int)respuesta.StatusCode}.");
            }

            return JsonSerializer.Deserialize<T>(json, OpcionesJson)
                ?? throw new InvalidOperationException("La API devolvió una respuesta vacía.");
        }
    }
}
