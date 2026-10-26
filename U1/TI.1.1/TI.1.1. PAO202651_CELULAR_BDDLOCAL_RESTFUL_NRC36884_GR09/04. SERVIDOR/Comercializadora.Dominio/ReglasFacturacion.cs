using Celulares.Contratos;

namespace Comercializadora.Dominio;

public static class ReglasFacturacion
{
    public static decimal TotalEfectivo(decimal precio)
    {
        if (precio <= 0m) throw new ErrorNegocio("El precio debe ser positivo.", 400);
        return decimal.Round(precio * 0.58m, 2, MidpointRounding.AwayFromZero);
    }
}
