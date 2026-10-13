using Microsoft.AspNetCore.Mvc;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;
using WSConversionUnidades_DotNet_REST_GR09.Servicios;

namespace WSConversionUnidades_DotNet_REST_GR09.Controladores;

[ApiController]
[Route("api/conversiones")]
public sealed class ConversionesController(ServicioConversion conversion) : ControllerBase
{
    [HttpPost("{magnitud}")]
    public ActionResult<ConversionRespuesta> Convertir(
        string magnitud,
        [FromBody] ConversionSolicitud solicitud) =>
        Ok(conversion.Convertir(magnitud, solicitud));
}
