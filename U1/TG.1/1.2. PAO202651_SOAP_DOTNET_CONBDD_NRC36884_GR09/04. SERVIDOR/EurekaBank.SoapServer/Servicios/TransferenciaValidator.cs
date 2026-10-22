using System.Text.RegularExpressions;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Servicios;

public static partial class TransferenciaValidator
{
    [GeneratedRegex("^[0-9]{8}$", RegexOptions.CultureInvariant)]
    private static partial Regex CuentaRegex();

    public static bool TryValidate(string? origen, string? destino, decimal importe, string? clave, out TransferenciaRequest solicitud)
    {
        solicitud = null!;
        if (!Guid.TryParse(clave, out var idempotencia) || idempotencia == Guid.Empty
            || importe <= 0m || importe > 9_999_999_999.99m || decimal.Round(importe, 2) != importe)
            return false;
        origen = origen?.Trim();
        destino = destino?.Trim();
        if (origen is null || destino is null || !CuentaRegex().IsMatch(origen)
            || !CuentaRegex().IsMatch(destino) || origen == destino)
            return false;
        solicitud = new TransferenciaRequest(origen, destino, importe, idempotencia);
        return true;
    }
}
