using System.Globalization;
using EurekaBank.Cliente.Controladores;

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
            MostrarMensaje(string.Empty);
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
                CuentaConsultaEntry.Text?.Trim() ?? string.Empty);
            MostrarMensaje("Consulta completada.");
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se pudieron consultar los movimientos.");
        }
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
                    CuentaDepositoEntry.Text?.Trim() ?? string.Empty, importe)
                ? "Depósito registrado."
                : "Depósito rechazado. Revise cuenta e importe.");
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se pudo registrar el depósito.");
        }
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
                    CuentaOrigenTransferenciaEntry.Text?.Trim() ?? string.Empty,
                    CuentaDestinoTransferenciaEntry.Text?.Trim() ?? string.Empty, importe, Guid.NewGuid())
                ? "Transferencia registrada: débito y crédito confirmados."
                : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
        }
        catch (HttpRequestException)
        {
            MostrarMensaje("No se confirmó el resultado. Reintente con la misma clave de operación.");
        }
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
}
