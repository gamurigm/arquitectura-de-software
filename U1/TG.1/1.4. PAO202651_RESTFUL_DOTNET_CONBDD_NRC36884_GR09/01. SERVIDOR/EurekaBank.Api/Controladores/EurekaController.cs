using EurekaBank.Api.Modelos;
using EurekaBank.Api.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace EurekaBank.Api.Controladores;

[ApiController]
[Route("api/eureka")]
public sealed class EurekaController(IEurekaBankService servicio) : ControllerBase
{
    [HttpPost("login")]
    public async Task<ActionResult<LoginResponse>> IniciarSesion(
        [FromBody] LoginRequest request,
        CancellationToken cancellationToken)
    {
        return Ok(await servicio.IniciarSesionAsync(request.Usuario, request.Contrasena, cancellationToken));
    }

    [HttpGet("movimientos")]
    public async Task<ActionResult<IReadOnlyList<MovimientoResponse>>> ConsultarMovimientos(
        [FromQuery] string? cuenta,
        CancellationToken cancellationToken)
    {
        var movimientos = await servicio.ConsultarMovimientosAsync(cuenta ?? string.Empty, cancellationToken);
        return Ok(movimientos);
    }

    [HttpPost("depositos")]
    public async Task<ActionResult<DepositoResponse>> RegistrarDeposito(
        [FromBody] DepositoRequest request,
        CancellationToken cancellationToken)
    {
        var registrado = await servicio.RegistrarDepositoAsync(request.Cuenta, request.Importe, cancellationToken);
        return Ok(new DepositoResponse(registrado));
    }

    [HttpPost("transferencias")]
    public async Task<ActionResult<TransferenciaResponse>> Transferir(
        [FromHeader(Name = "Authorization")] string? authorization,
        [FromBody] TransferenciaRequest request,
        CancellationToken cancellationToken)
    {
        var respuesta = await servicio.TransferirAsync(ExtraerToken(authorization), request, cancellationToken);
        return respuesta is null ? Unauthorized() : Ok(respuesta);
    }

    [HttpPost("logout")]
    public IActionResult CerrarSesion([FromHeader(Name = "Authorization")] string? authorization)
    {
        servicio.CerrarSesion(ExtraerToken(authorization));
        return NoContent();
    }

    private static string? ExtraerToken(string? authorization) =>
        authorization?.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase) == true
            ? authorization[7..].Trim()
            : null;
}
