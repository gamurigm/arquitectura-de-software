using EurekaBank.Cliente.Modelos;
using EurekaBank.Cliente.Servicios;

namespace EurekaBank.Cliente.Controladores;

public sealed class EurekaBankController(IEurekaBankService servicio)
{
    private TransferenciaPendiente? _transferenciaPendiente;
    public string? UsuarioActivo { get; private set; }
    public string? Token { get; private set; }
    public IReadOnlyList<CuentaDto> Cuentas { get; private set; } = [];
    public bool SesionActiva => UsuarioActivo is not null && Token is not null;

    public async Task<bool> IniciarSesionAsync(
        string usuario,
        string contrasena,
        CancellationToken cancellationToken = default)
    {
        var respuesta = await servicio.IniciarSesionAsync(usuario, contrasena, cancellationToken);
        if (!respuesta.Autenticado || string.IsNullOrWhiteSpace(respuesta.Token))
        {
            return false;
        }

        UsuarioActivo = usuario;
        Token = respuesta.Token;
        Cuentas = await ConSesionAsync(() => servicio.ConsultarCuentasAsync(Token!, cancellationToken));
        return true;
    }

    public Task<IReadOnlyList<MovimientoDto>> ConsultarMovimientosAsync(
        string cuenta,
        CancellationToken cancellationToken = default)
    {
        ExigirSesion();
        return ConSesionAsync(() => servicio.ConsultarMovimientosAsync(cuenta, Token!, cancellationToken));
    }

    public Task<bool> RegistrarDepositoAsync(
        string cuenta,
        decimal importe,
        CancellationToken cancellationToken = default)
    {
        ExigirSesion();
        return ConSesionAsync(() => servicio.RegistrarDepositoAsync(cuenta, importe, Token!, cancellationToken));
    }

    public Task<bool> TransferirAsync(string origen, string destino, decimal importe, Guid claveOperacion,
        CancellationToken cancellationToken = default)
    {
        ExigirSesion();
        var clave = _transferenciaPendiente is { } pendiente
            && pendiente.Origen == origen && pendiente.Destino == destino && pendiente.Importe == importe
                ? pendiente.Clave
                : claveOperacion;
        _transferenciaPendiente = new TransferenciaPendiente(origen, destino, importe, clave);
        return TransferirPendienteAsync(clave, origen, destino, importe, cancellationToken);
    }

    private async Task<bool> TransferirPendienteAsync(Guid clave, string origen, string destino, decimal importe,
        CancellationToken cancellationToken)
    {
        var resultado = await ConSesionAsync(() => servicio.TransferirAsync(origen, destino, importe, clave, Token!, cancellationToken));
        _transferenciaPendiente = null;
        return resultado;
    }

    public async Task CerrarSesionAsync(CancellationToken cancellationToken = default)
    {
        var token = Token;
        Token = null;
        UsuarioActivo = null;
        Cuentas = [];
        await servicio.CerrarSesionAsync(token, cancellationToken);
    }

    private void ExigirSesion()
    {
        if (!SesionActiva)
        {
            throw new InvalidOperationException("Inicie sesión antes de operar.");
        }
    }

    private async Task<T> ConSesionAsync<T>(Func<Task<T>> operacion)
    {
        try { return await operacion(); }
        catch (HttpRequestException error) when (error.StatusCode == System.Net.HttpStatusCode.Unauthorized)
        {
            Token = null;
            UsuarioActivo = null;
            Cuentas = [];
            _transferenciaPendiente = null;
            throw new UnauthorizedAccessException("La sesión caducó. Inicie sesión nuevamente.", error);
        }
    }

    private sealed record TransferenciaPendiente(string Origen, string Destino, decimal Importe, Guid Clave);
}
