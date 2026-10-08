using System.Net.Http.Headers;
using System.Net.Http.Json;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorConversionDotNetRest : IAdaptadorConversion
{
    private readonly HttpClient _httpClient;

    public AdaptadorConversionDotNetRest(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public TipoBackend Tipo => TipoBackend.DotNetRest;

    public async Task<double> ConvertirAsync(PeticionConversion peticion, string token)
    {
        // 1. Determinar el endpoint basado en el tipo de magnitud
        string endpoint = peticion.TipoConversion.ToLower() switch
        {
            "longitud" => "longitud",
            "masa" => "masa",
            "temperatura" => "temperatura",
            _ => throw new Exception("Magnitud no soportada")
        };

        // 2. Preparar el JSON que tu backend espera
        var payload = new
        {
            valor = peticion.Valor,
            unidadOrigen = peticion.UnidadOrigen,
            unidadDestino = peticion.UnidadDestino
        };

        // 3. Crear la petición HTTP con el Bearer Token
        var request = new HttpRequestMessage(HttpMethod.Post, $"{ConstantesRed.UrlDotNetRest}/conversiones/{endpoint}");
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        request.Content = JsonContent.Create(payload);

        // 4. Enviar y procesar
        var response = await _httpClient.SendAsync(request);

        if (!response.IsSuccessStatusCode)
        {
            var error = await response.Content.ReadAsStringAsync();
            throw new Exception($"Error de servidor: {error}");
        }

        var resultado = await response.Content.ReadFromJsonAsync<ConversionResponseDto>();
        return resultado?.ValorConvertido ?? 0;
    }

    // Clase interna para mapear el JSON de respuesta
    private class ConversionResponseDto { public double ValorConvertido { get; set; } }
}