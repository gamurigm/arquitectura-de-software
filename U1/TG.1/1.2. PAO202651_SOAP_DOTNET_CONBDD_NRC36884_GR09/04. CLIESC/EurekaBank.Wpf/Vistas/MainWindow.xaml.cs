using System.Globalization;
using System.Windows;
using ec.edu.monster.Controladores;
using ec.edu.monster.Servicios;

namespace ec.edu.monster.Vistas;

public partial class MainWindow : Window
{
    private readonly EurekaSoapClient _servicio = new();
    private readonly EurekaBankController _controlador;

    public MainWindow()
    {
        InitializeComponent();
        _controlador = new EurekaBankController(_servicio);
        Closed += (_, _) => _servicio.Dispose();
    }

    private async void IniciarSesion_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            if (!_controlador.SesionActiva && await _controlador.IniciarSesionAsync(Usuario.Text, Contrasena.Password))
            {
                PanelLogin.Visibility = Visibility.Collapsed;
                PanelBanco.Visibility = Visibility.Visible;
                Contrasena.Clear();
                Cuenta.ItemsSource = _controlador.Cuentas;
                CuentaOrigen.ItemsSource = _controlador.Cuentas;
                Cuenta.SelectedIndex = CuentaOrigen.SelectedIndex = _controlador.Cuentas.Count > 0 ? 0 : -1;
                if (_controlador.Cuentas.Count == 0) MensajeBanco.Text = "No hay cuentas activas asignadas.";
                await ConsultarAsync();
            }
            else MensajeLogin.Text = "Credenciales inválidas.";
        }
        catch (Exception error) { MensajeLogin.Text = $"No se pudo iniciar sesión: {error.Message}"; }
    }

    private async void Consultar_Click(object sender, RoutedEventArgs e) => await ConsultarAsync();

    private async Task<bool> ConsultarAsync()
    {
        try
        {
            if (Cuenta.SelectedValue is not string codigo) { MensajeBanco.Text = "Seleccione una cuenta activa."; return false; }
            await _controlador.ConsultarMovimientosAsync(codigo);
            TablaMovimientos.ItemsSource = _controlador.Movimientos;
            Saldo.Text = $"Saldo calculado: {_controlador.Saldo:N2}";
            MensajeBanco.Text = "Movimientos actualizados.";
            return true;
        }
        catch (UnauthorizedAccessException error) { MostrarLoginCaducado(error.Message); return false; }
        catch (Exception error) { MensajeBanco.Text = error.Message; return false; }
    }

    private async void Depositar_Click(object sender, RoutedEventArgs e)
    {
        if (!decimal.TryParse(Importe.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe)
            || importe <= 0 || decimal.Round(importe, 2) != importe)
        {
            MensajeBanco.Text = "Ingrese un importe positivo con hasta dos decimales.";
            return;
        }
        try
        {
            var registrado = await _controlador.RegistrarDepositoAsync(importe);
            MensajeBanco.Text = registrado ? "Depósito registrado." : "No se pudo registrar el depósito.";
            if (registrado)
                MensajeBanco.Text = await ConsultarAsync()
                    ? "Depósito registrado; movimientos actualizados."
                    : "Depósito registrado; no se pudieron actualizar los movimientos.";
        }
        catch (UnauthorizedAccessException error) { MostrarLoginCaducado(error.Message); }
        catch (Exception error) { MensajeBanco.Text = $"No se pudo registrar el depósito: {error.Message}"; }
    }

    private void CerrarSesion_Click(object sender, RoutedEventArgs e)
    {
        _ = CerrarSesionAsync();
    }

    private async Task CerrarSesionAsync()
    {
        try { await _controlador.CerrarSesionAsync(); }
        catch (Exception error) { MensajeLogin.Text = $"Sesión local cerrada; el servidor la expirará: {error.Message}"; }
        TablaMovimientos.ItemsSource = null;
        Saldo.Text = "";
        MensajeLogin.Text = "";
        PanelBanco.Visibility = Visibility.Collapsed;
        PanelLogin.Visibility = Visibility.Visible;
    }

    private async void Transferir_Click(object sender, RoutedEventArgs e)
    {
        var origen = CuentaOrigen.SelectedValue as string ?? "";
        var destino = CuentaDestino.Text.Trim();
        if (!decimal.TryParse(ImporteTransferencia.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe)
            || !origen.All(char.IsAsciiDigit) || origen.Length != 8 || !destino.All(char.IsAsciiDigit)
            || destino.Length != 8 || origen == destino || importe <= 0 || decimal.Round(importe, 2) != importe)
        { MensajeBanco.Text = "Use dos cuentas distintas de ocho dígitos e importe positivo con dos decimales."; return; }
        try
        {
            var registrada = await _controlador.TransferirAsync(origen, destino, importe);
            MensajeBanco.Text = registrada ? "Transferencia registrada: débito y crédito confirmados." : "Transferencia rechazada. Revise cuentas, moneda e importe.";
            if (registrada)
            {
                Cuenta.SelectedValue = origen;
                await ConsultarAsync();
            }
        }
        catch (UnauthorizedAccessException error) { MostrarLoginCaducado(error.Message); }
        catch (Exception error) { MensajeBanco.Text = $"No se confirmó. Reenvíe la misma solicitud. {error.Message}"; }
    }

    private void MostrarLoginCaducado(string mensaje)
    {
        TablaMovimientos.ItemsSource = null;
        Saldo.Text = "";
        PanelBanco.Visibility = Visibility.Collapsed;
        PanelLogin.Visibility = Visibility.Visible;
        MensajeLogin.Text = mensaje;
    }
}
