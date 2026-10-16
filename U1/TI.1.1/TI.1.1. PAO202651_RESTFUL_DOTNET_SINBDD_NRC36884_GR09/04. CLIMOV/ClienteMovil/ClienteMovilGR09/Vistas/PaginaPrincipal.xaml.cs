using System.Globalization;
using ClienteMovilGR09.Controladores;
using ClienteMovilGR09.Modelos;
using ClienteMovilGR09.Servicios;

namespace ClienteMovilGR09;

public partial class PaginaPrincipal : ContentPage
{
    private readonly ControladorConversor _controlador = new(new ClienteRest());
    private CatalogoRespuesta? _catalogo;
    private bool _ocupado;

    public PaginaPrincipal()
    {
        InitializeComponent();
    }

    private async void IniciarSesion_Clicked(object? sender, EventArgs e)
    {
        if (_ocupado) return;
        lblMensajeLogin.Text = "";
        if (string.IsNullOrWhiteSpace(txtUsuario.Text) || string.IsNullOrWhiteSpace(txtClave.Text))
        {
            lblMensajeLogin.Text = "Ingresa usuario y contraseña.";
            return;
        }

        try
        {
            EstablecerOcupado(true, BotonIngresar, "Conectando…");
            txtClave.Unfocus();
            var sesion = await _controlador.IniciarSesionAsync(txtUsuario.Text ?? "", txtClave.Text ?? "");
            _catalogo = await _controlador.ObtenerCatalogoAsync();
            pickerMagnitud.ItemsSource = _catalogo.Magnitudes.Keys.ToList();
            pickerMagnitud.SelectedIndex = 0;
            lblSesion.Text = $"Hola, {sesion.Usuario}. Elige qué convertir.";
            txtClave.Text = "";
            VerContrasenia.IsChecked = false;
            panelLogin.IsVisible = false;
            panelConversion.IsVisible = true;
            Presentacion.IsVisible = false;
            await Desplazamiento.ScrollToAsync(0, 0, false);
        }
        catch (InvalidOperationException ex)
        {
            lblMensajeLogin.Text = ex.Message;
        }
        finally
        {
            EstablecerOcupado(false, BotonIngresar, "Entrar al conversor");
        }
    }

    private void Magnitud_SelectionChanged(object? sender, EventArgs e)
    {
        if (_catalogo is null || pickerMagnitud.SelectedItem is not string magnitud
            || !_catalogo.Magnitudes.TryGetValue(magnitud, out var unidades))
        {
            return;
        }

        pickerOrigen.ItemsSource = unidades;
        pickerDestino.ItemsSource = unidades;
        pickerOrigen.SelectedIndex = 0;
        pickerDestino.SelectedIndex = unidades.Count > 1 ? 1 : 0;
    }

    private async void Convertir_Clicked(object? sender, EventArgs e)
    {
        if (_ocupado || !panelConversion.IsVisible) return;
        AlCambiarEntrada(sender, e);
        if (pickerMagnitud.SelectedItem is not string magnitud
            || pickerOrigen.SelectedItem is not string origen
            || pickerDestino.SelectedItem is not string destino
            || !double.TryParse(txtValor.Text, NumberStyles.Float, CultureInfo.CurrentCulture, out var valor)
            || !double.IsFinite(valor))
        {
            lblMensaje.Text = "Ingrese un número finito y seleccione las unidades.";
            return;
        }
        if (string.Equals(magnitud, "masa", StringComparison.OrdinalIgnoreCase) && valor < 0)
        {
            lblMensaje.Text = "La masa no puede ser negativa.";
            return;
        }

        try
        {
            EstablecerOcupado(true, BotonConvertir, "Convirtiendo…");
            txtValor.Unfocus();
            var resultado = await _controlador.ConvertirAsync(magnitud, valor, origen, destino);
            if (!double.IsFinite(resultado.Resultado))
            {
                throw new InvalidOperationException("El servidor devolvió un resultado inválido.");
            }
            lblResultado.Text = $"{resultado.Resultado.ToString("G", CultureInfo.CurrentCulture)} {destino}";
            await Desplazamiento.ScrollToAsync(TarjetaResultado, ScrollToPosition.MakeVisible, true);
        }
        catch (ExcepcionServicio ex)
        {
            lblMensaje.Text = ex.Message;
            if (ex.Codigo == 401)
            {
                panelConversion.IsVisible = false;
                panelLogin.IsVisible = true;
                Presentacion.IsVisible = true;
                lblMensajeLogin.Text = "La sesión venció. Inicie sesión nuevamente.";
            }
        }
        catch (InvalidOperationException ex)
        {
            lblMensaje.Text = ex.Message;
        }
        finally
        {
            EstablecerOcupado(false, BotonConvertir, "Convertir ahora");
        }
    }

    private async void CerrarSesion_Clicked(object? sender, EventArgs e)
    {
        try
        {
            await _controlador.CerrarSesionAsync();
        }
        catch (InvalidOperationException ex)
        {
            lblMensaje.Text = ex.Message;
        }

        panelConversion.IsVisible = false;
        panelLogin.IsVisible = true;
        Presentacion.IsVisible = true;
        txtClave.Text = "";
        AlCambiarEntrada(sender, e);
    }

    private void AlMostrarContrasenia(object? sender, CheckedChangedEventArgs e) =>
        txtClave.IsPassword = !e.Value;

    private void AlIntercambiarUnidades(object? sender, EventArgs e)
    {
        var origen = pickerOrigen.SelectedIndex;
        pickerOrigen.SelectedIndex = pickerDestino.SelectedIndex;
        pickerDestino.SelectedIndex = origen;
    }

    private void AlCambiarEntrada(object? sender, EventArgs e)
    {
        lblResultado.Text = "Tu resultado aparecerá aquí";
        lblMensaje.Text = "";
    }

    private void EstablecerOcupado(bool ocupado, Button boton, string texto)
    {
        _ocupado = ocupado;
        panelLogin.IsEnabled = !ocupado;
        panelConversion.IsEnabled = !ocupado;
        boton.Text = texto;
    }
}
