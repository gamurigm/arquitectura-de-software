using System.Globalization;
using ec.edu.monster.Controladores;
using ec.edu.monster.Servicios;

namespace ec.edu.monster.Vistas;

public partial class MainPage : ContentPage
{
    private EurekaSoapClient? _servicio;
    private EurekaBankController? _controlador;
    private string? _endpointActual;

    public MainPage()
    {
        InitializeComponent();
#if ANDROID
        Endpoint.Text = Environment.GetEnvironmentVariable("EUREKABANK_SOAP_DOTNET_URL") ?? "http://10.0.2.2:5080/WSEureka";
#else
        Endpoint.Text = Environment.GetEnvironmentVariable("EUREKABANK_SOAP_DOTNET_URL") ?? "http://localhost:5080/WSEureka";
#endif
    }

    private async void IniciarSesion_Click(object? sender, EventArgs e)
    {
        try
        {
            var endpoint = Endpoint.Text?.Trim() ?? "";
            if (_controlador is null || _endpointActual != endpoint)
            {
                _servicio?.Dispose();
                _servicio = new EurekaSoapClient(endpoint);
                _controlador = new EurekaBankController(_servicio);
                _endpointActual = endpoint;
            }
            if (await _controlador!.IniciarSesionAsync(Usuario.Text ?? "", Contrasena.Text ?? ""))
            {
                LoginPanel.IsVisible = false;
                BancoPanel.IsVisible = true;
                UsuarioEtiqueta.Text = $"Hola, {Usuario.Text?.Trim()}";
                Contrasena.Text = "";
                Mensaje.Text = "Sesión iniciada.";
                Cuenta.ItemsSource = _controlador.Cuentas.ToList();
                CuentaOrigenTransferencia.ItemsSource = _controlador.Cuentas.ToList();
                Cuenta.SelectedIndex = CuentaOrigenTransferencia.SelectedIndex = _controlador.Cuentas.Count > 0 ? 0 : -1;
                if (_controlador.Cuentas.Count == 0) Mensaje.Text = "No hay cuentas activas asignadas.";
                await ConsultarAsync();
            }
            else Mensaje.Text = "Credenciales inválidas.";
        }
        catch (Exception error) { Mensaje.Text = $"No se pudo iniciar sesión: {error.Message}"; }
    }

    private async void Consultar_Clicked(object? sender, EventArgs e) => await ConsultarAsync();

    private async Task<bool> ConsultarAsync()
    {
        try
        {
            if (Cuenta.SelectedItem is not ec.edu.monster.Modelos.Cuenta cuenta) { Mensaje.Text = "Seleccione una cuenta activa."; return false; }
            await _controlador!.ConsultarMovimientosAsync(cuenta.Numero);
            Saldo.Text = $"Saldo calculado: {_controlador.Saldo:N2}";
            ListaMovimientos.Text = _controlador.Movimientos.Count == 0
                ? "No hay movimientos para esta cuenta."
                : string.Join(Environment.NewLine, _controlador.Movimientos.Select(m => $"{m.Fecha:yyyy-MM-dd}  {m.Tipo}\n{m.Accion}  {m.Importe.ToString("N2", CultureInfo.CurrentCulture)}"));
            Mensaje.Text = "";
            return true;
        }
        catch (UnauthorizedAccessException error) { MostrarLoginCaducado(error.Message); return false; }
        catch (Exception error) { Mensaje.Text = error.Message; return false; }
    }

    private async void Depositar_Clicked(object? sender, EventArgs e)
    {
        if (!decimal.TryParse(Importe.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe)
            || importe <= 0 || decimal.Round(importe, 2) != importe)
        {
            Mensaje.Text = "Ingrese un importe positivo con hasta dos decimales.";
            return;
        }
        try
        {
            var registrado = await _controlador!.RegistrarDepositoAsync(importe);
            Mensaje.Text = registrado ? "Depósito registrado." : "No se pudo registrar el depósito.";
            if (registrado)
                Mensaje.Text = await ConsultarAsync()
                    ? "Depósito registrado; movimientos actualizados."
                    : "Depósito registrado; no se pudieron actualizar los movimientos.";
        }
        catch (UnauthorizedAccessException error) { MostrarLoginCaducado(error.Message); }
        catch (Exception error) { Mensaje.Text = $"No se pudo registrar el depósito: {error.Message}"; }
    }

    private async void CerrarSesion_Clicked(object? sender, EventArgs e)
    {
        try { await _controlador!.CerrarSesionAsync(); }
        catch (Exception error) { Mensaje.Text = $"Sesión local cerrada; el servidor la expirará: {error.Message}"; }
        BancoPanel.IsVisible = false;
        LoginPanel.IsVisible = true;
        ListaMovimientos.Text = "Consulte una cuenta para ver movimientos.";
        Saldo.Text = "Saldo: --";
        UsuarioEtiqueta.Text = "Inicie sesión para continuar";
        Mensaje.Text = "Sesión cerrada.";
    }

    private async void Transferir_Clicked(object? sender, EventArgs e)
    {
        var origen = (CuentaOrigenTransferencia.SelectedItem as ec.edu.monster.Modelos.Cuenta)?.Numero ?? "";
        var destino = CuentaDestinoTransferencia.Text?.Trim() ?? "";
        if (!decimal.TryParse(ImporteTransferencia.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe)
            || origen.Length != 8 || !origen.All(char.IsAsciiDigit) || destino.Length != 8
            || !destino.All(char.IsAsciiDigit) || origen == destino || importe <= 0 || decimal.Round(importe, 2) != importe)
        { Mensaje.Text = "Use dos cuentas distintas de ocho dígitos e importe positivo con dos decimales."; return; }
        try
        {
            var registrada = await _controlador!.TransferirAsync(origen, destino, importe);
            Mensaje.Text = registrada ? "Transferencia registrada: débito y crédito confirmados." : "Transferencia rechazada. Revise cuentas, moneda e importe.";
            if (registrada)
            {
                Cuenta.SelectedItem = _controlador.Cuentas.FirstOrDefault(c => c.Numero == origen);
                await ConsultarAsync();
            }
        }
        catch (UnauthorizedAccessException error) { MostrarLoginCaducado(error.Message); }
        catch (Exception error) { Mensaje.Text = $"No se confirmó. Reenvíe la misma solicitud. {error.Message}"; }
    }

    private void MostrarLoginCaducado(string mensaje)
    {
        BancoPanel.IsVisible = false;
        LoginPanel.IsVisible = true;
        ListaMovimientos.Text = "Consulte una cuenta para ver movimientos.";
        Saldo.Text = "Saldo: --";
        UsuarioEtiqueta.Text = "Inicie sesión para continuar";
        Mensaje.Text = mensaje;
    }
}
