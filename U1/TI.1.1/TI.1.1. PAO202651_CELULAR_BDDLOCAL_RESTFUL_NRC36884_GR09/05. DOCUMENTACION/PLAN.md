# Plan de implementación CELULAR BDDLOCAL RESTFUL GR09

Fuente de requisitos: `ExamenComplexivoPractivo_CELULARES.docx` del repositorio. Referencia de consulta: GR01 de Andrango, Calapaqui y Quilumbaquin. No se modifica ni copia ese proyecto.

## Alcance y límites

Este directorio implementa la comercializadora de celulares: catálogo, facturas, API REST y clientes. El proyecto bancario independiente es `U1/TG.1/1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09`; allí viven BanQuito.Api, el dominio de crédito y `BanQuitoCore`. La comercializadora consume el contrato bancario por HTTP y no agrega referencias de proyecto ni scripts bancarios.

## Plataforma

.NET 10, C# 14, ASP.NET Core REST y SQL Server 2025. La API de la tienda usa el puerto 19102. La API BanQuito del proyecto bancario usa el puerto 19101. Consola, WPF, MAUI Android y web MVC (19103) consumen la API de la comercializadora.

`ComercializadoraCelulares` pertenece a este proyecto. `BanQuitoCore` pertenece al proyecto del banco. Para desarrollo local se usa SQL Server 2025 Developer en la instancia predeterminada; SQLEXPRESS se conserva como destino de entrega. Los artefactos .NET y paquetes se guardan bajo `C:\Users\GABI\Documents\ESPE VIII SII2026\tools`.

## Entregables de la comercializadora

1. Mantener la estructura numerada GR09 y separar MVC y transporte en cada cliente.
2. Mantener scripts de catálogo y facturación solo en `03. BDD` de este proyecto.
3. Exponer mantenimiento de catálogo, venta en efectivo y crédito, y consulta de amortización desde la API de tienda.
4. Implementar consola, WPF, MAUI Android y MVC web consumiendo la misma API de tienda.
5. Documentar los datos propios de la comercializadora y sus referencias remotas al banco.

## Contrato bancario consumido

BanQuito.Api, en el proyecto bancario, expone:

- `GET /api/credito/elegibilidad?cedula=...`
- `GET /api/credito/clientes/{codigoCliente}/monto-maximo`
- `POST /api/creditos` con `codigoCliente`, `precioTelefono`, `numeroCuotas` y `referenciaVenta`.
- `GET /api/creditos/{id}/amortizacion`

La API de tienda expone `GET/POST /api/celulares`, `PUT/DELETE /api/celulares/{id}`, `POST /api/facturas` y `GET /api/facturas/{id}/amortizacion`. El cliente HTTP de la tienda mantiene DTOs propios; no comparte ensamblados con el banco.

## Reglas del examen que atraviesan la integración

- El banco requiere cliente existente, depósito en el último mes y ausencia de crédito activo.
- Si el género es masculino, debe tener al menos 25 años cumplidos.
- Ventanas móviles inclusivas de uno y tres meses, relativas a la fecha de evaluación.
- Promedios por valor de movimiento DEP y RET en los últimos tres meses.
- Máximo = max(0, (promedioDepósitos - promedioRetiros) * 0.35 * 6).
- El porcentaje elegido fue 35%; el texto del examen contradice su fórmula impresa del 30%.
- Crédito: precio positivo no superior al máximo; plazo entero entre 3 y 18 meses; tasa anual 16,5%, cuota fija y ajuste final a saldo cero.
- Efectivo: descuento del 42% sobre precio de lista.
- Una factura corresponde a un celular. No hay inventario.
- Un rechazo o indisponibilidad del banco no confirma una venta a crédito.

## Publicación

La autorización de commit y push registrada para el 23 de octubre de 2026 expiró. No hay autorización vigente para publicar cambios. Cuando se solicite publicación, limitarla a los proyectos GR09 modificados y excluir GR01 y el documento original sin seguimiento. Identificar el avance incompleto como tal.
