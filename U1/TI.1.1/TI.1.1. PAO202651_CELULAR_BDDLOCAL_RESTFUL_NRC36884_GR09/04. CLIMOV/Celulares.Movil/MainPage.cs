using System.Globalization;
using System.IO;
using Celulares.Cliente.Controladores;
using Celulares.Cliente.Servicios;
using Celulares.Contratos;
using Microsoft.Maui.Controls.Shapes;

namespace Celulares.Movil;

public sealed class MainPage : ContentPage
{
    private static readonly Color Fondo = Color.FromArgb("#080D18"), Texto = Color.FromArgb("#F5F7FB"), Secundario = Color.FromArgb("#C6D0E0"), Acento = Color.FromArgb("#52E0D2");
    private readonly Image foto = new() { HeightRequest = 190, Aspect = Aspect.AspectFit };
    private readonly Label fotoEstado = new() { Text = "Seleccione un celular para administrar su foto.", LineBreakMode = LineBreakMode.WordWrap };
    private Button? botonTema;
    private Button? botonQuitarFoto;
    private bool temaClaro;
    private byte[]? fotoPendiente;
    private string? nombreFotoPendiente;
    private readonly Entry servidor = Campo("Dirección de la comercializadora", "http://10.0.2.2:19102/");
    private readonly Entry nombre = Campo("Nombre del celular"), precio = Campo("Precio de lista"), cedula = Campo("Cédula"), comprador = Campo("Nombre del comprador"), cuotas = Campo("Cuotas de 3 a 18"), cedulaBusqueda = Campo("Cédula del comprador");
    private readonly Picker catalogo = new() { Title = "Seleccione un celular", TextColor = Texto, TitleColor = Secundario, ItemDisplayBinding = new Binding(nameof(Celular.Nombre)) };
    private readonly Picker celularVenta = new() { Title = "Seleccione un celular", TextColor = Texto, TitleColor = Secundario, ItemDisplayBinding = new Binding(nameof(Celular.Nombre)) };
    private readonly Picker forma = new() { Title = "Forma de pago", TextColor = Texto, TitleColor = Secundario, ItemsSource = new[] { "Efectivo", "Crédito directo" }, SelectedIndex = 0 };
    private readonly Picker facturas = new() { Title = "Seleccione una factura", TextColor = Texto, TitleColor = Secundario, ItemDisplayBinding = new Binding(nameof(Factura.Etiqueta)) };
    private readonly Label estado = new() { TextColor = Color.FromArgb("#FFCD70"), LineBreakMode = LineBreakMode.WordWrap };
    private readonly Label elegibilidad = new() { TextColor = Acento, LineBreakMode = LineBreakMode.WordWrap };
    private readonly Label precioCatalogo = new() { TextColor = Acento, LineBreakMode = LineBreakMode.WordWrap };
    private readonly Label precioVenta = new() { TextColor = Acento, LineBreakMode = LineBreakMode.WordWrap };
    private readonly Label detalle = new() { TextColor = Texto, LineBreakMode = LineBreakMode.WordWrap };
    private readonly VerticalStackLayout tabla = new() { Spacing = 6 };
    private ControladorTienda? tienda;
    private HttpClient? http;
    private int? editarId;
    private SolicitudFactura? pendiente;

    public MainPage()
    {
        AplicarTema(false);
        SetDynamicResource(Page.BackgroundColorProperty, "Fondo");
        fotoEstado.SetDynamicResource(Label.TextColorProperty, "Secundario");
        estado.SetDynamicResource(Label.TextColorProperty, "Aviso");
        elegibilidad.SetDynamicResource(Label.TextColorProperty, "Acento");
        precioCatalogo.SetDynamicResource(Label.TextColorProperty, "Acento");
        precioVenta.SetDynamicResource(Label.TextColorProperty, "Acento");
        detalle.SetDynamicResource(Label.TextColorProperty, "Texto");
        foto.SetDynamicResource(VisualElement.BackgroundColorProperty, "Campo");
        foreach (var selector in new[] { catalogo, celularVenta, forma, facturas })
        {
            selector.SetDynamicResource(Picker.TextColorProperty, "Texto");
            selector.SetDynamicResource(Picker.TitleColorProperty, "Secundario");
        }
        precio.Keyboard = cuotas.Keyboard = Keyboard.Numeric;
        servidor.Keyboard = Keyboard.Url;
        catalogo.SelectedIndexChanged += (_, _) => ActualizarPrecioVenta();
        celularVenta.SelectedIndexChanged += (_, _) => ActualizarPrecioVenta();
        forma.SelectedIndexChanged += (_, _) => ActualizarPrecioVenta();

        var pagina = new VerticalStackLayout { Padding = new Thickness(18, 28), Spacing = 14 };
        pagina.Add(ColorDinamico(new Label { Text = "Celulares", FontSize = 30, FontAttributes = FontAttributes.Bold }, "Acento"));
        pagina.Add(ColorDinamico(new Label { Text = "Comercializadora GR09 · BanQuito" }, "Secundario"));
        botonTema = Boton("Tema claro", CambiarTema);
        pagina.Add(botonTema);
        pagina.Add(Seccion("Conexión", Etiqueta("Dirección de la comercializadora"), servidor,
            Boton("Conectar y actualizar", async () => await Conectar())));
        pagina.Add(estado);
        botonQuitarFoto = Boton("Quitar foto", QuitarFoto);
        botonQuitarFoto.IsEnabled = false;
        pagina.Add(Seccion("Catálogo", catalogo, precioCatalogo, foto, fotoEstado,
            Boton("Cargar o reemplazar foto", ElegirFoto), botonQuitarFoto,
            Boton("Actualizar catálogo", async () => await Ejecutar(async () => await CargarCatalogo())),
            Boton("Cargar selección para editar", CargarSeleccion),
            Etiqueta("Nombre"), nombre, Etiqueta("Precio de lista · USD"), precio,
            Boton("Guardar celular", async () => await Ejecutar(GuardarCelular)),
            Boton("Nuevo celular", NuevoCelular),
            Boton("Desactivar selección", async () => await Desactivar())));
        pagina.Add(Seccion("Nueva venta", ColorDinamico(new Label { Text = "Efectivo: descuento 42% · Crédito: tasa anual 16,5%, de 3 a 18 cuotas." }, "Secundario"),
            Etiqueta("Cédula"), cedula, Boton("Consultar elegibilidad", async () => await ConsultarElegibilidad()), elegibilidad,
            Etiqueta("Nombre del comprador"), comprador, Etiqueta("Seleccione celular"), celularVenta,
            precioVenta, Etiqueta("Forma de pago"), forma, Etiqueta("Cuotas para crédito"), cuotas,
            Boton("Confirmar venta", async () => await Vender()), Boton("Reintentar venta", async () => await Reintentar()),
            Boton("Nueva venta", async () => await NuevaVenta())));
        pagina.Add(Seccion("Facturas y amortización", Etiqueta("Buscar por cédula"), cedulaBusqueda,
            Boton("Buscar facturas", async () => await BuscarFacturas()), facturas,
            Boton("Abrir factura seleccionada", async () => await AbrirFactura()), detalle, tabla));
        Content = new ScrollView { Content = pagina };
        Loaded += async (_, _) => await Conectar();
    }

    private async Task Conectar() => await Ejecutar(async () =>
    {
        var direccion = ClienteComercializadora.Direccion(servidor.Text, "http://10.0.2.2:19102/");
        if (pendiente is not null && http?.BaseAddress != direccion)
            throw new InvalidOperationException("Resuelva la venta pendiente antes de cambiar de servidor.");
        http?.Dispose();
        http = new HttpClient { BaseAddress = direccion, Timeout = TimeSpan.FromSeconds(30) };
        tienda = new ControladorTienda(new ClienteComercializadora(http));
        await CargarCatalogo();
    });

    private async Task CargarCatalogo(int? seleccionarId = null)
    {
        var items = await Controlador().Catalogo();
        catalogo.ItemsSource = items;
        celularVenta.ItemsSource = items;
        catalogo.SelectedItem = items.FirstOrDefault(c => c.Id == seleccionarId);
        estado.Text = items.Length == 0 ? "Catálogo vacío. Registre un celular." : $"Conectado · {items.Length} celulares disponibles.";
        ActualizarPrecioVenta();
    }

    private async Task GuardarCelular()
    {
        var valor = ControladorTienda.InterpretarPrecio(precio.Text);
        var resultado = await Controlador().Guardar(editarId, nombre.Text ?? "", valor);
        if (fotoPendiente is not null)
        {
            using var flujo = new MemoryStream(fotoPendiente);
            await Controlador().SubirImagen(resultado.Id, flujo, nombreFotoPendiente ?? "foto.jpg", TipoImagen(nombreFotoPendiente ?? "foto.jpg"));
            fotoPendiente = null; nombreFotoPendiente = null;
        }
        editarId = null;
        nombre.Text = precio.Text = "";
        await CargarCatalogo(resultado.Id);
        estado.Text = $"Celular guardado · {resultado.Nombre} · {resultado.Precio:C2}.";
    }

    private async Task CargarSeleccion()
    {
        if (catalogo.SelectedItem is not Celular celular) { estado.Text = "Seleccione un celular del catálogo."; return; }
        editarId = celular.Id;
        nombre.Text = celular.Nombre;
        precio.Text = celular.Precio.ToString("F2", CultureInfo.CurrentCulture);
        estado.Text = $"Editando {celular.Nombre}.";
        await CargarFotoExistente(celular);
    }

    private Task NuevoCelular()
    {
        editarId = null;
        fotoPendiente = null; nombreFotoPendiente = null; foto.Source = null;
        botonQuitarFoto!.IsEnabled = false;
        nombre.Text = precio.Text = "";
        fotoEstado.Text = "Seleccione una foto para cargarla con el nuevo celular.";
        estado.Text = "Nuevo celular. Complete nombre y precio de lista.";
        return Task.CompletedTask;
    }

    private async Task ElegirFoto()
    {
        var archivo = await FilePicker.Default.PickAsync();
        if (archivo is null) return;
        await Ejecutar(async () =>
        {
            var tipo = TipoImagen(archivo.FileName);
            var datos = await LeerImagen(archivo);
            if (catalogo.SelectedItem is Celular celular)
            {
                using var flujo = new MemoryStream(datos);
                await Controlador().SubirImagen(celular.Id, flujo, archivo.FileName, tipo);
                await CargarCatalogo(celular.Id);
                foto.Source = ImageSource.FromStream(() => new MemoryStream(datos));
                fotoEstado.Text = "Foto guardada o reemplazada en SQL Server.";
                botonQuitarFoto!.IsEnabled = true;
            }
            else
            {
                fotoPendiente = datos; nombreFotoPendiente = archivo.FileName;
                foto.Source = ImageSource.FromStream(() => new MemoryStream(datos));
                fotoEstado.Text = "Vista previa. Guarde el celular para subir la foto.";
                botonQuitarFoto!.IsEnabled = true;
            }
        });
    }

    private async Task QuitarFoto()
    {
        if (fotoPendiente is not null && catalogo.SelectedItem is not Celular)
        {
            fotoPendiente = null; nombreFotoPendiente = null; foto.Source = null;
            fotoEstado.Text = "Foto pendiente descartada.";
            botonQuitarFoto!.IsEnabled = false; return;
        }
        if (catalogo.SelectedItem is not Celular celular || celular.ImagenUrl is null)
        {
            estado.Text = "Seleccione un celular con foto."; return;
        }
        if (!await DisplayAlertAsync("Quitar foto", $"¿Eliminar la foto de {celular.Nombre}?", "Quitar", "Cancelar")) return;
        await Ejecutar(async () =>
        {
            await Controlador().QuitarImagen(celular.Id);
            foto.Source = null; fotoEstado.Text = "Foto eliminada.";
            botonQuitarFoto!.IsEnabled = false;
            await CargarCatalogo(celular.Id);
            estado.Text = "Foto eliminada.";
        });
    }

    private async Task CargarFotoExistente(Celular celular)
    {
        foto.Source = null;
        if (celular.ImagenUrl is null) { fotoEstado.Text = "Este celular aún no tiene foto."; botonQuitarFoto!.IsEnabled = false; return; }
        botonQuitarFoto!.IsEnabled = true;
        try
        {
            var datos = await Controlador().DescargarImagen(celular);
            foto.Source = ImageSource.FromStream(() => new MemoryStream(datos));
            fotoEstado.Text = "Foto guardada en SQL Server.";
        }
        catch (Exception error) { fotoEstado.Text = "No se pudo cargar la foto: " + error.Message; }
    }

    private static async Task<byte[]> LeerImagen(FileResult archivo)
    {
        await using var origen = await archivo.OpenReadAsync();
        using var datos = new MemoryStream();
        var buffer = new byte[81920];
        int leidos;
        while ((leidos = await origen.ReadAsync(buffer)) > 0)
        {
            if (datos.Length + leidos > 5 * 1024 * 1024) throw new ArgumentException("La foto debe pesar hasta 5 MB.");
            await datos.WriteAsync(buffer.AsMemory(0, leidos));
        }
        if (datos.Length == 0) throw new ArgumentException("El archivo seleccionado está vacío.");
        return datos.ToArray();
    }

    private static string TipoImagen(string ruta) => System.IO.Path.GetExtension(ruta).ToLowerInvariant() switch
    {
        ".jpg" or ".jpeg" => "image/jpeg",
        ".png" => "image/png",
        ".webp" => "image/webp",
        _ => throw new ArgumentException("Use una foto JPEG, PNG o WebP.")
    };

    private Task CambiarTema()
    {
        temaClaro = !temaClaro;
        AplicarTema(temaClaro);
        if (botonTema is not null) botonTema.Text = temaClaro ? "Tema oscuro" : "Tema claro";
        return Task.CompletedTask;
    }

    private void AplicarTema(bool claro)
    {
        var tonos = claro
            ? new Dictionary<string, Color> { ["Fondo"] = Color.FromArgb("#F3F6FA"), ["Panel"] = Color.FromArgb("#F2FFFFFF"), ["Campo"] = Color.FromArgb("#FFFFFF"), ["Texto"] = Color.FromArgb("#1B293A"), ["Secundario"] = Color.FromArgb("#52657A"), ["Acento"] = Color.FromArgb("#087F78"), ["Aviso"] = Color.FromArgb("#76520A"), ["TextoBoton"] = Color.FromArgb("#FFFFFF"), ["Borde"] = Color.FromArgb("#AAB9CA") }
            : new Dictionary<string, Color> { ["Fondo"] = Color.FromArgb("#080D18"), ["Panel"] = Color.FromArgb("#E6121C30"), ["Campo"] = Color.FromArgb("#111B2D"), ["Texto"] = Color.FromArgb("#F5F7FB"), ["Secundario"] = Color.FromArgb("#C6D0E0"), ["Acento"] = Color.FromArgb("#52E0D2"), ["Aviso"] = Color.FromArgb("#FFCD70"), ["TextoBoton"] = Color.FromArgb("#09111F"), ["Borde"] = Color.FromArgb("#52627A") };
        foreach (var (nombre, color) in tonos) Resources[nombre] = color;
        Resources["BordeBrush"] = new SolidColorBrush((Color)Resources["Borde"]);
    }

    private static Label ColorDinamico(Label etiqueta, string recurso)
    {
        etiqueta.SetDynamicResource(Label.TextColorProperty, recurso);
        return etiqueta;
    }
    private async Task Desactivar()
    {
        if (catalogo.SelectedItem is not Celular celular) { estado.Text = "Seleccione un celular del catálogo."; return; }
        if (!await DisplayAlertAsync("Desactivar celular", $"¿Desactivar {celular.Nombre}?", "Desactivar", "Cancelar")) return;
        await Ejecutar(async () => { await Controlador().Eliminar(celular.Id); await CargarCatalogo(); estado.Text = "Celular desactivado."; });
    }

    private void ActualizarPrecioVenta()
    {
        precioCatalogo.Text = catalogo.SelectedItem is Celular edicion
            ? $"Edición seleccionada · {edicion.Nombre} · {edicion.Precio:C2}"
            : "Seleccione un celular para editarlo o desactivarlo.";
        if (celularVenta.SelectedItem is not Celular celular) { precioVenta.Text = "Seleccione un celular para ver el precio."; return; }
        if (forma.SelectedIndex == 0)
        {
            var total = ControladorTienda.TotalEfectivo(celular.Precio);
            precioVenta.Text = $"Lista {celular.Precio:C2} · descuento 42% · total {total:C2}";
        }
        else precioVenta.Text = $"Crédito {celular.Precio:C2} · 16,5% anual · 3 a 18 cuotas";
    }

    private async Task ConsultarElegibilidad() => await Ejecutar(async () =>
    {
        var resultado = await Controlador().EvaluarCredito(cedula.Text ?? "");
        Mostrar(resultado);
    });

    private async Task Vender() => await Ejecutar(async () =>
    {
        if (celularVenta.SelectedItem is not Celular celular) throw new ArgumentException("Seleccione un celular del catálogo.");
        var pago = forma.SelectedIndex == 1 ? "CREDITO_DIRECTO" : "EFECTIVO";
        var reintento = pendiente is not null;
        var actual = new SolicitudFactura
        {
            Cedula = cedula.Text?.Trim() ?? "", NombreCliente = comprador.Text?.Trim() ?? "",
            IdCelular = celular.Id, FormaPago = pago,
            NumeroCuotas = pago == "CREDITO_DIRECTO" ? Entero(cuotas.Text) : null,
            PrecioEsperado = celular.Precio
        };
        if (pendiente is not null && !MismaVenta(pendiente, actual))
            throw new InvalidOperationException("Hay una venta pendiente. Reintente la misma operación antes de cambiar los datos.");
        if (pago == "CREDITO_DIRECTO" && !reintento)
        {
            var evaluacion = await Controlador().EvaluarCredito(actual.Cedula);
            Mostrar(evaluacion);
            if (!evaluacion.SujetoCredito || celular.Precio > evaluacion.MontoMaximo)
                throw new InvalidOperationException("Crédito no elegible o precio superior al máximo de BanQuito.");
        }

        var total = pago == "EFECTIVO" ? ControladorTienda.TotalEfectivo(celular.Precio) : celular.Precio;
        if (!await DisplayAlertAsync("Confirmar venta", $"{celular.Nombre}\n{pago}\nTotal {total:C2}", "Confirmar", "Cancelar")) return;
        pendiente ??= actual;
        await EnviarPendiente();
    });

    private async Task Reintentar()
    {
        if (pendiente is null) { estado.Text = "No hay una venta pendiente en esta sesión."; return; }
        await Ejecutar(EnviarPendiente);
    }

    private async Task NuevaVenta()
    {
        if (pendiente is not null && !await DisplayAlertAsync("Venta pendiente", "La venta pudo registrarse. Reintente o consulte facturas antes de descartarla. ¿Continuar?", "Descartar", "Cancelar")) return;
        pendiente = null;
        cedula.Text = comprador.Text = cuotas.Text = "";
        celularVenta.SelectedItem = null;
        elegibilidad.Text = "";
        estado.Text = "Nueva venta lista.";
    }

    private async Task BuscarFacturas() => await Ejecutar(async () =>
    {
        var items = await Controlador().FacturasPorCedula(cedulaBusqueda.Text ?? "");
        facturas.ItemsSource = items;
        facturas.SelectedItem = null;
        estado.Text = items.Length == 0 ? "No hay facturas para esa cédula." : $"{items.Length} facturas encontradas. Seleccione una.";
    });

    private async Task AbrirFactura()
    {
        if (facturas.SelectedItem is not Factura factura) { estado.Text = "Busque y seleccione una factura."; return; }
        await Ejecutar(() => MostrarFactura(factura));
    }

    private async Task EnviarPendiente()
    {
        var factura = await Controlador().Facturar(pendiente!);
        pendiente = null;
        await MostrarFactura(factura);
        estado.Text = $"Venta registrada · factura {factura.Id} · {factura.Estado} · {factura.Total:C2}.";
    }

    private async Task MostrarFactura(Factura factura)
    {
        detalle.Text = $"FACTURA {factura.Id} · {factura.Estado}\n{factura.NombreCliente} · {factura.Cedula} · {factura.Celular}\n{factura.FormaPago} · precio {factura.Precio:C2} · descuento {factura.Descuento:C2} · total {factura.Total:C2}";
        tabla.Children.Clear();
        if (factura.FormaPago != "CREDITO_DIRECTO" || factura.Estado != "APROBADA") return;
        try
        {
            foreach (var cuota in await Controlador().Amortizacion(factura.Id))
            {
                var fila = new Border
                {
                    StrokeShape = new RoundRectangle { CornerRadius = 10 }, Padding = 10,
                    Content = ColorDinamico(new Label { Text = $"Cuota {cuota.NumeroCuota} · {cuota.FechaPago:yyyy-MM-dd}\n{cuota.ValorCuota:C2} · interés {cuota.InteresPagado:C2} · capital {cuota.CapitalPagado:C2} · saldo {cuota.Saldo:C2}" }, "Texto")
                };
                fila.SetDynamicResource(Border.BackgroundColorProperty, "Panel");
                fila.SetDynamicResource(Border.StrokeProperty, "BordeBrush");
                tabla.Add(fila);
            }
        }
        catch (Exception error) { estado.Text = $"Factura {factura.Id} aprobada; no se pudo cargar la amortización: {error.Message}"; }
    }

    private void Mostrar(EvaluacionCredito resultado)
    {
        elegibilidad.Text = resultado.SujetoCredito
            ? $"BanQuito: elegible · máximo {resultado.MontoMaximo:C2}. La venta vuelve a validar el crédito."
            : $"BanQuito: no elegible · {resultado.Motivo}";
    }

    private ControladorTienda Controlador() => tienda ?? throw new InvalidOperationException("Conecte primero con la comercializadora.");
    private async Task Ejecutar(Func<Task> accion) { try { await accion(); } catch (Exception error) { estado.Text = error.Message; } }
    private static bool MismaVenta(SolicitudFactura a, SolicitudFactura b) =>
        a.Cedula == b.Cedula && a.NombreCliente == b.NombreCliente && a.IdCelular == b.IdCelular &&
        a.FormaPago == b.FormaPago && a.NumeroCuotas == b.NumeroCuotas && a.PrecioEsperado == b.PrecioEsperado;
    private static int Entero(string? texto) => int.TryParse(texto, out var numero) && numero is >= 3 and <= 18
        ? numero : throw new ArgumentException("Ingrese entre 3 y 18 cuotas.");
    private static Entry Campo(string descripcion, string? valor = null)
    {
        var entrada = new Entry { Placeholder = descripcion, Text = valor, ClearButtonVisibility = ClearButtonVisibility.WhileEditing };
        entrada.SetDynamicResource(Entry.TextColorProperty, "Texto");
        entrada.SetDynamicResource(Entry.PlaceholderColorProperty, "Secundario");
        entrada.SetDynamicResource(VisualElement.BackgroundColorProperty, "Campo");
        return entrada;
    }
    private static Label Etiqueta(string texto) => ColorDinamico(new Label { Text = texto, FontSize = 13 }, "Secundario");
    private static Button Boton(string texto, Func<Task> accion)
    {
        var boton = new Button { Text = texto, CornerRadius = 14, FontAttributes = FontAttributes.Bold, MinimumHeightRequest = 48 };
        boton.SetDynamicResource(VisualElement.BackgroundColorProperty, "Acento");
        boton.SetDynamicResource(Button.TextColorProperty, "TextoBoton");
        boton.Clicked += async (_, _) => { boton.IsEnabled = false; try { await accion(); } finally { boton.IsEnabled = true; } };
        return boton;
    }
    private static View Seccion(string titulo, params View[] elementos)
    {
        var contenido = new VerticalStackLayout { Spacing = 9 };
        contenido.Add(ColorDinamico(new Label { Text = titulo, FontAttributes = FontAttributes.Bold, FontSize = 19 }, "Acento"));
        foreach (var elemento in elementos) contenido.Add(elemento);
        var panel = new Border
        {
            StrokeShape = new RoundRectangle { CornerRadius = 18 }, Padding = 16, Content = contenido
        };
        panel.SetDynamicResource(Border.BackgroundColorProperty, "Panel");
        panel.SetDynamicResource(Border.StrokeProperty, "BordeBrush");
        return panel;
    }
}
