using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;
using System.Net.Http.Headers;
using System.Net.Http.Json;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorAutenticacionDotNetRest : IAdaptadorAutenticacion
{
    private readonly HttpClient _httpClient;

    public TipoBackend Tipo => TipoBackend.DotNetRest;

    public AdaptadorAutenticacionDotNetRest(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<string> IniciarSesionAsync(string usuario, string contrasenia)
    {
        // Nota: Tu API .NET espera "contrasena" sin 'i'
        var payload = new { usuario = usuario, contrasena = contrasenia };

        var response = await _httpClient.PostAsJsonAsync($"{ConstantesRed.UrlDotNetRest}/seguridad/login", payload);

        if (!response.IsSuccessStatusCode)
        {
            var error = await response.Content.ReadAsStringAsync();
            throw new Exception($"Error REST: {error}");
        }

        var resultado = await response.Content.ReadFromJsonAsync<LoginResponseDto>();
        return resultado?.Token ?? throw new Exception("Token no recibido");
    }

    public async Task<string> CambiarContraseniaAsync(string contraseniaActual, string contraseniaNueva, string token)
    {
        var payload = new { contrasenaActual = contraseniaActual, nuevaContrasena = contraseniaNueva };

        // Configuramos el token Bearer en la cabecera
        var request = new HttpRequestMessage(HttpMethod.Post, $"{ConstantesRed.UrlDotNetRest}/seguridad/cambiar-contrasena");
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        request.Content = JsonContent.Create(payload);

        var response = await _httpClient.SendAsync(request);

        if (!response.IsSuccessStatusCode)
        {
            var error = await response.Content.ReadAsStringAsync();
            throw new Exception($"Error al cambiar contraseña: {error}");
        }

        return await response.Content.ReadAsStringAsync();
    }

    // Clase interna temporal para mapear el JSON de respuesta
    private class LoginResponseDto { public string Token { get; set; } }
}