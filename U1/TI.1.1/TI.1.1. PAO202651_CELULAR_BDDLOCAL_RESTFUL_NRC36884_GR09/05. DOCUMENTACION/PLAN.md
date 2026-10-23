# Plan de implementación CELULAR BDDLOCAL RESTFUL GR09

Fuente de requisitos: ExamenComplexivoPractivo_CELULARES.docx del repositorio.
Referencia de consulta: GR01 de Andrango, Calapaqui y Quilumbaquin. No se modifica ni copia ese proyecto.

## Plataforma

.NET 10 y C# 14. Dos APIs ASP.NET Core REST: BanQuito (19101) y comercializadora (19102).
Clientes: consola, WPF, MAUI Android y web MVC (19103). Todos usan la API comercializadora; solo esta llama al banco.
SQL Server 2025 Express, instancia SQLEXPRESS. Bases BanQuitoCore y ComercializadoraCelulares.
Instaladores, paquetes, temporales y artefactos configurables se guardan en D:.

## Entregables

1. Mantener la estructura numerada GR09 y separar MVC y transporte en cada cliente.
2. Implementar el modelo Core del enunciado: CLIENTE, CUENTA y MOVIMIENTO; añadir CREDITO y AMORTIZACION.
3. Sembrar al menos 5 clientes, 5 cuentas y 50 movimientos. Crear catálogo y facturación en otra base.
4. Publicar elegibilidad por cédula, máximo por código, otorgamiento y consulta de amortización.
5. Implementar mantenimiento de catálogo, venta en efectivo y crédito, y consulta de amortización en los cuatro clientes.
6. Añadir casos de uso, descripción de elegibilidad, ER comercializadora, arquitectura, diccionario y manual.
7. Verificar reglas financieras, APIs, persistencia SQL y ejecución de clientes. Documentar límites reales.

## Contrato propuesto

Banco:
- GET /api/credito/elegibilidad?cedula=...
- GET /api/credito/clientes/{codigoCliente}/monto-maximo
- POST /api/creditos con codigoCliente, precioTelefono y numeroCuotas.
- GET /api/creditos/{id}/amortizacion.

Comercializadora:
- GET/POST /api/celulares; PUT/DELETE /api/celulares/{id}.
- POST /api/facturas con cedula, nombreCliente, idCelular, formaPago y numeroCuotas.
- GET /api/facturas/{id}/amortizacion.

JSON camelCase, dinero decimal y errores HTTP ProblemDetails. Los clientes no conectan directamente a SQL Server.

## Reglas cerradas

- Debe existir el cliente, tener depósito en el último mes y no poseer crédito activo.
- Si género es masculino, debe tener al menos 25 años cumplidos.
- Ventanas móviles inclusivas de uno y tres meses, relativas a la fecha de evaluación.
- Promedios por valor de movimiento DEP y RET en los últimos tres meses.
- Máximo = max(0, (promedioDepósitos - promedioRetiros) * 0.35 * 6).
- El usuario eligió expresamente 35%; el texto del examen contradice su fórmula impresa del 30%.
- El precio debe ser positivo y no superar el máximo. Plazo entero entre 3 y 18 meses.
- Cuota fija mensual, tasa 0.165/12. Ajustar redondeo final para saldo cero.
- Efectivo: descuento del 42% sobre precio de lista.
- Una factura corresponde a un celular. No hay inventario.
- El rechazo o la indisponibilidad del banco no confirma una venta a crédito.

## Escenarios de aceptación

Cliente inexistente; masculino menor de 25; cumpleaños exacto; ausencia de depósitos recientes; crédito activo.
Promedios con movimientos fuera de ventana; sin retiros; diferencia negativa.
Crédito igual al máximo y superior; plazos 2, 3, 18 y 19.
Préstamo 450 a 6 meses: cuota aproximada 78.65 y saldo final 0.00.
Efectivo 100: descuento 42 y total 58.
Persistencia, errores del banco y los mismos flujos en los cuatro clientes.

## Publicación autorizada

El usuario pidió commit y push hoy 23 de octubre de 2026 a las 17:00 America/Guayaquil, incluso si solo hay plan.
Publicar únicamente el nuevo directorio GR09 en origin/main. Excluir GR01 y el documento original sin seguimiento.
El avance incompleto debe identificarse como tal; nunca afirmar que una estructura vacía es una aplicación terminada.

