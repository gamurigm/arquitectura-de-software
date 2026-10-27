# Avance del proyecto de celulares GR09

## Implementación

- API REST de tienda y cuatro clientes: consola, WPF, MAUI Android y MVC web.
- BanQuito mantiene la elegibilidad, el crédito y la amortización en su proyecto REST .NET. La tienda conserva solo el identificador remoto del crédito.
- El modelo ER de tienda está en `02. MER/ModeloDatos.md`; el diagrama de casos de uso y su descripción están en el proyecto BanQuito.
- Arquitectura completa en `Arquitectura.md`.

## Verificación local, 26 de octubre de 2026

- SQL Server Express `SQLEXPRESS`: `BanQuitoCore` tiene 5 clientes, 5 cuentas y 50 movimientos. `ComercializadoraCelulares` tiene 3 celulares activos.
- Pruebas automatizadas de la tienda: 9 de 9 aprobadas.
- Consola y WPF compilaron sin errores ni advertencias. MVC respondió HTTP 200 y mostró el catálogo.
- MAUI compiló con Android SDK 36 en `D:\tools\android-sdk`; se ejecutó en `GR10_Pixel_35`, conectó a la API y cargó 3 celulares.
- API BanQuito devolvió elegibilidad y monto máximo de $2.310 para el cliente 5.
- API de tienda registró venta en efectivo de $261 y venta a crédito aprobada. Reintento conservó la factura y el crédito. Amortización: 3 cuotas, saldo final $0.
- Se ejercitaron alta, edición y desactivación del catálogo. El registro de prueba se retiró después de comprobar la desactivación.

## Entrega

- El emulador se usó para abrir la app móvil; no se probó un teléfono físico.
- Las fuentes Mermaid son editables y se muestran en GitHub. El diagrama y la descripción de casos de uso de crédito están en el proyecto BanQuito.
- El equipo no tiene unidad CD/DVD. El paquete para grabación queda en `D:\tools\CelularGR09\Entrega_Examen_GR09.zip`.
