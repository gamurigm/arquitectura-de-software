using Comercializadora.Dominio;
using Xunit;

namespace Comercializadora.Pruebas;

public class ReglasFacturacionTests
{
    [Fact]
    public void EfectivoDescuenta42PorCiento() =>
        Assert.Equal(58m, ReglasFacturacion.TotalEfectivo(100m));
}
