using System.Security.Cryptography;
using System.Text;

namespace ec.edu.monster.Seguridad;

public static class PasswordHasher
{
    private const int Iteraciones = 600_000;
    private const int BytesSalt = 16;
    private const int BytesHash = 32;
    private const string Prefijo = "pbkdf2-sha256";

    public static string CrearHash(string contrasena)
    {
        var salt = RandomNumberGenerator.GetBytes(BytesSalt);
        var hash = Rfc2898DeriveBytes.Pbkdf2(Encoding.UTF8.GetBytes(contrasena), salt, Iteraciones, HashAlgorithmName.SHA256, BytesHash);
        return $"{Prefijo}${Iteraciones}${Base64Url(salt)}${Base64Url(hash)}";
    }

    public static bool Verificar(string contrasena, string? hashGuardado)
    {
        if (string.IsNullOrWhiteSpace(hashGuardado)) return false;
        var partes = hashGuardado.Split('$');
        if (partes.Length != 4 || partes[0] != Prefijo || !int.TryParse(partes[1], out var iteraciones) || iteraciones != Iteraciones)
            return false;
        try
        {
            var salt = Convert.FromBase64String(partes[2].Replace('-', '+').Replace('_', '/') + new string('=', (4 - partes[2].Length % 4) % 4));
            var esperado = Convert.FromBase64String(partes[3].Replace('-', '+').Replace('_', '/') + new string('=', (4 - partes[3].Length % 4) % 4));
            if (salt.Length != BytesSalt || esperado.Length != BytesHash) return false;
            var calculado = Rfc2898DeriveBytes.Pbkdf2(Encoding.UTF8.GetBytes(contrasena), salt, iteraciones, HashAlgorithmName.SHA256, BytesHash);
            return CryptographicOperations.FixedTimeEquals(calculado, esperado);
        }
        catch (FormatException) { return false; }
    }

    private static string Base64Url(byte[] bytes) => Convert.ToBase64String(bytes).TrimEnd('=').Replace('+', '-').Replace('/', '_');
}
