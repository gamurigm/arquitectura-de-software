using System.Net.Http.Json;
using System.Net.Http.Headers;
using EurekaBank.Cliente.Modelos;

namespace EurekaBank.Cliente.Servicios;

public sealed class EurekaBankApiClient(HttpClient httpClient) : IEurekaBankApiClient
{
    public async Task<LoginResponse> LoginAsync(LoginRequest request, CancellationToken cancellationToken = default)
    {
        using var response = await httpClient.PostAsJsonAsync("api/eureka/login", request, cancellationToken);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<LoginResponse>(cancellationToken: cancellationToken)
            ?? throw new InvalidDataException("El servidor devolvió una respuesta de inicio de sesión vacía.");
    }

    public async Task<IReadOnlyList<CuentaDto>> ConsultarCuentasAsync(string token, CancellationToken cancellationToken = default)
    {
        using var request = CrearSolicitud(HttpMethod.Get, "api/eureka/cuentas", token);
        using var response = await httpClient.SendAsync(request, cancellationToken);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<CuentaDto>>(cancellationToken: cancellationToken)
            ?? throw new InvalidDataException("El servidor devolvió una lista de cuentas vacía.");
    }

    public async Task<IReadOnlyList<MovimientoDto>> ConsultarMovimientosAsync(
        string cuenta,
        string? token = null,
        CancellationToken cancellationToken = default)
    {
        var ruta = $"api/eureka/movimientos?cuenta={Uri.EscapeDataString(cuenta)}";
        using var request = CrearSolicitud(HttpMethod.Get, ruta, token);
        using var response = await httpClient.SendAsync(request, cancellationToken);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<MovimientoDto>>(cancellationToken: cancellationToken)
            ?? throw new InvalidDataException("El servidor devolvió una lista de movimientos vacía.");
    }

    public async Task<DepositoResponse> DepositarAsync(
        DepositoRequest request,
        string? token = null,
        CancellationToken cancellationToken = default)
    {
        using var message = CrearSolicitud(HttpMethod.Post, "api/eureka/depositos", token);
        message.Content = JsonContent.Create(request);
        using var response = await httpClient.SendAsync(message, cancellationToken);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<DepositoResponse>(cancellationToken: cancellationToken)
            ?? throw new InvalidDataException("El servidor devolvió una respuesta de depósito vacía.");
    }

    public async Task<TransferenciaResponse> TransferirAsync(
        TransferenciaRequest request,
        string token,
        CancellationToken cancellationToken = default)
    {
        using var message = CrearSolicitud(HttpMethod.Post, "api/eureka/transferencias", token);
        message.Content = JsonContent.Create(request);
        using var response = await httpClient.SendAsync(message, cancellationToken);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<TransferenciaResponse>(cancellationToken: cancellationToken)
            ?? throw new InvalidDataException("El servidor devolvió una respuesta de transferencia vacía.");
    }

    public async Task CerrarSesionAsync(string? token, CancellationToken cancellationToken = default)
    {
        using var request = CrearSolicitud(HttpMethod.Post, "api/eureka/logout", token);
        using var response = await httpClient.SendAsync(request, cancellationToken);
        response.EnsureSuccessStatusCode();
    }

    private static HttpRequestMessage CrearSolicitud(HttpMethod metodo, string ruta, string? token)
    {
        var request = new HttpRequestMessage(metodo, ruta);
        if (!string.IsNullOrWhiteSpace(token))
            request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        return request;
    }
}
