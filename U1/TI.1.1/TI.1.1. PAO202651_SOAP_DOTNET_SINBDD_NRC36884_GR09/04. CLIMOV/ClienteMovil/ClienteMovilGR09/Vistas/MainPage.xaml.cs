using System.Globalization;
using ClienteMovilGR09.Modelos;
using ClienteMovilGR09.Controladores;

namespace ClienteMovilGR09.Vistas;

public partial class MainPage : ContentPage
{
    private readonly ControladorConversor _controlador = new();

    public MainPage()
    {
        InitializeComponent();
        TipoPicker.SelectedIndex = 0;
    }

    private async void OnIniciarSesionClicked(object? sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(UsuarioEntry.Text) || string.IsNullOrWhiteSpace(ContraseniaEntry.Text))
        {
            MostrarMensaje("Ingresa usuario y contraseña.", true);
            return;
        }

        try
        {
            await _controlador.IniciarSesionAsync(UsuarioEntry.Text.Trim(), ContraseniaEntry.Text);
            ContraseniaEntry.Text = string.Empty;
            PanelInicioSesion.IsVisible = false;
            PanelConversor.IsVisible = true;
            MostrarMensaje("Sesión iniciada.", false);
        }
        catch (Exception excepcion)
        {
            MostrarMensaje(excepcion.Message, true);
        }
    }

    private async void OnConvertirClicked(object? sender, EventArgs e)
    {
        if (!double.TryParse(ValorEntry.Text, NumberStyles.Float, CultureInfo.CurrentCulture, out var valor))
        {
            MostrarMensaje("Ingresa un valor numérico válido.", true);
            return;
        }

        if (UnidadInicialPicker.SelectedItem is not OpcionUnidad inicial ||
            UnidadFinalPicker.SelectedItem is not OpcionUnidad final)
        {
            MostrarMensaje("Selecciona las unidades de origen y destino.", true);
            return;
        }

        try
        {
            var resultado = await _controlador.ConvertirAsync(TipoSeleccionado(), valor, inicial.Codigo, final.Codigo);
            ResultadoLabel.Text = $"Resultado: {resultado.ToString("G", CultureInfo.CurrentCulture)} {final.Etiqueta}";
            MostrarMensaje("Conversión completada.", false);
        }
        catch (Exception excepcion)
        {
            MostrarMensaje(excepcion.Message, true);
        }
    }

    private async void OnCambiarContraseniaClicked(object? sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(ContraseniaActualEntry.Text) || string.IsNullOrWhiteSpace(ContraseniaNuevaEntry.Text))
        {
            MostrarMensaje("Ingresa ambas contraseñas.", true);
            return;
        }

        try
        {
            var respuesta = await _controlador.CambiarContraseniaAsync(ContraseniaActualEntry.Text, ContraseniaNuevaEntry.Text);
            ContraseniaActualEntry.Text = string.Empty;
            ContraseniaNuevaEntry.Text = string.Empty;
            MostrarMensaje(respuesta, false);
        }
        catch (Exception excepcion)
        {
            MostrarMensaje(excepcion.Message, true);
        }
    }

    private void OnCerrarSesionClicked(object? sender, EventArgs e)
    {
        _controlador.CerrarSesion();
        PanelConversor.IsVisible = false;
        PanelInicioSesion.IsVisible = true;
        ResultadoLabel.Text = string.Empty;
        UsuarioEntry.Text = string.Empty;
        MostrarMensaje("Sesión cerrada.", false);
    }

    private void OnTipoChanged(object? sender, EventArgs e)
    {
        var opciones = CatalogoUnidades.Obtener(TipoSeleccionado()).ToList();
        UnidadInicialPicker.ItemsSource = opciones;
        UnidadInicialPicker.ItemDisplayBinding = new Binding(nameof(OpcionUnidad.Etiqueta));
        UnidadFinalPicker.ItemsSource = opciones;
        UnidadFinalPicker.ItemDisplayBinding = new Binding(nameof(OpcionUnidad.Etiqueta));
        UnidadInicialPicker.SelectedIndex = 0;
        UnidadFinalPicker.SelectedIndex = Math.Min(1, opciones.Count - 1);
    }

    private TipoConversion TipoSeleccionado() => TipoPicker.SelectedIndex switch
    {
        1 => TipoConversion.Masa,
        2 => TipoConversion.Temperatura,
        _ => TipoConversion.Longitud
    };

    private void MostrarMensaje(string mensaje, bool esError)
    {
        MensajeLabel.Text = mensaje;
        MensajeLabel.TextColor = esError ? Color.FromArgb("#B42318") : Color.FromArgb("#067647");
        MensajeLabel.BackgroundColor = esError ? Color.FromArgb("#FEF3F2") : Color.FromArgb("#ECFDF3");
        MensajeLabel.IsVisible = true;
    }
}
