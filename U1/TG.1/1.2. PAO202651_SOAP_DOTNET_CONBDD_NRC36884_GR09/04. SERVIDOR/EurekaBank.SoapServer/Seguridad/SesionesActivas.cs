using System.Collections.Concurrent;
using System.Security.Cryptography;

namespace ec.edu.monster.Seguridad;

public sealed class SesionesActivas
{
    private static readonly TimeSpan Duracion = TimeSpan.FromMinutes(20);
    private readonly ConcurrentDictionary<string, SesionUsuario> _sesiones = new(StringComparer.Ordinal);

    public string Crear(string usuario, string? codigoCliente)
    {
        var token = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32))
            .TrimEnd('=').Replace('+', '-').Replace('/', '_');
        _sesiones[token] = new SesionUsuario(usuario, codigoCliente, DateTimeOffset.UtcNow.Add(Duracion));
        return token;
    }

    public bool TryObtener(string? token, out SesionUsuario sesion)
    {
        sesion = null!;
        if (string.IsNullOrWhiteSpace(token) || !_sesiones.TryGetValue(token, out var encontrada)) return false;
        if (encontrada.Expira <= DateTimeOffset.UtcNow) { _sesiones.TryRemove(token, out _); return false; }
        sesion = encontrada;
        return true;
    }

    public void Revocar(string? token)
    {
        if (!string.IsNullOrWhiteSpace(token)) _sesiones.TryRemove(token, out _);
    }
}

public sealed record SesionUsuario(string Usuario, string? CodigoCliente, DateTimeOffset Expira);
