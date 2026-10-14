using System.Globalization;
using System.Windows;
using ClienteEscritorioGR09.Modelos;
using ClienteEscritorioGR09.Controladores;

namespace ClienteEscritorioGR09.Vistas;

public partial class VentanaPrincipal : Window
{
    private readonly ControladorConversor _controlador = new();

    public VentanaPrincipal()
    {
        InitializeComponent();
        MagnitudBox.ItemsSource = Enum.GetValues<TipoConversion>();
        MagnitudBox.SelectedIndex = 0;
    }

    private async void IniciarSesion_Click(object sender, RoutedEventArgs e)
    {
        if (string.IsNullOrWhiteSpace(UsuarioBox.Text) || string.IsNullOrWhiteSpace(ContraseniaBox.Password))
        {
            EstadoText.Text = "Ingresa usuario y contraseña.";
            return;
        }

        try
        {
            await _controlador.IniciarSesionAsync(UsuarioBox.Text.Trim(), ContraseniaBox.Password);
            PanelLogin.Visibility = Visibility.Collapsed;
            PanelConversor.Visibility = Visibility.Visible;
            EstadoText.Text = "Sesión iniciada.";
        }
        catch (Exception excepcion)
        {
            EstadoText.Text = $"Error: {excepcion.Message}";
        }
    }

    private void Magnitud_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
    {
        if (MagnitudBox.SelectedItem is not TipoConversion tipo || UnidadInicialBox is null)
        {
            return;
        }

        var unidades = CatalogoUnidades.Obtener(tipo);
        UnidadInicialBox.ItemsSource = unidades;
        UnidadInicialBox.DisplayMemberPath = nameof(OpcionUnidad.Etiqueta);
        UnidadInicialBox.SelectedValuePath = nameof(OpcionUnidad.Codigo);
        UnidadInicialBox.SelectedIndex = 0;

        UnidadFinalBox.ItemsSource = unidades;
        UnidadFinalBox.DisplayMemberPath = nameof(OpcionUnidad.Etiqueta);
        UnidadFinalBox.SelectedValuePath = nameof(OpcionUnidad.Codigo);
        UnidadFinalBox.SelectedIndex = Math.Min(1, unidades.Count - 1);
    }

    private async void Convertir_Click(object sender, RoutedEventArgs e)
    {
        if (MagnitudBox.SelectedItem is not TipoConversion tipo
            || !double.TryParse(ValorBox.Text.Trim().Replace(',', '.'), NumberStyles.Float,
                CultureInfo.InvariantCulture, out var valor)
            || !double.IsFinite(valor)
            || UnidadInicialBox.SelectedValue is not string inicial
            || UnidadFinalBox.SelectedValue is not string final)
        {
            EstadoText.Text = "Ingresa un número finito y selecciona ambas unidades.";
            return;
        }
        if (tipo == TipoConversion.Masa && valor < 0)
        {
            EstadoText.Text = "La masa no puede ser negativa.";
            return;
        }

        try
        {
            var resultado = await _controlador.ConvertirAsync(tipo, valor, inicial, final);
            if (!double.IsFinite(resultado))
            {
                throw new InvalidOperationException("El resultado excede el rango permitido.");
            }
            ResultadoText.Text = $"Resultado: {resultado.ToString("G", CultureInfo.CurrentCulture)} {final}";
            EstadoText.Text = string.Empty;
        }
        catch (Exception excepcion)
        {
            EstadoText.Text = $"Error: {excepcion.Message}";
        }
    }

    private async void CambiarContrasenia_Click(object sender, RoutedEventArgs e)
    {
        if (string.IsNullOrWhiteSpace(ContraseniaActualBox.Password)
            || string.IsNullOrWhiteSpace(ContraseniaNuevaBox.Password))
        {
            EstadoText.Text = "Ingresa ambas contraseñas.";
            return;
        }
        try
        {
            EstadoText.Text = await _controlador.CambiarContraseniaAsync(
                ContraseniaActualBox.Password, ContraseniaNuevaBox.Password);
            ContraseniaActualBox.Clear();
            ContraseniaNuevaBox.Clear();
        }
        catch (Exception excepcion)
        {
            EstadoText.Text = $"Error: {excepcion.Message}";
        }
    }

    private void CerrarSesion_Click(object sender, RoutedEventArgs e)
    {
        _controlador.CerrarSesion();
        PanelConversor.Visibility = Visibility.Collapsed;
        PanelLogin.Visibility = Visibility.Visible;
        ContraseniaBox.Clear();
        ResultadoText.Text = string.Empty;
        EstadoText.Text = "Sesión cerrada.";
    }
}
