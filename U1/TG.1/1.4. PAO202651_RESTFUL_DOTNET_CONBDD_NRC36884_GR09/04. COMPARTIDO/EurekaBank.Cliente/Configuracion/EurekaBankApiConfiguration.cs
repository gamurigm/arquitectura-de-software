namespace EurekaBank.Cliente.Configuracion;

public static class EurekaBankApiConfiguration
{
    public static Uri GetBaseAddress(string defaultAddress)
    {
        var configuredAddress = Environment.GetEnvironmentVariable("EUREKABANK_REST_DOTNET_URL");
        var address = string.IsNullOrWhiteSpace(configuredAddress) ? defaultAddress : configuredAddress.Trim();
        if (!Uri.TryCreate(address, UriKind.Absolute, out var baseAddress)
            || (baseAddress.Scheme != Uri.UriSchemeHttp && baseAddress.Scheme != Uri.UriSchemeHttps))
        {
            throw new InvalidOperationException("EUREKABANK_REST_DOTNET_URL debe ser una URL HTTP o HTTPS absoluta.");
        }

        return new Uri(baseAddress.AbsoluteUri.EndsWith('/') ? baseAddress.AbsoluteUri : baseAddress.AbsoluteUri + "/");
    }
}
