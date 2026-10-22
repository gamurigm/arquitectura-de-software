namespace ec.edu.monster.Modelos;

public sealed class Movimiento
{
    public string Cuenta { get; set; } = "";
    public int Nromov { get; set; }
    public DateTime Fecha { get; set; }
    public string Tipo { get; set; } = "";
    public string Accion { get; set; } = "";
    public decimal Importe { get; set; }
}
