using System.Text.Json;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;

namespace WSConversionUnidades_DotNet_REST_GR09.Excepciones;

public sealed class MiddlewareErrores(RequestDelegate siguiente, ILogger<MiddlewareErrores> logger)
{
    private static readonly JsonSerializerOptions OpcionesJson = new(JsonSerializerDefaults.Web);

    public async Task InvokeAsync(HttpContext contexto)
    {
        try
        {
            await siguiente(contexto);
        }
        catch (ErrorApiException ex)
        {
            await EscribirError(contexto, ex.Codigo, ex.Error, ex.Message);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error no controlado al procesar {Ruta}", contexto.Request.Path);
            await EscribirError(
                contexto,
                StatusCodes.Status500InternalServerError,
                "Error interno",
                "Ocurrió un error inesperado en el servidor.");
        }
    }

    private static async Task EscribirError(HttpContext contexto, int codigo, string error, string mensaje)
    {
        if (contexto.Response.HasStarted)
        {
            return;
        }

        contexto.Response.Clear();
        contexto.Response.StatusCode = codigo;
        contexto.Response.ContentType = "application/json; charset=utf-8";
        var respuesta = new ErrorRespuesta(
            false,
            codigo,
            error,
            mensaje,
            contexto.Request.Path,
            DateTimeOffset.UtcNow);
        await contexto.Response.WriteAsJsonAsync(respuesta, OpcionesJson);
    }
}
