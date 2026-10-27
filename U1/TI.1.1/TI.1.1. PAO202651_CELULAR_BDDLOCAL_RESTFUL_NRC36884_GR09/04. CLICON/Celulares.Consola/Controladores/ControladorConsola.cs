using Celulares.Cliente.Controladores;
using Celulares.Consola.Vistas;
using Celulares.Contratos;

namespace Celulares.Consola.Controladores;

internal sealed class ControladorConsola(ControladorTienda tienda)
{
    private SolicitudFactura? pendiente;

    public async Task Ejecutar()
    {
        while (true)
        {
            Console.WriteLine("\nCelulares GR09 | 1 Catálogo | 2 Guardar | 3 Desactivar | 4 Venta | 5 Factura | 6 Amortización | 7 Reintentar venta | 8 Descartar pendiente | 0 Salir");
            var opcion = VistaConsola.Texto("Opción");
            if (opcion == "0") return;
            try
            {
                switch (opcion)
                {
                    case "1":
                        foreach (var c in await tienda.Catalogo()) Console.WriteLine($"{c.Id} | {c.Nombre} | ${c.Precio:F2}");
                        break;
                    case "2":
                        var idTexto = VistaConsola.Texto("ID para editar; vacío para crear");
                        int? id = string.IsNullOrWhiteSpace(idTexto) ? null : int.Parse(idTexto);
                        var celular = await tienda.Guardar(id, VistaConsola.Texto("Nombre"), VistaConsola.Dinero("Precio"));
                        Console.WriteLine($"Celular guardado: {celular.Id} - {celular.Nombre}");
                        break;
                    case "3":
                        var eliminarId = VistaConsola.Entero("ID del celular");
                        if (VistaConsola.Confirmar("¿Desactivar el celular?")) await tienda.Eliminar(eliminarId);
                        break;
                    case "4":
                        if (pendiente is not null) throw new InvalidOperationException("Hay una venta pendiente. Reintente con la opción 7 o descarte con la opción 8.");
                        var forma = VistaConsola.Texto("Forma de pago: 1 efectivo, 2 crédito");
                        if (forma is not ("1" or "2")) throw new ArgumentException("Seleccione 1 o 2.");
                        var nueva = new SolicitudFactura
                        {
                            Cedula = VistaConsola.Texto("Cédula"), NombreCliente = VistaConsola.Texto("Nombre"),
                            IdCelular = VistaConsola.Entero("ID del celular"),
                            FormaPago = forma == "1" ? "EFECTIVO" : "CREDITO_DIRECTO",
                            NumeroCuotas = forma == "2" ? VistaConsola.Entero("Cuotas (3 a 18)") : null
                        };
                        if (VistaConsola.Confirmar("¿Confirmar la venta?")) { pendiente = nueva; await EnviarPendiente(); }
                        break;
                    case "5":
                        Mostrar(await tienda.Factura(VistaConsola.Entero("ID de factura")));
                        break;
                    case "6":
                        foreach (var cuota in await tienda.Amortizacion(VistaConsola.Entero("ID de factura")))
                            Console.WriteLine($"{cuota.NumeroCuota} | {cuota.FechaPago:yyyy-MM-dd} | cuota ${cuota.ValorCuota:F2} | interés ${cuota.InteresPagado:F2} | capital ${cuota.CapitalPagado:F2} | saldo ${cuota.Saldo:F2}");
                        break;
                    case "7":
                        if (pendiente is null) Console.WriteLine("No hay una venta pendiente en esta sesión.");
                        else await EnviarPendiente();
                        break;
                    case "8":
                        if (pendiente is not null && VistaConsola.Confirmar("La venta pudo registrarse. Consulte antes de descartarla. ¿Continuar?")) pendiente = null;
                        break;
                    default: Console.WriteLine("Seleccione una opción del menú."); break;
                }
            }
            catch (Exception error) when (error is ArgumentException or FormatException or InvalidOperationException or HttpRequestException or TaskCanceledException)
            {
                Console.WriteLine("No se pudo completar la operación: " + error.Message);
                if (pendiente is not null) Console.WriteLine("Puede reintentar la misma venta con la opción 7.");
            }
        }
    }

    private async Task EnviarPendiente()
    {
        var factura = await tienda.Facturar(pendiente!);
        Mostrar(factura);
        pendiente = null;
    }

    private static void Mostrar(Factura factura) =>
        Console.WriteLine($"Factura {factura.Id} | {factura.Estado} | {factura.Celular} | {factura.FormaPago} | descuento ${factura.Descuento:F2} | total ${factura.Total:F2}");
}
