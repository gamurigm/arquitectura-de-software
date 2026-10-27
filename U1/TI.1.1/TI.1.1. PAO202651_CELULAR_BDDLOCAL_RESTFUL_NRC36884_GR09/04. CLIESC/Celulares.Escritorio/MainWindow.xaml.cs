using System.Globalization;
using System.Net.Http;
using System.Windows;
using System.Windows.Controls;
using Celulares.Cliente.Controladores;
using Celulares.Cliente.Servicios;
using Celulares.Contratos;

namespace Celulares.Escritorio;

public partial class MainWindow : Window
{
    private readonly ControladorTienda tienda;
    private SolicitudFactura? pendiente;

    public MainWindow()
    {
        InitializeComponent();
        var cliente = new HttpClient { BaseAddress = ClienteComercializadora.Direccion(Environment.GetEnvironmentVariable("CELULARES_API_URL")), Timeout = TimeSpan.FromSeconds(30) };
        tienda = new ControladorTienda(new ClienteComercializadora(cliente));
    }

    private async void AlCargar(object sender, RoutedEventArgs e) => await CargarCatalogo();
    private async void ActualizarCatalogo(object sender, RoutedEventArgs e) => await CargarCatalogo();
    private async Task CargarCatalogo()
    {
        try { var datos = await tienda.Catalogo(); Catalogo.ItemsSource = datos; CelularVenta.ItemsSource = datos; Estado.Text = $"{datos.Length} celulares disponibles."; }
        catch (Exception error) { Estado.Text = "No se pudo cargar el catálogo: " + error.Message; }
    }

    private void SeleccionarCelular(object sender, SelectionChangedEventArgs e)
    {
        if (Catalogo.SelectedItem is Celular c) { NombreCelular.Text = c.Nombre; PrecioCelular.Text = c.Precio.ToString("F2", CultureInfo.CurrentCulture); }
    }
    private void NuevoCelular(object sender, RoutedEventArgs e) { Catalogo.SelectedItem = null; NombreCelular.Clear(); PrecioCelular.Clear(); }

    private async void GuardarCelular(object sender, RoutedEventArgs e)
    {
        try
        {
            var precio = ControladorTienda.InterpretarPrecio(PrecioCelular.Text);
            var id = (Catalogo.SelectedItem as Celular)?.Id;
            var celular = await tienda.Guardar(id, NombreCelular.Text, precio);
            await CargarCatalogo(); Estado.Text = $"Celular guardado: {celular.Nombre}.";
        }
        catch (Exception error) { Estado.Text = error.Message; }
    }

    private async void DesactivarCelular(object sender, RoutedEventArgs e)
    {
        if (Catalogo.SelectedItem is not Celular c) { Estado.Text = "Seleccione un celular."; return; }
        if (MessageBox.Show($"¿Desactivar {c.Nombre}?", "Confirmar", MessageBoxButton.YesNo) != MessageBoxResult.Yes) return;
        try { await tienda.Eliminar(c.Id); await CargarCatalogo(); Estado.Text = "Celular desactivado."; }
        catch (Exception error) { Estado.Text = error.Message; }
    }

    private async void Vender(object sender, RoutedEventArgs e)
    {
        try
        {
            if (CelularVenta.SelectedValue is not int id) throw new ArgumentException("Seleccione un celular.");
            var forma = (FormaPago.SelectedItem as ComboBoxItem)?.Tag?.ToString() ?? "EFECTIVO";
            int? cuotas = forma == "CREDITO_DIRECTO" ? Entero(Cuotas.Text) : null;
            var actual = new SolicitudFactura { Cedula = Cedula.Text.Trim(), NombreCliente = Comprador.Text.Trim(), IdCelular = id, FormaPago = forma, NumeroCuotas = cuotas };
            if (pendiente is not null && (pendiente.Cedula != actual.Cedula || pendiente.NombreCliente != actual.NombreCliente || pendiente.IdCelular != actual.IdCelular || pendiente.FormaPago != actual.FormaPago || pendiente.NumeroCuotas != actual.NumeroCuotas))
                throw new InvalidOperationException("Hay una venta pendiente. Reintente con los mismos datos o seleccione Nueva venta.");
            if (MessageBox.Show("¿Confirmar esta venta?", "Venta", MessageBoxButton.YesNo) != MessageBoxResult.Yes) return;
            pendiente ??= actual;
            var factura = await tienda.Facturar(pendiente);
            pendiente = null; FacturaId.Text = factura.Id.ToString(); await MostrarFactura(factura.Id);
            Estado.Text = $"Factura {factura.Id}: {factura.Estado}.";
        }
        catch (Exception error) { Estado.Text = error.Message; }
    }

    private void NuevaVenta(object sender, RoutedEventArgs e)
    {
        if (pendiente is not null && MessageBox.Show("La venta pendiente pudo haberse registrado. Consulte antes de iniciar otra. ¿Continuar?", "Confirmar", MessageBoxButton.YesNo) != MessageBoxResult.Yes) return;
        pendiente = null; Cedula.Clear(); Comprador.Clear(); Cuotas.Clear(); CelularVenta.SelectedItem = null;
    }
    private async void ConsultarFactura(object sender, RoutedEventArgs e)
    {
        try { await MostrarFactura(Entero(FacturaId.Text)); Estado.Text = "Factura consultada."; }
        catch (Exception error) { Estado.Text = error.Message; }
    }
    private async Task MostrarFactura(int id)
    {
        var factura = await tienda.Factura(id);
        DetalleFactura.Text = $"Factura {factura.Id} | {factura.Estado} | {factura.NombreCliente} | {factura.Celular} | {factura.FormaPago} | descuento ${factura.Descuento:F2} | total ${factura.Total:F2}";
        TablaCuotas.ItemsSource = factura.FormaPago == "CREDITO_DIRECTO" && factura.Estado == "APROBADA" ? await tienda.Amortizacion(id) : [];
    }
    private static int Entero(string valor) => int.TryParse(valor, out var numero) && numero > 0 ? numero : throw new ArgumentException("Ingrese un número entero positivo.");
}
