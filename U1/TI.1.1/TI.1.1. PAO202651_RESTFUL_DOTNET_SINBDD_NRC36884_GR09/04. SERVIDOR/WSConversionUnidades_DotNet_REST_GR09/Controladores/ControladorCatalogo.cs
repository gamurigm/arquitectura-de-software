using Microsoft.AspNetCore.Mvc;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;
using WSConversionUnidades_DotNet_REST_GR09.Servicios;

namespace WSConversionUnidades_DotNet_REST_GR09.Controladores;

[ApiController]
[Route("api/catalogo")]
public sealed class ControladorCatalogo(ServicioCatalogo catalogo) : ControllerBase
{
    [HttpGet]
    public ActionResult<CatalogoRespuesta> Obtener() => Ok(catalogo.ObtenerCatalogo());
}
