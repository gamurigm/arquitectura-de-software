using System.Net.Http.Headers;
using System.Net.Http.Json;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorAutenticacionJavaRest : IAdaptadorAutenticacion
{
    private readonly HttpClient _httpClient;

    public TipoBackend Tipo => TipoBackend.JavaRest;

    public AdaptadorAutenticacionJavaRest(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<string> IniciarSesionAsync(string usuario, string contrasenia)
    {
        // En Java REST DTO se llama "contrasenia" (con i)
        var payload = new Dictionary<string, string>
        {
            { "Usuario", usuario },
            { "Contrasena", contrasenia }
        };

        var response = await _httpClient.PostAsJsonAsync($"{ConstantesRed.UrlJavaRest}/seguridad/login", payload);

        if (!response.IsSuccessStatusCode)
        {
            var errorInfo = await response.Content.ReadFromJsonAsync<MensajeErrorDto>();
            throw new Exception($"Error de Login: {errorInfo?.Mensaje ?? "Credenciales incorrectas"}");
        }

        var resultado = await response.Content.ReadFromJsonAsync<LoginResponseDto>();
        return resultado?.Token ?? throw new Exception("No se recibió el token del servidor.");
    }

    public async Task<string> CambiarContraseniaAsync(string contraseniaActual, string contraseniaNueva, string token)
    {
        // Nombres exactos de CambiarContrasenaRequest en JavaREST
        var payload = new Dictionary<string, string>
    {
        { "ContrasenaActual", contraseniaActual },
        { "NuevaContrasena", contraseniaNueva }
    };

        var request = new HttpRequestMessage(HttpMethod.Post, $"{ConstantesRed.UrlJavaRest}/seguridad/cambiar-contrasena");
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        request.Content = JsonContent.Create(payload);

        var response = await _httpClient.SendAsync(request);

        if (!response.IsSuccessStatusCode)
        {
            var errorInfo = await response.Content.ReadFromJsonAsync<MensajeErrorDto>();
            throw new Exception($"Error: {errorInfo?.Mensaje ?? "No se pudo cambiar la contraseña"}");
        }

        var resultado = await response.Content.ReadFromJsonAsync<MensajeErrorDto>();
        return resultado?.Mensaje ?? "Contraseña actualizada";
    }

    // Clases internas para mapear el JSON de respuesta de Java
    private class LoginResponseDto { public string Token { get; set; } }
    private class MensajeErrorDto { public string Mensaje { get; set; } }
}