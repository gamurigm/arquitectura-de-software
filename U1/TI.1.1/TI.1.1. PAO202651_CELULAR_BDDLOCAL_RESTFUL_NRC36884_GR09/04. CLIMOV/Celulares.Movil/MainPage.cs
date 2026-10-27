using System.Globalization;
using Celulares.Cliente.Controladores;
using Celulares.Cliente.Servicios;
using Celulares.Contratos;
using Microsoft.Maui.Controls.Shapes;

namespace Celulares.Movil;

public sealed class MainPage : ContentPage
{
    private static readonly Color Fondo = Color.FromArgb("#0B1020"), Panel = Color.FromArgb("#121C30"), Texto = Color.FromArgb("#F5F7FB"), Acento = Color.FromArgb("#52E0D2");
    private readonly Entry servidor = Campo("Dirección del servidor", "http://10.0.2.2:19102/");
    private readonly Entry nombre = Campo("Nombre del celular"), precio = Campo("Precio de lista"), cedula = Campo("Cédula"), comprador = Campo("Nombre del comprador"), cuotas = Campo("Cuotas de 3 a 18"), facturaId = Campo("Número de factura");
    private readonly Picker catalogo = new() { Title = "Seleccione un celular", TextColor = Texto, TitleColor = Colors.LightGray, ItemDisplayBinding = new Binding(nameof(Celular.Nombre)) };
    private readonly Picker forma = new() { Title = "Forma de pago", TextColor = Texto, TitleColor = Colors.LightGray, ItemsSource = new[] { "Efectivo", "Crédito directo" }, SelectedIndex = 0 };
    private readonly Label estado = new() { TextColor = Color.FromArgb("#FFCD70"), LineBreakMode = LineBreakMode.WordWrap };
    private readonly Label detalle = new() { TextColor = Texto, LineBreakMode = LineBreakMode.WordWrap };
    private readonly VerticalStackLayout tabla = new() { Spacing = 4 };
    private ControladorTienda? tienda;
    private HttpClient? http;
    private int? editarId;
    private SolicitudFactura? pendiente;

    public MainPage()
    {
        BackgroundColor = Fondo;
        precio.Keyboard = cuotas.Keyboard = facturaId.Keyboard = Keyboard.Numeric;
        servidor.Keyboard = Keyboard.Url;
        var pagina = new VerticalStackLayout { Padding = new Thickness(18, 24), Spacing = 14 };
        pagina.Add(new Label { Text = "Celulares GR09", FontSize = 28, FontAttributes = FontAttributes.Bold, TextColor = Acento });
        pagina.Add(new Label { Text = "Catálogo, ventas y amortización", TextColor = Texto });
        pagina.Add(estado);
        pagina.Add(Seccion("Conexión", Etiqueta("Dirección de la comercializadora"), servidor, Boton("Conectar y actualizar", async () => await Conectar())));
        pagina.Add(Seccion("Catálogo", catalogo, Boton("Actualizar catálogo", async () => await Ejecutar(CargarCatalogo)),
            Boton("Cargar selección para editar", () => { if (catalogo.SelectedItem is Celular c) { editarId = c.Id; nombre.Text = c.Nombre; precio.Text = c.Precio.ToString("F2", CultureInfo.CurrentCulture); } else estado.Text = "Seleccione un celular."; return Task.CompletedTask; }),
            Etiqueta("Nombre"), nombre, Etiqueta("Precio de lista"), precio,
            Boton("Guardar celular", async () => await Ejecutar(GuardarCelular)),
            Boton("Nuevo celular", () => { editarId = null; nombre.Text = precio.Text = ""; return Task.CompletedTask; }),
            Boton("Desactivar selección", async () => await Desactivar())));
        pagina.Add(Seccion("Registrar venta", new Label { Text = "Efectivo: 42% de descuento. Crédito: 3 a 18 cuotas.", TextColor = Texto },
            Etiqueta("Cédula"), cedula, Etiqueta("Nombre del comprador"), comprador, Etiqueta("Forma de pago"), forma,
            Etiqueta("Cuotas para crédito"), cuotas, Boton("Confirmar venta", async () => await Vender()),
            Boton("Nueva venta", async () => await NuevaVenta())));
        pagina.Add(Seccion("Consultar factura y amortización", Etiqueta("Número de factura"), facturaId,
            Boton("Consultar", async () => await Ejecutar(Consultar)), detalle, tabla));
        Content = new ScrollView { Content = pagina };
        Loaded += async (_, _) => await Conectar();
    }

    private async Task Conectar() => await Ejecutar(async () =>
    {
        var direccion = ClienteComercializadora.Direccion(servidor.Text, "http://10.0.2.2:19102/");
        if (pendiente is not null && http?.BaseAddress != direccion)
            throw new InvalidOperationException("Resuelva la venta pendiente antes de cambiar de servidor.");
        http?.Dispose(); http = new HttpClient { BaseAddress = direccion, Timeout = TimeSpan.FromSeconds(30) };
        tienda = new ControladorTienda(new ClienteComercializadora(http));
        await CargarCatalogo();
    });

    private async Task CargarCatalogo()
    {
        var items = await Controlador().Catalogo(); catalogo.ItemsSource = items; estado.Text = $"{items.Length} celulares disponibles.";
    }
    private async Task GuardarCelular()
    {
        var valor = ControladorTienda.InterpretarPrecio(precio.Text);
        var resultado = await Controlador().Guardar(editarId, nombre.Text ?? "", valor);
        editarId = null; nombre.Text = precio.Text = ""; await CargarCatalogo(); estado.Text = $"Celular guardado: {resultado.Nombre}.";
    }
    private async Task Desactivar()
    {
        if (catalogo.SelectedItem is not Celular c) { estado.Text = "Seleccione un celular."; return; }
        if (!await DisplayAlertAsync("Confirmar", $"¿Desactivar {c.Nombre}?", "Sí", "No")) return;
        await Ejecutar(async () => { await Controlador().Eliminar(c.Id); await CargarCatalogo(); estado.Text = "Celular desactivado."; });
    }
    private async Task Vender()
    {
        await Ejecutar(async () =>
        {
            if (catalogo.SelectedItem is not Celular c) throw new ArgumentException("Seleccione un celular.");
            var formaPago = forma.SelectedIndex == 1 ? "CREDITO_DIRECTO" : "EFECTIVO";
            var actual = new SolicitudFactura { Cedula = cedula.Text?.Trim() ?? "", NombreCliente = comprador.Text?.Trim() ?? "", IdCelular = c.Id, FormaPago = formaPago, NumeroCuotas = formaPago == "CREDITO_DIRECTO" ? Entero(cuotas.Text) : null };
            if (pendiente is not null && (pendiente.Cedula != actual.Cedula || pendiente.NombreCliente != actual.NombreCliente || pendiente.IdCelular != actual.IdCelular || pendiente.FormaPago != actual.FormaPago || pendiente.NumeroCuotas != actual.NumeroCuotas))
                throw new InvalidOperationException("Hay una venta pendiente. Reintente con los mismos datos o seleccione Nueva venta.");
            if (!await DisplayAlertAsync("Confirmar venta", $"{c.Nombre} · {forma.SelectedItem}", "Confirmar", "Cancelar")) return;
            pendiente ??= actual;
            var factura = await Controlador().Facturar(pendiente);
            pendiente = null; facturaId.Text = factura.Id.ToString(); await MostrarFactura(factura.Id);
            estado.Text = $"Factura {factura.Id}: {factura.Estado}.";
        });
    }
    private async Task NuevaVenta()
    {
        if (pendiente is not null && !await DisplayAlertAsync("Venta pendiente", "La venta anterior pudo registrarse. Consulte antes de continuar.", "Continuar", "Cancelar")) return;
        pendiente = null; cedula.Text = comprador.Text = cuotas.Text = "";
    }
    private async Task Consultar() => await MostrarFactura(Entero(facturaId.Text));
    private async Task MostrarFactura(int id)
    {
        var factura = await Controlador().Factura(id);
        detalle.Text = $"Factura {factura.Id} · {factura.Estado}\n{factura.NombreCliente} · {factura.Celular}\n{factura.FormaPago} · descuento ${factura.Descuento:F2} · total ${factura.Total:F2}";
        tabla.Children.Clear();
        if (factura.FormaPago == "CREDITO_DIRECTO" && factura.Estado == "APROBADA")
            foreach (var cuota in await Controlador().Amortizacion(id))
                tabla.Add(new Label { Text = $"{cuota.NumeroCuota} | {cuota.FechaPago:yyyy-MM-dd} | cuota ${cuota.ValorCuota:F2} | interés ${cuota.InteresPagado:F2} | capital ${cuota.CapitalPagado:F2} | saldo ${cuota.Saldo:F2}", TextColor = Texto });
    }
    private ControladorTienda Controlador() => tienda ?? throw new InvalidOperationException("Conecte primero con la comercializadora.");
    private async Task Ejecutar(Func<Task> accion) { try { await accion(); } catch (Exception error) { estado.Text = error.Message; } }
    private static int Entero(string? texto) => int.TryParse(texto, out var n) && n > 0 ? n : throw new ArgumentException("Ingrese un número entero positivo.");
    private static Entry Campo(string descripcion, string? valor = null) => new() { Placeholder = descripcion, Text = valor, TextColor = Texto, PlaceholderColor = Colors.LightGray, BackgroundColor = Fondo };
    private static Label Etiqueta(string texto) => new() { Text = texto, TextColor = Texto };
    private static Button Boton(string texto, Func<Task> accion)
    {
        var boton = new Button { Text = texto, BackgroundColor = Acento, TextColor = Fondo };
        boton.Clicked += async (_, _) => { boton.IsEnabled = false; try { await accion(); } finally { boton.IsEnabled = true; } };
        return boton;
    }
    private static View Seccion(string titulo, params View[] elementos)
    {
        var contenido = new VerticalStackLayout { Spacing = 7 };
        contenido.Add(new Label { Text = titulo, TextColor = Acento, FontAttributes = FontAttributes.Bold, FontSize = 20 });
        foreach (var elemento in elementos) contenido.Add(elemento);
        return new Border { BackgroundColor = Panel, Stroke = new SolidColorBrush(Color.FromArgb("#26354D")), StrokeShape = new RoundRectangle { CornerRadius = 14 }, Padding = 16, Content = contenido };
    }
}
