using Celulares.Cliente.Servicios;
using Celulares.Contratos;
using System.Globalization;

namespace Celulares.Cliente.Controladores;

public sealed class ControladorTienda(ClienteComercializadora api)
{
    public static decimal InterpretarPrecio(string? texto)
    {
        var valor = texto?.Trim() ?? "";
        if (valor.Contains('.') && valor.Contains(','))
            throw new ArgumentException("Ingrese el precio sin separadores de miles.");
        valor = valor.Replace(',', '.');
        if (!decimal.TryParse(valor, NumberStyles.AllowDecimalPoint, CultureInfo.InvariantCulture, out var precio)
            || precio <= 0 || precio > 99999999.99m || decimal.Round(precio, 2) != precio)
            throw new ArgumentException("Ingrese un precio positivo con máximo dos decimales.");
        return precio;
    }

    public Task<Celular[]> Catalogo() => api.Listar();
    public Task<Celular> Guardar(int? id, string nombre, decimal precio) =>
        api.Guardar(id, new GuardarCelular { Nombre = nombre.Trim(), Precio = precio, Activo = true });
    public Task Eliminar(int id) => api.Eliminar(id);
    public Task<Factura> Facturar(SolicitudFactura solicitud) => api.Facturar(solicitud);
    public Task<Factura> Factura(int id) => api.ObtenerFactura(id);
    public Task<Cuota[]> Amortizacion(int facturaId) => api.Amortizacion(facturaId);
}
