using System.Collections.Concurrent;

namespace WSConversionUnidades_DotNet_SOAP_GR09.Seguridad;

public sealed class AdministradorTokens
{
    private static readonly TimeSpan Vigencia = TimeSpan.FromMinutes(15);
    private readonly ConcurrentDictionary<string, DateTimeOffset> _expiraciones = new(StringComparer.Ordinal);

    public string Generar()
    {
        var ahora = DateTimeOffset.UtcNow;
        foreach (var (token, expiracion) in _expiraciones)
        {
            if (expiracion <= ahora)
            {
                _expiraciones.TryRemove(token, out _);
            }
        }

        var nuevoToken = Guid.NewGuid().ToString();
        _expiraciones[nuevoToken] = ahora.Add(Vigencia);
        return nuevoToken;
    }

    public bool Validar(string? token)
    {
        if (string.IsNullOrWhiteSpace(token) || !_expiraciones.TryGetValue(token, out var expiracion))
        {
            return false;
        }

        if (expiracion > DateTimeOffset.UtcNow)
        {
            return true;
        }

        _expiraciones.TryRemove(token, out _);
        return false;
    }
}
