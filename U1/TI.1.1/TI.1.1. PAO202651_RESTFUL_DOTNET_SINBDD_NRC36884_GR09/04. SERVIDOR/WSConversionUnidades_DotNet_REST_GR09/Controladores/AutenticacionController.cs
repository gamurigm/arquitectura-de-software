using Microsoft.AspNetCore.Mvc;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;
using WSConversionUnidades_DotNet_REST_GR09.Servicios;

namespace WSConversionUnidades_DotNet_REST_GR09.Controladores;

[ApiController]
[Route("api/auth")]
public sealed class AutenticacionController(ServicioAutenticacion autenticacion) : ControllerBase
{
    [HttpPost("login")]
    public ActionResult<TokenRespuesta> IniciarSesion([FromBody] LoginSolicitud solicitud) =>
        Ok(autenticacion.IniciarSesion(solicitud));

    [HttpPost("logout")]
    public ActionResult<CierreSesionRespuesta> CerrarSesion()
    {
        var encabezado = Request.Headers.Authorization.ToString();
        var token = encabezado.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase)
            ? encabezado[7..].Trim()
            : Request.Headers["X-Auth-Token"].FirstOrDefault();
        autenticacion.CerrarSesion(token);
        return Ok(new CierreSesionRespuesta(true, "Sesión cerrada correctamente."));
    }
}
