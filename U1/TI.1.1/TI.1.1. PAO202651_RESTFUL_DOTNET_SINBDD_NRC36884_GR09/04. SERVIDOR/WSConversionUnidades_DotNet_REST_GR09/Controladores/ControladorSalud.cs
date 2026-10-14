using Microsoft.AspNetCore.Mvc;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;

namespace WSConversionUnidades_DotNet_REST_GR09.Controladores;

[ApiController]
[Route("api/salud")]
public sealed class ControladorSalud : ControllerBase
{
    [HttpGet]
    public ActionResult<EstadoServidorRespuesta> ObtenerEstado() =>
        Ok(new EstadoServidorRespuesta(
            true,
            "WSConversionUnidades_DotNet_REST_GR09",
            "EN LINEA",
            DateTimeOffset.UtcNow));
}
