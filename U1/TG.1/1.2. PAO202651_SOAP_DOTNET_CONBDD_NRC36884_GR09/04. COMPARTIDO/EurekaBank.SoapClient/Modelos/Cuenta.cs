namespace ec.edu.monster.Modelos;

public sealed record Cuenta(string Numero, string Moneda, decimal Saldo)
{
    public string Etiqueta => $"{Numero} · {Moneda} {Saldo:N2}";
}
