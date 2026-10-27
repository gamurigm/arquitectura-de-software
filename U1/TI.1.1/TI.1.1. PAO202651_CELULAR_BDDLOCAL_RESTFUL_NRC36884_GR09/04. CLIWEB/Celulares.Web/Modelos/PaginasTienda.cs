using Celulares.Contratos;

namespace Celulares.Web.Modelos;

public sealed class PaginaTienda
{
    public IReadOnlyList<Celular> Catalogo { get; init; } = [];
    public SolicitudFactura Venta { get; init; } = new();
    public string? Error { get; init; }
}

public sealed record PaginaFactura(Factura Factura, IReadOnlyList<Cuota> Cuotas);
