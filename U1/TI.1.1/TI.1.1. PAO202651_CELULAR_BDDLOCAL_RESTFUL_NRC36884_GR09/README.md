# Comercializadora de celulares y crédito BanQuito GR09

Proyecto del examen práctico de celulares. Stack acordado: .NET 10, C# 14, REST y SQL Server 2025 Express local.

## Decisiones acordadas

- Alcance completo del examen, con consola, WPF, MAUI Android y ASP.NET Core MVC.
- Crédito máximo: 35% de la diferencia de los promedios de depósitos y retiros, multiplicado por 6.
- Efectivo: descuento del 42%. Crédito: 3 a 18 cuotas, interés anual del 16,5%.
- El proyecto GR01 de Andrango es únicamente referencia de consulta. Sus archivos no se modifican ni se incorporan.
- No se desarrolla el Core bancario completo ni inventario.

## Estado

Implementación en curso. Dominio financiero con 21 pruebas correctas y API bancaria compilada. La integración SQL, la comercializadora y sus cuatro interfaces siguen pendientes.
El detalle de avances, verificaciones y pendientes está en 05. DOCUMENTACION/AVANCE.md.
La estructura creada no debe confundirse con módulos ya implementados.

## Organización

01. UML: requisitos y casos de uso.
02. MER: modelos conceptual, lógico y físico.
03. BDD: scripts SQL Server.
04. SERVIDOR: APIs REST BanQuito y comercializadora.
04. CLICON: consola .NET.
04. CLIESC: WPF.
04. CLIMOV: Android MAUI.
04. CLIWEB: ASP.NET Core MVC.
05. DOCUMENTACION: plan, manual y evidencia.

