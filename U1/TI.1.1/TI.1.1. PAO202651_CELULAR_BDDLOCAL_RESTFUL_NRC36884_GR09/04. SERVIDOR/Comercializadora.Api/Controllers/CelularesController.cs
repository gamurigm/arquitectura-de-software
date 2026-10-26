using Celulares.Contratos;
using Comercializadora.Api.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace Comercializadora.Api.Controllers;

[ApiController]
[Route("api/celulares")]
public class CelularesController(ServicioCatalogo servicio) : ControllerBase
{
    [HttpGet]
    public Task<IReadOnlyList<Celular>> Listar(CancellationToken ct) => servicio.Listar(ct);

    [HttpGet("{id:int}")]
    public Task<Celular> Obtener(int id, CancellationToken ct) => servicio.Obtener(id, ct);

    [HttpPost]
    public async Task<ActionResult<Celular>> Crear(GuardarCelular solicitud, CancellationToken ct)
    {
        var celular = await servicio.Crear(solicitud, ct);
        return CreatedAtAction(nameof(Obtener), new { id = celular.Id }, celular);
    }

    [HttpPut("{id:int}")]
    public Task<Celular> Actualizar(int id, GuardarCelular solicitud, CancellationToken ct) =>
        servicio.Actualizar(id, solicitud, ct);

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Eliminar(int id, CancellationToken ct)
    {
        await servicio.Eliminar(id, ct);
        return NoContent();
    }
}
