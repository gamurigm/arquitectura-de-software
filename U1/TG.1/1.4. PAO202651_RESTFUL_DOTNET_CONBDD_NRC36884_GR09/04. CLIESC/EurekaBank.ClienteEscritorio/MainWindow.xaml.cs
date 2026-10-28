using System.Globalization;
using System.Net.Http;
using System.Windows;
using EurekaBank.Cliente.Modelos;
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
            foreach (var selector in new[] { CuentaConsultaBox, CuentaDepositoBox, CuentaOrigenBox })
            {
                selector.ItemsSource = controlador.Cuentas;
                selector.DisplayMemberPath = nameof(CuentaDto.Etiqueta);
                selector.SelectedValuePath = nameof(CuentaDto.Numero);
                selector.SelectedIndex = controlador.Cuentas.Count > 0 ? 0 : -1;
            }
            Mensaje(controlador.Cuentas.Count == 0 ? "No tiene cuentas activas asignadas." : string.Empty);
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
            if (CuentaConsultaBox.SelectedValue is not string cuenta) { Mensaje("Seleccione una cuenta."); return; }
            MovimientosGrid.ItemsSource = await controlador.ConsultarMovimientosAsync(cuenta);
            Mensaje("Consulta completada.");
        }
        catch (HttpRequestException)
        {
            Mensaje("No se pudieron consultar los movimientos.");
        }
        catch (UnauthorizedAccessException) { MostrarLoginPorCaducidad(); }
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
            if (CuentaDepositoBox.SelectedValue is not string cuenta) { Mensaje("Seleccione una cuenta."); return; }
            Mensaje(await controlador.RegistrarDepositoAsync(cuenta, importe)
                ? "Depósito registrado."
                : "Depósito rechazado. Revise cuenta e importe.");
        }
        catch (HttpRequestException)
        {
            Mensaje("No se pudo registrar el depósito.");
        }
        catch (UnauthorizedAccessException) { MostrarLoginPorCaducidad(); }
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
            if (CuentaOrigenBox.SelectedValue is not string origen) { Mensaje("Seleccione una cuenta de origen."); return; }
            Mensaje(await controlador.TransferirAsync(origen, CuentaDestinoBox.Text.Trim(),
                importe, Guid.NewGuid())
                ? "Transferencia registrada: débito y crédito confirmados."
                : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
        }
        catch (HttpRequestException)
        {
            Mensaje("No se confirmó el resultado. Reintente con la misma clave de operación.");
        }
        catch (UnauthorizedAccessException) { MostrarLoginPorCaducidad(); }
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

    private void MostrarLoginPorCaducidad()
    {
        MovimientosGrid.ItemsSource = null;
        BancoPanel.Visibility = Visibility.Collapsed;
        LoginPanel.Visibility = Visibility.Visible;
        Mensaje("La sesión caducó. Inicie sesión nuevamente.");
    }
}
