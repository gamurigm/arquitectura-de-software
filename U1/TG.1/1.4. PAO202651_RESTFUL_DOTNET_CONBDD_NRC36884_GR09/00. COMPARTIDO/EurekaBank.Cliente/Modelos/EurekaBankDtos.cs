namespace EurekaBank.Cliente.Modelos;

public sealed record LoginRequest(string Usuario, string Contrasena);
public sealed record LoginResponse(bool Autenticado, string? Token);
public sealed record DepositoRequest(string Cuenta, decimal Importe);
public sealed record DepositoResponse(bool Registrado);
public sealed record TransferenciaRequest(string CuentaOrigen, string CuentaDestino, decimal Importe, Guid ClaveOperacion);
public sealed record TransferenciaResponse(bool Registrada);
public sealed record MovimientoDto(
    int Numero,
    string Cuenta,
    DateTimeOffset Fecha,
    string Tipo,
    string Accion,
    decimal Importe);
