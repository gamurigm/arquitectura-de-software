# Comercializadora de celulares GR09

Proyecto REST de la tienda de celulares. La tienda es propietaria del catálogo, las facturas y la base `ComercializadoraCelulares`.

## Límite con el banco

El crédito pertenece al proyecto bancario independiente `U1/TG.1/1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09`. Allí viven la API BanQuito, las reglas financieras, sus pruebas y la base `BanQuitoCore`. La tienda llama al banco por HTTP; no referencia sus ensamblados ni inicializa su base.

- API de tienda: `http://127.0.0.1:19102/`.
- API de crédito del banco: `http://127.0.0.1:19101/`.
- El precio en efectivo tiene 42% de descuento. El crédito directo usa entre 3 y 18 cuotas.
- El estado de una factura conserva el identificador de crédito como referencia remota, sin clave foránea entre bases.

## Estado

API y cuatro clientes implementados. Bases, ventas y amortización se verificaron en SQL Server; la app móvil se instaló y abrió en el emulador. Consulta `05. DOCUMENTACION/AVANCE.md`, `05. DOCUMENTACION/VERIFICACION_EXAMEN.md` y `05. DOCUMENTACION/ManualTecnico.md`.

## Organización

- `01. UML`: entregables de la tienda.
- `02. MER`: modelo de catálogo y facturación.
- `03. BDD`: scripts exclusivos de `ComercializadoraCelulares`.
- `04. SERVIDOR`: contratos de tienda, regla de facturación, inicializador y API de comercializadora.
- `04. CLICON`, `04. CLIESC`, `04. CLIMOV`, `04. CLIWEB`: clientes implementados.
- `05. DOCUMENTACION`: plan, avance, arquitectura y manual técnico.

El proyecto de celulares no contiene la API, reglas ni scripts bancarios.
