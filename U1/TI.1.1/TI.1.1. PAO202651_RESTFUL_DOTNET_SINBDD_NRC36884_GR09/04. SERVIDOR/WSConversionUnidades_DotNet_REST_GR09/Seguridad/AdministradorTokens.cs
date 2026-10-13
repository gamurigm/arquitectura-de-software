using System.Collections.Concurrent;
using System.Security.Cryptography;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;

namespace WSConversionUnidades_DotNet_REST_GR09.Seguridad;

public sealed class AdministradorTokens(IConfiguration configuracion)
{
    private readonly ConcurrentDictionary<string, TokenSesion> _tokens = new(StringComparer.Ordinal);
    private readonly TimeSpan _vigencia = TimeSpan.FromMinutes(
        Math.Clamp(configuracion.GetValue("Autenticacion:VigenciaMinutos", 10), 1, 1440));

    public TokenSesion Emitir(string usuario)
    {
        var valor = Convert.ToHexString(RandomNumberGenerator.GetBytes(32)).ToLowerInvariant();
        var sesion = new TokenSesion(valor, usuario, DateTimeOffset.UtcNow.Add(_vigencia));
        _tokens[valor] = sesion;
        return sesion;
    }

    public TokenSesion? Validar(string? valor)
    {
        if (string.IsNullOrWhiteSpace(valor) || !_tokens.TryGetValue(valor, out var sesion))
        {
            return null;
        }

        if (sesion.ExpiraEn > DateTimeOffset.UtcNow)
        {
            return sesion;
        }

        _tokens.TryRemove(valor, out _);
        return null;
    }

    public void Revocar(string? valor)
    {
        if (!string.IsNullOrWhiteSpace(valor))
        {
            _tokens.TryRemove(valor, out _);
        }
    }
}
