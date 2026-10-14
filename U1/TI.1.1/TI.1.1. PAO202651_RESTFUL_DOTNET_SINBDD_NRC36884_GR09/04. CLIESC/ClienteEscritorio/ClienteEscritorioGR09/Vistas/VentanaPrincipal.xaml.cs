using System.Globalization;
using System.Windows;
using ClienteEscritorioGR09.Controladores;
using ClienteEscritorioGR09.Modelos;
using ClienteEscritorioGR09.Servicios;

namespace ClienteEscritorioGR09;

public partial class VentanaPrincipal : Window
{
    private readonly ControladorConversor _controlador = new(new ClienteRest());
    private CatalogoRespuesta? _catalogo;

    public VentanaPrincipal()
    {
        InitializeComponent();
        txtUsuario.Focus();
    }

    private async void IniciarSesion_Click(object sender, RoutedEventArgs e)
    {
        lblMensajeLogin.Text = "";
        if (string.IsNullOrWhiteSpace(txtUsuario.Text) || string.IsNullOrWhiteSpace(txtClave.Password))
        {
            lblMensajeLogin.Text = "Ingrese usuario y contraseña.";
            return;
        }

        try
        {
            var sesion = await _controlador.IniciarSesionAsync(txtUsuario.Text, txtClave.Password);
            _catalogo = await _controlador.ObtenerCatalogoAsync();
            lblSesion.Text = $"Sesión de {sesion.Usuario}. El token vence en 10 minutos.";
            cmbMagnitud.ItemsSource = _catalogo.Magnitudes.Keys;
            cmbMagnitud.SelectedIndex = 0;
            panelLogin.Visibility = Visibility.Collapsed;
            panelConversion.Visibility = Visibility.Visible;
        }
        catch (InvalidOperationException ex)
        {
            lblMensajeLogin.Text = ex.Message;
        }
    }

    private void Magnitud_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
    {
        if (_catalogo is null || cmbMagnitud.SelectedItem is not string magnitud
            || !_catalogo.Magnitudes.TryGetValue(magnitud, out var unidades))
        {
            return;
        }

        cmbOrigen.ItemsSource = unidades;
        cmbDestino.ItemsSource = unidades;
        cmbOrigen.SelectedIndex = 0;
        cmbDestino.SelectedIndex = unidades.Count > 1 ? 1 : 0;
    }

    private async void Convertir_Click(object sender, RoutedEventArgs e)
    {
        lblMensaje.Text = "";
        if (cmbMagnitud.SelectedItem is not string magnitud
            || cmbOrigen.SelectedItem is not string origen
            || cmbDestino.SelectedItem is not string destino
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
            var respuesta = await _controlador.ConvertirAsync(magnitud, valor, origen, destino);
            if (!double.IsFinite(respuesta.Resultado))
            {
                throw new InvalidOperationException("El servidor devolvió un resultado inválido.");
            }
            lblResultado.Text = respuesta.Mensaje;
        }
        catch (InvalidOperationException ex)
        {
            lblMensaje.Text = ex.Message;
            if (ex.Message.Contains("token", StringComparison.OrdinalIgnoreCase))
            {
                panelConversion.Visibility = Visibility.Collapsed;
                panelLogin.Visibility = Visibility.Visible;
            }
        }
    }

    private async void CerrarSesion_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            await _controlador.CerrarSesionAsync();
        }
        catch (InvalidOperationException ex)
        {
            lblMensaje.Text = ex.Message;
        }

        panelConversion.Visibility = Visibility.Collapsed;
        panelLogin.Visibility = Visibility.Visible;
        txtClave.Clear();
    }
}
