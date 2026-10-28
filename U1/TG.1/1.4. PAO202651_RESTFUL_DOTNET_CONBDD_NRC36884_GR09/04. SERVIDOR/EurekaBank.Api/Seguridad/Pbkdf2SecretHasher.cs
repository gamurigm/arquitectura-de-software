using System.Globalization;
using System.Security.Cryptography;
using System.Text;

namespace EurekaBank.Api.Seguridad;

public sealed class Pbkdf2SecretHasher : ISecretHasher
{
    private const int Iterations = 600_000;
    private const int SaltLength = 16;
    private const int HashLength = 32;

    public bool Verify(string encodedHash, string password)
    {
        var parts = encodedHash.Split('$');
        if (parts.Length != 4
            || parts[0] != "pbkdf2-sha256"
            || !int.TryParse(parts[1], NumberStyles.None, CultureInfo.InvariantCulture, out var iterations)
            || iterations != Iterations
            || parts[2].Length != 22
            || parts[3].Length != 43)
        {
            return false;
        }

        try
        {
            var salt = DecodeBase64Url(parts[2]);
            var expectedHash = DecodeBase64Url(parts[3]);
            if (salt.Length != SaltLength || expectedHash.Length != HashLength)
            {
                return false;
            }

            var passwordBytes = Encoding.UTF8.GetBytes(password);
            try
            {
                var actualHash = Rfc2898DeriveBytes.Pbkdf2(
                    passwordBytes, salt, Iterations, HashAlgorithmName.SHA256, HashLength);
                try
                {
                    return CryptographicOperations.FixedTimeEquals(expectedHash, actualHash);
                }
                finally
                {
                    CryptographicOperations.ZeroMemory(actualHash);
                }
            }
            finally
            {
                CryptographicOperations.ZeroMemory(passwordBytes);
            }
        }
        catch (FormatException)
        {
            return false;
        }
    }

    private static byte[] DecodeBase64Url(string value)
    {
        var base64 = value.Replace('-', '+').Replace('_', '/');
        return Convert.FromBase64String(base64.PadRight(base64.Length + (4 - base64.Length % 4) % 4, '='));
    }
}
