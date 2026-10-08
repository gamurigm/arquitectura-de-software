using System.Net.Http.Headers;
using System.Net.Http.Json;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorConversionJavaRest : IAdaptadorConversion
{
    private readonly HttpClient _httpClient;

    public AdaptadorConversionJavaRest(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public TipoBackend Tipo => TipoBackend.JavaRest;

    public async Task<double> ConvertirAsync(PeticionConversion peticion, string token)
    {
        // La ruta en Java coincide: /longitud, /masa, /temperatura
        string endpoint = peticion.TipoConversion.ToLower();

        // El DTO de Java espera estas propiedades
        var payload = new Dictionary<string, object>
    {
        { "Valor", peticion.Valor },
        { "UnidadOrigen", peticion.UnidadOrigen },
        { "UnidadDestino", peticion.UnidadDestino }
    };

        var request = new HttpRequestMessage(HttpMethod.Post, $"{ConstantesRed.UrlJavaRest}/conversiones/{endpoint}");

        // Inyectamos el Token para que pase por tu ManejadorAutenticacion.java
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        request.Content = JsonContent.Create(payload);

        var response = await _httpClient.SendAsync(request);

        if (!response.IsSuccessStatusCode)
        {
            var errorInfo = await response.Content.ReadFromJsonAsync<MensajeErrorDto>();
            throw new Exception($"Error: {errorInfo?.Mensaje ?? response.StatusCode.ToString()}");
        }

        var resultado = await response.Content.ReadFromJsonAsync<ConversionResponseDto>();
        return resultado?.ValorConvertido ?? 0;
    }

    private class ConversionResponseDto { public double ValorConvertido { get; set; } }
    private class MensajeErrorDto { public string Mensaje { get; set; } }
}