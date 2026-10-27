using EurekaBank.Api.Modelos;
using EurekaBank.Api.Seguridad;
using EurekaBank.Api.Servicios;
using Xunit;

namespace EurekaBank.Api.Tests;

public sealed class CuentasAsignadasTests
{
    [Fact]
    public async Task RechazaCredencialSinClienteAsociado()
    {
        var repositorio = new RepositorioPrueba(new CredencialUsuario("hash", null));
        var servicio = new EurekaBankService(repositorio, new HasherPrueba(), new SesionesActivas());

        var respuesta = await servicio.IniciarSesionAsync("cliente", "clave", CancellationToken.None);

        Assert.False(respuesta.Autenticado);
        Assert.Null(respuesta.Token);
    }

    [Fact]
    public async Task ListaCuentasPorClienteDerivadoDeSesion()
    {
        var repositorio = new RepositorioPrueba(new CredencialUsuario("hash", "00005"));
        var servicio = new EurekaBankService(repositorio, new HasherPrueba(), new SesionesActivas());
        var login = await servicio.IniciarSesionAsync("cliente", "clave", CancellationToken.None);

        var cuentas = await servicio.ConsultarCuentasAsync(login.Token, CancellationToken.None);

        Assert.Equal("00005", repositorio.CodigoClienteConsultado);
        Assert.Equal("00100001", Assert.Single(cuentas!).Numero);
    }

    private sealed class HasherPrueba : ISecretHasher
    {
        public bool Verify(string hash, string secreto) => hash == "hash" && secreto == "clave";
        public string Hash(string secreto) => throw new NotSupportedException();
    }

    private sealed class RepositorioPrueba(CredencialUsuario? credencial) : IEurekaBankRepository
    {
        public string? CodigoClienteConsultado { get; private set; }
        public Task<CredencialUsuario?> ObtenerCredencialAsync(string usuario, CancellationToken cancellationToken) => Task.FromResult(credencial);
        public Task<IReadOnlyList<CuentaResponse>> ConsultarCuentasAsync(string codigoCliente, CancellationToken cancellationToken)
        {
            CodigoClienteConsultado = codigoCliente;
            IReadOnlyList<CuentaResponse> cuentas = [new CuentaResponse("00100001", "01", 100m)];
            return Task.FromResult(cuentas);
        }
        public Task<IReadOnlyList<MovimientoResponse>> ConsultarMovimientosAsync(string cuenta, string codigoCliente, CancellationToken cancellationToken) => throw new NotSupportedException();
        public Task<bool> RegistrarDepositoAsync(string cuenta, decimal importe, string codigoCliente, CancellationToken cancellationToken) => throw new NotSupportedException();
        public Task<bool> TransferirAsync(string usuario, string codigoCliente, TransferenciaRequest solicitud, CancellationToken cancellationToken) => throw new NotSupportedException();
    }
}
