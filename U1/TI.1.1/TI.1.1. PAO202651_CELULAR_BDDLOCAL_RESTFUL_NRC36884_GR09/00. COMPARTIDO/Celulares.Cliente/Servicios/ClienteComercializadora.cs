using System.Net.Http.Json;
using System.Text.Json;
using Celulares.Contratos;

namespace Celulares.Cliente.Servicios;

public sealed class ClienteComercializadora(HttpClient http)
{
    public static Uri Direccion(string? valor, string predeterminada = "http://127.0.0.1:19102/")
    {
        var texto = string.IsNullOrWhiteSpace(valor) ? predeterminada : valor.Trim();
        if (!Uri.TryCreate(texto.EndsWith('/') ? texto : texto + "/", UriKind.Absolute, out var uri) ||
            (uri.Scheme != Uri.UriSchemeHttp && uri.Scheme != Uri.UriSchemeHttps))
            throw new ArgumentException("La dirección del servidor debe ser una URL HTTP o HTTPS válida.");
        return uri;
    }

    public Task<Celular[]> Listar(CancellationToken ct = default) =>
        Leer<Celular[]>(http.GetAsync("api/celulares", ct), ct);

    public Task<Celular> Guardar(int? id, GuardarCelular celular, CancellationToken ct = default) =>
        Leer<Celular>(id is null ? http.PostAsJsonAsync("api/celulares", celular, ct)
            : http.PutAsJsonAsync($"api/celulares/{id}", celular, ct), ct);

    public async Task Eliminar(int id, CancellationToken ct = default)
    {
        using var respuesta = await http.DeleteAsync($"api/celulares/{id}", ct);
        await Comprobar(respuesta, ct);
    }

    public Task<Factura> Facturar(SolicitudFactura factura, CancellationToken ct = default) =>
        Leer<Factura>(http.PostAsJsonAsync("api/facturas", factura, ct), ct);

    public Task<Factura> ObtenerFactura(int id, CancellationToken ct = default) =>
        Leer<Factura>(http.GetAsync($"api/facturas/{id}", ct), ct);

    public Task<Cuota[]> Amortizacion(int id, CancellationToken ct = default) =>
        Leer<Cuota[]>(http.GetAsync($"api/facturas/{id}/amortizacion", ct), ct);

    private static async Task<T> Leer<T>(Task<HttpResponseMessage> enviar, CancellationToken ct)
    {
        using var respuesta = await enviar;
        await Comprobar(respuesta, ct);
        return await respuesta.Content.ReadFromJsonAsync<T>(cancellationToken: ct)
            ?? throw new InvalidOperationException("El servidor devolvió una respuesta vacía.");
    }

    private static async Task Comprobar(HttpResponseMessage respuesta, CancellationToken ct)
    {
        if (respuesta.IsSuccessStatusCode) return;
        string? mensaje = null;
        try
        {
            using var json = await JsonDocument.ParseAsync(await respuesta.Content.ReadAsStreamAsync(ct), cancellationToken: ct);
            if (json.RootElement.TryGetProperty("title", out var titulo)) mensaje = titulo.GetString();
        }
        catch (JsonException) { }
        throw new InvalidOperationException(mensaje ?? $"El servidor rechazó la operación (HTTP {(int)respuesta.StatusCode}).");
    }
}
