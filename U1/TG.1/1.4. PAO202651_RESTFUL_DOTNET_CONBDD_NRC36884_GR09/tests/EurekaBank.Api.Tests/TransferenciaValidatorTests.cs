using EurekaBank.Api.Modelos;
using EurekaBank.Api.Servicios;
using Xunit;

namespace EurekaBank.Api.Tests;

public sealed class TransferenciaValidatorTests
{
    [Fact]
    public void AceptaSolicitudValida()
    {
        var solicitud = new TransferenciaRequest("00200001", "00200002", 12.50m, Guid.NewGuid());

        Assert.True(TransferenciaValidator.TryValidate(solicitud, out var normalizada));
        Assert.Equal("00200001", normalizada.CuentaOrigen);
        Assert.Equal("00200002", normalizada.CuentaDestino);
    }

    [Theory]
    [InlineData("00200001", "00200001", 1.00)]
    [InlineData("200001", "00200002", 1.00)]
    [InlineData("00200001", "00200002", 0.00)]
    [InlineData("00200001", "00200002", -1.00)]
    [InlineData("00200001", "00200002", 1.001)]
    public void RechazaCuentasRepetidasOCamposInvalidos(string origen, string destino, decimal importe)
    {
        var solicitud = new TransferenciaRequest(origen, destino, importe, Guid.NewGuid());

        Assert.False(TransferenciaValidator.TryValidate(solicitud, out _));
    }

    [Fact]
    public void RechazaClaveDeIdempotenciaVacia()
    {
        var solicitud = new TransferenciaRequest("00200001", "00200002", 1.00m, Guid.Empty);

        Assert.False(TransferenciaValidator.TryValidate(solicitud, out _));
    }
}
