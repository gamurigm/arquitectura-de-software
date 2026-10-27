# Evidencias de requisitos del examen

Verificación local: 26 de octubre de 2026. Ejecutada contra SQL Server Express `SQLEXPRESS`.

1. Diagrama de casos de uso: [BanQuito](<../../../TG.1/1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09/01. UML/CasosUsoCreditoBanQuito.md>), escrito en Mermaid.
2. Descripción del caso de uso de elegibilidad: sección “Verificar sujeto de crédito” del mismo documento.
3. Datos centrales: `BanQuitoCore` devuelve 5 clientes, 5 cuentas y 50 movimientos. Incluye `CREDITO` y `AMORTIZACION`.
4. Modelo ER de la tienda: [ModeloDatos.md](../02. MER/ModeloDatos.md).
5. Elegibilidad por cédula: `GET /api/credito/elegibilidad?cedula=0506070809` respondió sujeto de crédito para cliente 5.
6. Monto máximo por código: `GET /api/credito/clientes/5/monto-maximo` devolvió `$2.310,00`.
7. Crédito y amortización: una venta directa quedó aprobada; repetir su `IdOperacion` devolvió la misma factura y crédito. La consulta devolvió tres cuotas y saldo final `$0,00`.
8. Catálogo: alta, edición y desactivación vía API confirmadas. El registro temporal se limpió después de verificar el estado inactivo.
9. Facturación: API confirmó venta en efectivo por `$261,00` y venta directa aprobada por `$450,00`. Consola, WPF, MAUI y MVC compilaron; la web respondió HTTP 200.
10. Servicio de amortización: `GET /api/facturas/{id}/amortizacion` devolvió las cuotas del crédito aprobado. Los cuatro clientes llaman al mismo servicio.
11. Consulta desde la tienda: el formulario MVC permite ingresar el número de factura y presenta la amortización. Las vistas de consola, WPF y MAUI también consultan por factura.
12. Arquitectura: diagrama Mermaid en [Arquitectura.md](Arquitectura.md).

Las nueve pruebas automatizadas de tienda pasaron. La app MAUI se compiló, instaló y abrió en el emulador `GR10_Pixel_35`; no se usó un teléfono físico.
