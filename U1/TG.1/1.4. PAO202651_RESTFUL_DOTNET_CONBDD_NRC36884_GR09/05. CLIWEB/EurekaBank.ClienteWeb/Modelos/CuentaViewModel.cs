using System.ComponentModel.DataAnnotations;
using EurekaBank.Cliente.Modelos;

namespace EurekaBank.ClienteWeb.Modelos;

public sealed class CuentaViewModel
{
    public string Usuario { get; set; } = string.Empty;

    [StringLength(8)]
    public string Cuenta { get; set; } = string.Empty;

    [StringLength(8)]
    public string CuentaDeposito { get; set; } = string.Empty;

    public decimal Importe { get; set; }

    [StringLength(8)]
    public string CuentaOrigenTransferencia { get; set; } = string.Empty;

    [StringLength(8)]
    public string CuentaDestinoTransferencia { get; set; } = string.Empty;

    public decimal ImporteTransferencia { get; set; }

    public Guid ClaveOperacion { get; set; }

    public string? Mensaje { get; set; }
    public IReadOnlyList<MovimientoDto> Movimientos { get; set; } = [];
}
