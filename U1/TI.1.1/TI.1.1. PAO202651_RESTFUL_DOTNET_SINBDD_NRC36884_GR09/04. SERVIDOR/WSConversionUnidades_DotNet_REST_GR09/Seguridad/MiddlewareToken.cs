using WSConversionUnidades_DotNet_REST_GR09.Modelos;

namespace WSConversionUnidades_DotNet_REST_GR09.Seguridad;

public sealed class MiddlewareToken(RequestDelegate siguiente)
{
    public async Task InvokeAsync(HttpContext contexto, AdministradorTokens tokens)
    {
        if (!RequiereToken(contexto.Request))
        {
            await siguiente(contexto);
            return;
        }

        var token = ExtraerToken(contexto.Request);
        if (tokens.Validar(token) is null)
        {
            contexto.Response.StatusCode = StatusCodes.Status401Unauthorized;
            contexto.Response.ContentType = "application/json; charset=utf-8";
            var error = new ErrorRespuesta(
                false,
                StatusCodes.Status401Unauthorized,
                "No autorizado",
                "Token ausente, inválido o caducado. Inicie sesión nuevamente.",
                contexto.Request.Path,
                DateTimeOffset.UtcNow);
            await contexto.Response.WriteAsJsonAsync(error);
            return;
        }

        await siguiente(contexto);
    }

    private static bool RequiereToken(HttpRequest solicitud) =>
        solicitud.Path.StartsWithSegments("/api/conversiones")
        || (HttpMethods.IsPost(solicitud.Method)
            && solicitud.Path.Equals("/api/auth/logout", StringComparison.OrdinalIgnoreCase));

    private static string? ExtraerToken(HttpRequest solicitud)
    {
        var autorizacion = solicitud.Headers.Authorization.ToString();
        if (autorizacion.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
        {
            return autorizacion[7..].Trim();
        }

        return solicitud.Headers["X-Auth-Token"].FirstOrDefault()?.Trim();
    }
}
