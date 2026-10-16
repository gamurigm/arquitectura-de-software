using System.Globalization;
using ClienteMovilGR09.Modelos;
using ClienteMovilGR09.Controladores;

namespace ClienteMovilGR09.Vistas;

public partial class PaginaPrincipal : ContentPage
{
    private readonly ControladorConversor _controlador = new();
    private bool _ocupado;

    public PaginaPrincipal()
    {
        InitializeComponent();
        TipoPicker.SelectedIndex = 0;
    }

    private async void AlPulsarIniciarSesion(object? sender, EventArgs e)
    {
        if (_ocupado) return;
        if (string.IsNullOrWhiteSpace(UsuarioEntry.Text) || string.IsNullOrWhiteSpace(ContraseniaEntry.Text))
        {
            MostrarMensaje("Ingresa usuario y contraseña.", true);
            return;
        }

        try
        {
            EstablecerOcupado(true, BotonIngresar, "Conectando…");
            ContraseniaEntry.Unfocus();
            await _controlador.IniciarSesionAsync(UsuarioEntry.Text.Trim(), ContraseniaEntry.Text);
            ContraseniaEntry.Text = string.Empty;
            PanelInicioSesion.IsVisible = false;
            TarjetaInicioSesion.IsVisible = false;
            PanelConversor.IsVisible = true;
            Presentacion.IsVisible = false;
            VerContrasenia.IsChecked = false;
            ResumenSesion.Text = $"Hola, {UsuarioEntry.Text.Trim()}. Elige qué convertir.";
            MostrarMensaje("Sesión iniciada.", false);
            await Desplazamiento.ScrollToAsync(0, 0, false);
        }
        catch (Exception excepcion)
        {
            MostrarMensaje(excepcion.Message, true);
        }
        finally
        {
            EstablecerOcupado(false, BotonIngresar, "Entrar al conversor");
        }
    }

    private async void AlPulsarConvertir(object? sender, EventArgs e)
    {
        if (_ocupado || !PanelConversor.IsVisible) return;
        AlCambiarEntrada(sender, e);
        if (!double.TryParse(ValorEntry.Text, NumberStyles.Float, CultureInfo.CurrentCulture, out var valor)
            || !double.IsFinite(valor))
        {
            MostrarMensaje("Ingresa un número finito válido.", true);
            return;
        }

        var tipo = TipoSeleccionado();
        if (tipo == TipoConversion.Masa && valor < 0)
        {
            MostrarMensaje("La masa no puede ser negativa.", true);
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
            EstablecerOcupado(true, BotonConvertir, "Convirtiendo…");
            ValorEntry.Unfocus();
            var resultado = await _controlador.ConvertirAsync(tipo, valor, inicial.Codigo, final.Codigo);
            if (!double.IsFinite(resultado))
            {
                throw new InvalidOperationException("El resultado excede el rango permitido.");
            }
            ResultadoLabel.Text = $"{resultado.ToString("G", CultureInfo.CurrentCulture)} {final.Etiqueta}";
            MostrarMensaje("Conversión completada.", false);
            await Desplazamiento.ScrollToAsync(TarjetaResultado, ScrollToPosition.MakeVisible, true);
        }
        catch (Exception excepcion)
        {
            MostrarMensaje(excepcion.Message, true);
        }
        finally
        {
            EstablecerOcupado(false, BotonConvertir, "Convertir ahora");
        }
    }

    private async void AlPulsarCambiarContrasenia(object? sender, EventArgs e)
    {
        if (_ocupado) return;
        if (string.IsNullOrWhiteSpace(ContraseniaActualEntry.Text) || string.IsNullOrWhiteSpace(ContraseniaNuevaEntry.Text))
        {
            MostrarMensaje("Ingresa ambas contraseñas.", true);
            return;
        }
        try
        {
            EstablecerOcupado(true, BotonCambiarContrasenia, "Actualizando…");
            var respuesta = await _controlador.CambiarContraseniaAsync(ContraseniaActualEntry.Text, ContraseniaNuevaEntry.Text);
            ContraseniaActualEntry.Text = string.Empty;
            ContraseniaNuevaEntry.Text = string.Empty;
            PanelContrasenia.IsVisible = false;
            MostrarMensaje(respuesta, false);
        }
        catch (Exception excepcion)
        {
            MostrarMensaje(excepcion.Message, true);
        }
        finally
        {
            EstablecerOcupado(false, BotonCambiarContrasenia, "Actualizar contraseña");
        }
    }

    private void AlPulsarCerrarSesion(object? sender, EventArgs e)
    {
        _controlador.CerrarSesion();
        PanelConversor.IsVisible = false;
        PanelInicioSesion.IsVisible = true;
        TarjetaInicioSesion.IsVisible = true;
        Presentacion.IsVisible = true;
        PanelContrasenia.IsVisible = false;
        ContraseniaActualEntry.Text = string.Empty;
        ContraseniaNuevaEntry.Text = string.Empty;
        AlCambiarEntrada(sender, e);
        UsuarioEntry.Text = string.Empty;
        MostrarMensaje("Sesión cerrada.", false);
    }

    private void AlCambiarTipo(object? sender, EventArgs e)
    {
        var opciones = CatalogoUnidades.Obtener(TipoSeleccionado()).ToList();
        UnidadInicialPicker.ItemsSource = opciones;
        UnidadInicialPicker.ItemDisplayBinding = new Binding(nameof(OpcionUnidad.Etiqueta));
        UnidadFinalPicker.ItemsSource = opciones;
        UnidadFinalPicker.ItemDisplayBinding = new Binding(nameof(OpcionUnidad.Etiqueta));
        UnidadInicialPicker.SelectedIndex = 0;
        UnidadFinalPicker.SelectedIndex = Math.Min(1, opciones.Count - 1);
    }

    private void AlMostrarContrasenia(object? sender, CheckedChangedEventArgs e) =>
        ContraseniaEntry.IsPassword = !e.Value;

    private void AlAlternarContrasenia(object? sender, EventArgs e) =>
        PanelContrasenia.IsVisible = !PanelContrasenia.IsVisible;

    private void AlIntercambiarUnidades(object? sender, EventArgs e)
    {
        var origen = UnidadInicialPicker.SelectedIndex;
        UnidadInicialPicker.SelectedIndex = UnidadFinalPicker.SelectedIndex;
        UnidadFinalPicker.SelectedIndex = origen;
    }

    private void AlCambiarEntrada(object? sender, EventArgs e)
    {
        ResultadoLabel.Text = "Tu resultado aparecerá aquí";
        MensajeLabel.IsVisible = false;
    }

    private void EstablecerOcupado(bool ocupado, Button boton, string texto)
    {
        _ocupado = ocupado;
        TarjetaInicioSesion.IsEnabled = !ocupado;
        PanelConversor.IsEnabled = !ocupado;
        boton.Text = texto;
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
        MensajeLabel.TextColor = Color.FromArgb(esError ? "#FF8585" : "#52E0D2");
        MensajeLabel.BackgroundColor = Color.FromArgb(esError ? "#302034" : "#132E3A");
        MensajeLabel.IsVisible = true;
    }
}
