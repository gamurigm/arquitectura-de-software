using System.Globalization;
using System.Net.Http;
using System.Windows;
using EurekaBank.Cliente.Controladores;

namespace EurekaBank.ClienteEscritorio;

public partial class MainWindow : Window
{
    private readonly EurekaBankController controlador;

    public MainWindow(EurekaBankController controlador)
    {
        InitializeComponent();
        this.controlador = controlador;
    }

    private async void IniciarSesion_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            if (!await controlador.IniciarSesionAsync(UsuarioBox.Text.Trim(), ContrasenaBox.Password))
            {
                Mensaje("Credenciales incorrectas o cuenta sin asociación habilitada.");
                return;
            }

            LoginPanel.Visibility = Visibility.Collapsed;
            BancoPanel.Visibility = Visibility.Visible;
            SaludoText.Text = $"Bienvenido, {controlador.UsuarioActivo}";
            Mensaje(string.Empty);
        }
        catch (HttpRequestException)
        {
            Mensaje("No se pudo contactar la API. Revise EUREKABANK_REST_DOTNET_URL.");
        }
    }

    private async void Consultar_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            MovimientosGrid.ItemsSource = await controlador.ConsultarMovimientosAsync(CuentaConsultaBox.Text.Trim());
            Mensaje("Consulta completada.");
        }
        catch (HttpRequestException)
        {
            Mensaje("No se pudieron consultar los movimientos.");
        }
    }

    private async void Depositar_Click(object sender, RoutedEventArgs e)
    {
        if (!decimal.TryParse(ImporteBox.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe))
        {
            Mensaje("Ingrese un importe válido.");
            return;
        }

        try
        {
            Mensaje(await controlador.RegistrarDepositoAsync(CuentaDepositoBox.Text.Trim(), importe)
                ? "Depósito registrado."
                : "Depósito rechazado. Revise cuenta e importe.");
        }
        catch (HttpRequestException)
        {
            Mensaje("No se pudo registrar el depósito.");
        }
    }

    private async void Transferir_Click(object sender, RoutedEventArgs e)
    {
        if (!decimal.TryParse(ImporteTransferenciaBox.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe))
        {
            Mensaje("Ingrese un importe válido.");
            return;
        }
        try
        {
            Mensaje(await controlador.TransferirAsync(CuentaOrigenBox.Text.Trim(), CuentaDestinoBox.Text.Trim(),
                importe, Guid.NewGuid())
                ? "Transferencia registrada: débito y crédito confirmados."
                : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
        }
        catch (HttpRequestException)
        {
            Mensaje("No se confirmó el resultado. Reintente con la misma clave de operación.");
        }
    }

    private async void CerrarSesion_Click(object sender, RoutedEventArgs e)
    {
        try { await controlador.CerrarSesionAsync(); }
        catch (HttpRequestException) { }
        ContrasenaBox.Clear();
        MovimientosGrid.ItemsSource = null;
        BancoPanel.Visibility = Visibility.Collapsed;
        LoginPanel.Visibility = Visibility.Visible;
        Mensaje(string.Empty);
    }

    private void Mensaje(string texto) => MensajeText.Text = texto;
}
