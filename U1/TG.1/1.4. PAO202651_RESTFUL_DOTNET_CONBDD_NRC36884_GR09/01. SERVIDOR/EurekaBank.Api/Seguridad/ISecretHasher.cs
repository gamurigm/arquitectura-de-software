namespace EurekaBank.Api.Seguridad;

public interface ISecretHasher
{
    bool Verify(string encodedHash, string password);
}
