using System.Globalization;
using EurekaBank.Cliente.Controladores;
using EurekaBank.Cliente.Modelos;

namespace EurekaBank.ClienteMovil;

public partial class MainPage(EurekaBankController controlador) : ContentPage
{
    private async void IniciarSesion_Click(object sender, EventArgs e)
    {
        try
        {
            if (!await controlador.IniciarSesionAsync(UsuarioEntry.Text?.Trim() ?? string.Empty,
                    ContrasenaEntry.Text ?? string.Empty))
            {
                MostrarMensaje("Credenciales incorrectas o cuenta sin asociación habilitada.");
                return;
            }

            LoginPanel.IsVisible = false;
            BancoPanel.IsVisible = true;
            SaludoLabel.Text = $"Bienvenido, {controlador.UsuarioActivo}";
            foreach (var selector in new[] { CuentaConsultaPicker, CuentaDepositoPicker, CuentaOrigenTransferenciaPicker })
            {
                selector.ItemsSource = controlador.Cuentas.ToList();
                selector.SelectedIndex = controlador.Cuentas.Count > 0 ? 0 : -1;
            }
            MostrarMensaje(controlador.Cuentas.Count == 0 ? "No tiene cuentas activas asignadas." : string.Empty);
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se pudo contactar la API. Revise EUREKABANK_REST_DOTNET_URL.");
        }
    }

    private async void Consultar_Click(object sender, EventArgs e)
    {
        try
        {
            MovimientosList.ItemsSource = await controlador.ConsultarMovimientosAsync(
                (CuentaConsultaPicker.SelectedItem as CuentaDto)?.Numero ?? string.Empty);
            MostrarMensaje("Consulta completada.");
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se pudieron consultar los movimientos.");
        }
        catch (UnauthorizedAccessException) { MostrarLoginPorCaducidad(); }
    }

    private async void Depositar_Click(object sender, EventArgs e)
    {
        if (!decimal.TryParse(ImporteEntry.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe))
        {
            MostrarMensaje("Ingrese un importe válido.");
            return;
        }

        try
        {
            MostrarMensaje(await controlador.RegistrarDepositoAsync(
                    (CuentaDepositoPicker.SelectedItem as CuentaDto)?.Numero ?? string.Empty, importe)
                ? "Depósito registrado."
                : "Depósito rechazado. Revise cuenta e importe.");
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se pudo registrar el depósito.");
        }
        catch (UnauthorizedAccessException) { MostrarLoginPorCaducidad(); }
    }

    private async void Transferir_Click(object sender, EventArgs e)
    {
        if (!decimal.TryParse(ImporteTransferenciaEntry.Text, NumberStyles.Number, CultureInfo.CurrentCulture, out var importe))
        {
            MostrarMensaje("Ingrese un importe válido.");
            return;
        }
        try
        {
            MostrarMensaje(await controlador.TransferirAsync(
                    (CuentaOrigenTransferenciaPicker.SelectedItem as CuentaDto)?.Numero ?? string.Empty,
                    CuentaDestinoTransferenciaEntry.Text?.Trim() ?? string.Empty, importe, Guid.NewGuid())
                ? "Transferencia registrada: débito y crédito confirmados."
                : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se confirmó el resultado. Reintente con la misma clave de operación.");
        }
        catch (UnauthorizedAccessException) { MostrarLoginPorCaducidad(); }
    }

    private async void CerrarSesion_Click(object sender, EventArgs e)
    {
        try { await controlador.CerrarSesionAsync(); }
        catch (HttpRequestException) { }
        ContrasenaEntry.Text = string.Empty;
        MovimientosList.ItemsSource = null;
        BancoPanel.IsVisible = false;
        LoginPanel.IsVisible = true;
        MostrarMensaje(string.Empty);
    }

    private void MostrarMensaje(string texto) => MensajeLabel.Text = texto;

    private void MostrarLoginPorCaducidad()
    {
        MovimientosList.ItemsSource = null;
        BancoPanel.IsVisible = false;
        LoginPanel.IsVisible = true;
        MostrarMensaje("La sesión caducó. Inicie sesión nuevamente.");
    }
}
