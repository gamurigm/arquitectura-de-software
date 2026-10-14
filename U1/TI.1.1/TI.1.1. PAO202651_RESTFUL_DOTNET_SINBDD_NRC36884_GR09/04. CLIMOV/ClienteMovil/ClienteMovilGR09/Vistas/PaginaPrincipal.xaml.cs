using System.Globalization;
using ClienteMovilGR09.Controladores;
using ClienteMovilGR09.Modelos;
using ClienteMovilGR09.Servicios;

namespace ClienteMovilGR09;

public partial class PaginaPrincipal : ContentPage
{
    private readonly ControladorConversor _controlador = new(new ClienteRest());
    private CatalogoRespuesta? _catalogo;

    public PaginaPrincipal()
    {
        InitializeComponent();
    }

    private async void IniciarSesion_Clicked(object? sender, EventArgs e)
    {
        lblMensajeLogin.Text = "";
        if (string.IsNullOrWhiteSpace(txtUsuario.Text) || string.IsNullOrWhiteSpace(txtClave.Text))
        {
            lblMensajeLogin.Text = "Ingresa usuario y contraseña.";
            return;
        }

        try
        {
            var sesion = await _controlador.IniciarSesionAsync(txtUsuario.Text ?? "", txtClave.Text ?? "");
            _catalogo = await _controlador.ObtenerCatalogoAsync();
            pickerMagnitud.ItemsSource = _catalogo.Magnitudes.Keys.ToList();
            pickerMagnitud.SelectedIndex = 0;
            lblSesion.Text = $"Sesión de {sesion.Usuario}. Token válido por 10 minutos.";
            panelLogin.IsVisible = false;
            panelConversion.IsVisible = true;
        }
        catch (InvalidOperationException ex)
        {
            lblMensajeLogin.Text = ex.Message;
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
        lblMensaje.Text = "";
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
            var resultado = await _controlador.ConvertirAsync(magnitud, valor, origen, destino);
            if (!double.IsFinite(resultado.Resultado))
            {
                throw new InvalidOperationException("El servidor devolvió un resultado inválido.");
            }
            lblResultado.Text = resultado.Mensaje;
        }
        catch (ExcepcionServicio ex)
        {
            lblMensaje.Text = ex.Message;
            if (ex.Codigo == 401)
            {
                panelConversion.IsVisible = false;
                panelLogin.IsVisible = true;
                lblMensajeLogin.Text = "La sesión venció. Inicie sesión nuevamente.";
            }
        }
        catch (InvalidOperationException ex)
        {
            lblMensaje.Text = ex.Message;
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
        txtClave.Text = "";
    }
}
