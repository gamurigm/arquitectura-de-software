using EurekaBank.Api.Controladores;
using EurekaBank.Api.Modelos;
using EurekaBank.Api.Servicios;
using Microsoft.AspNetCore.Mvc;
using Xunit;

namespace EurekaBank.Api.Tests;

public sealed class DepositoSinSesionTests
{
    [Fact]
    public async Task Devuelve401CuandoElTokenNoEsValido()
    {
        var controller = new EurekaController(new ServicioSinSesion());

        var respuesta = await controller.RegistrarDeposito("Bearer token-invalido",
            new DepositoRequest { Cuenta = "00100001", Importe = 1m }, CancellationToken.None);

        Assert.IsType<UnauthorizedResult>(respuesta.Result);
    }

    private sealed class ServicioSinSesion : IEurekaBankService
    {
        public Task<LoginResponse> IniciarSesionAsync(string usuario, string contrasena, CancellationToken cancellationToken) => throw new NotSupportedException();
        public Task<IReadOnlyList<CuentaResponse>?> ConsultarCuentasAsync(string? token, CancellationToken cancellationToken) => throw new NotSupportedException();
        public Task<IReadOnlyList<MovimientoResponse>?> ConsultarMovimientosAsync(string? token, string cuenta, CancellationToken cancellationToken) => throw new NotSupportedException();
        public Task<bool?> RegistrarDepositoAsync(string? token, string cuenta, decimal importe, CancellationToken cancellationToken) => Task.FromResult<bool?>(null);
        public Task<TransferenciaResponse?> TransferirAsync(string? token, TransferenciaRequest solicitud, CancellationToken cancellationToken) => throw new NotSupportedException();
        public void CerrarSesion(string? token) => throw new NotSupportedException();
    }
}
