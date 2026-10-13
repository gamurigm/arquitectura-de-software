using WSConversionUnidades_DotNet_REST_GR09.Excepciones;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;
using WSConversionUnidades_DotNet_REST_GR09.Seguridad;

namespace WSConversionUnidades_DotNet_REST_GR09.Servicios;

public sealed class ServicioAutenticacion(
    IConfiguration configuracion,
    AdministradorTokens tokens)
{
    public TokenRespuesta IniciarSesion(LoginSolicitud solicitud)
    {
        var usuarioValido = configuracion["Autenticacion:Usuario"] ?? "monster";
        var claveValida = configuracion["Autenticacion:Clave"] ?? "monster9";
        var usuario = solicitud.Usuario?.Trim();

        if (!string.Equals(usuario, usuarioValido, StringComparison.Ordinal)
            || !string.Equals(solicitud.Clave, claveValida, StringComparison.Ordinal))
        {
            throw new ErrorApiException(
                StatusCodes.Status401Unauthorized,
                "No autorizado",
                "Credenciales incorrectas. Verifique usuario y clave.");
        }

        var sesion = tokens.Emitir(usuarioValido);
        return new TokenRespuesta(
            true,
            sesion.Valor,
            "Bearer",
            sesion.Usuario,
            Math.Max(0, (long)(sesion.ExpiraEn - DateTimeOffset.UtcNow).TotalSeconds),
            "Autenticación exitosa. Use el token en Authorization: Bearer <token>.");
    }

    public void CerrarSesion(string? token) => tokens.Revocar(token);
}
