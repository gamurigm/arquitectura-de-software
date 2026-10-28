using System.Text.RegularExpressions;
using EurekaBank.Api.Modelos;

namespace EurekaBank.Api.Servicios;

public static partial class TransferenciaValidator
{
    private const decimal ImporteMaximo = 9_999_999_999.99m;

    [GeneratedRegex("^[0-9]{8}$", RegexOptions.CultureInvariant)]
    private static partial Regex CuentaRegex();

    public static bool TryValidate(TransferenciaRequest? solicitud, out TransferenciaRequest normalizada)
    {
        normalizada = null!;
        if (solicitud is null || solicitud.ClaveOperacion == Guid.Empty
            || solicitud.Importe <= 0m || solicitud.Importe > ImporteMaximo
            || decimal.Round(solicitud.Importe, 2) != solicitud.Importe)
            return false;

        var origen = solicitud.CuentaOrigen?.Trim();
        var destino = solicitud.CuentaDestino?.Trim();
        if (origen is null || destino is null || !CuentaRegex().IsMatch(origen)
            || !CuentaRegex().IsMatch(destino) || origen == destino)
            return false;

        normalizada = solicitud with { CuentaOrigen = origen, CuentaDestino = destino };
        return true;
    }
}
