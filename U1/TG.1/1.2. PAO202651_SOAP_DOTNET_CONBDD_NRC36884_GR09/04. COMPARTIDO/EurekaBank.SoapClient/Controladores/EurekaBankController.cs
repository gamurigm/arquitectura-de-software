using ec.edu.monster.Modelos;
using ec.edu.monster.Servicios;

namespace ec.edu.monster.Controladores;

public sealed class EurekaBankController(IEurekaSoapClient servicio)
{
    private readonly IEurekaSoapClient _servicio = servicio;
    private string? _cuenta;
    private string? _token;
    private TransferenciaPendiente? _transferenciaPendiente;

    public bool SesionActiva { get; private set; }
    public IReadOnlyList<Movimiento> Movimientos { get; private set; } = Array.Empty<Movimiento>();
    public decimal Saldo => Movimientos.Sum(movimiento =>
        movimiento.Accion.Equals("SALIDA", StringComparison.OrdinalIgnoreCase) ? -movimiento.Importe : movimiento.Importe);

    public async Task<bool> IniciarSesionAsync(string usuario, string contrasena, CancellationToken cancellationToken = default)
    {
        if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrEmpty(contrasena))
            return false;
        _token = await _servicio.CrearSesionAsync(usuario.Trim(), contrasena, cancellationToken);
        if (string.IsNullOrWhiteSpace(_token)) return false;
        SesionActiva = true;
        return true;
    }

    public async Task ConsultarMovimientosAsync(string cuenta, CancellationToken cancellationToken = default)
    {
        ExigirSesion();
        var cuentaValidada = cuenta?.Trim() ?? "";
        if (cuentaValidada.Length is < 1 or > 8)
            throw new ArgumentException("Ingrese una cuenta válida.", nameof(cuenta));
        _cuenta = cuentaValidada;
        Movimientos = await _servicio.TraerMovimientosAsync(_cuenta, cancellationToken);
    }

    public async Task<bool> RegistrarDepositoAsync(decimal importe, CancellationToken cancellationToken = default)
    {
        ExigirSesion();
        if (_cuenta is null)
            throw new InvalidOperationException("Consulte una cuenta antes de registrar un depósito.");
        if (importe <= 0 || decimal.Round(importe, 2) != importe)
            return false;
        return await _servicio.RegistrarDepositoAsync(_cuenta, importe, cancellationToken) == 1;
    }

    public async Task<bool> TransferirAsync(string origen, string destino, decimal importe,
        CancellationToken cancellationToken = default)
    {
        ExigirSesion();
        if (_token is null || origen.Length != 8 || destino.Length != 8 || origen == destino
            || importe <= 0 || importe > 9_999_999_999.99m || decimal.Round(importe, 2) != importe)
            return false;
        if (_transferenciaPendiente is not { } pendiente || !pendiente.Coincide(origen, destino, importe))
            _transferenciaPendiente = new TransferenciaPendiente(origen, destino, importe, Guid.NewGuid());
        var actual = _transferenciaPendiente!;
        var resultado = await _servicio.TransferirAsync(_token, origen, destino, importe, actual.Clave, cancellationToken) == 1;
        _transferenciaPendiente = null;
        return resultado;
    }

    public async Task CerrarSesionAsync(CancellationToken cancellationToken = default)
    {
        var token = _token;
        _token = null;
        SesionActiva = false;
        _cuenta = null;
        Movimientos = Array.Empty<Movimiento>();
        if (token is not null) await _servicio.CerrarSesionAsync(token, cancellationToken);
    }

    private void ExigirSesion()
    {
        if (!SesionActiva)
            throw new InvalidOperationException("Inicie sesión primero.");
    }

    private sealed record TransferenciaPendiente(string Origen, string Destino, decimal Importe, Guid Clave)
    {
        public bool Coincide(string origen, string destino, decimal importe) =>
            Origen == origen && Destino == destino && Importe == importe;
    }
}
