using System.ComponentModel.DataAnnotations;

namespace EurekaBank.Api.Modelos;

public sealed class LoginRequest
{
    [Required, StringLength(30, MinimumLength = 1)]
    public string Usuario { get; init; } = string.Empty;

    [Required, StringLength(1024, MinimumLength = 1)]
    public string Contrasena { get; init; } = string.Empty;
}

public sealed class DepositoRequest
{
    [Required, StringLength(8, MinimumLength = 1)]
    public string Cuenta { get; init; } = string.Empty;

    public decimal Importe { get; init; }
}

public sealed record CredencialUsuario(string Hash, string? CodigoCliente);
public sealed record LoginResponse(bool Autenticado, string? Token);
public sealed record CuentaResponse(string Numero, string Moneda, decimal Saldo);
public sealed record DepositoResponse(bool Registrado);
public sealed record TransferenciaRequest(
    string CuentaOrigen,
    string CuentaDestino,
    decimal Importe,
    Guid ClaveOperacion);
public sealed record TransferenciaResponse(bool Registrada);
public sealed record MovimientoResponse(
    int Numero,
    string Cuenta,
    DateTimeOffset Fecha,
    string Tipo,
    string Accion,
    decimal Importe);
