namespace ec.edu.monster.Modelos;

public sealed record CredencialUsuario(string Hash, string? CodigoCliente);
public sealed record TransferenciaRequest(string CuentaOrigen, string CuentaDestino, decimal Importe, Guid ClaveOperacion);
