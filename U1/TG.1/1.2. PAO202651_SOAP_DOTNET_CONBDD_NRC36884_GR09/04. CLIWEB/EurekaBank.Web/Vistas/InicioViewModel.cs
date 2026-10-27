using ec.edu.monster.Modelos;

namespace ec.edu.monster.Vistas;

public sealed class InicioViewModel
{
    public string? Usuario { get; init; }
    public string Cuenta { get; init; } = "";
    public IReadOnlyList<Cuenta> Cuentas { get; set; } = Array.Empty<Cuenta>();
    public string? Mensaje { get; set; }
    public bool EsError { get; set; }
    public IReadOnlyList<Movimiento> Movimientos { get; set; } = Array.Empty<Movimiento>();
    public decimal Saldo { get; set; }
    public string ClaveOperacion { get; set; } = Guid.NewGuid().ToString();
}
