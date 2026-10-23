using Celulares.Contratos;
namespace BanQuito.Dominio;
public static class ReglasFacturacion
{
    public static decimal TotalEfectivo(decimal precio)
    {
        if (precio <= 0m) throw new ErrorNegocio("El precio debe ser positivo.", 400);
        return ReglasCredito.Dinero(precio * 0.58m);
    }
}
