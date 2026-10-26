# Avance del proyecto de celulares al 25 de octubre de 2026

## Separación aplicada

- BanQuito.Api, su dominio y contratos, las reglas de crédito, las pruebas, el inicializador y los scripts BanQuitoCore están en el proyecto bancario REST .NET.
- La comercializadora mantiene catálogo, facturación, sus propios DTOs del cliente HTTP y `ComercializadoraCelulares`.
- No hay `ProjectReference` del celular al banco ni scripts bancarios en `U1/TI.1.1/...CELULAR...`.
- Los dos inicializadores aplicaron sus scripts por separado contra el SQL Server Developer local. Los scripts no borran datos y no recrearon las bases que ya existían.
- La regla del descuento del 42% y su prueba permanecen en el proyecto de la comercializadora; las reglas y pruebas de crédito permanecen en el proyecto bancario.

## Verificación

- `BanQuito.Api` y sus contratos/dominio compilaron: 0 advertencias, 0 errores.
- `BanQuito.Administracion` compiló: 0 advertencias, 0 errores.
- `BanQuito.Pruebas` compiló: 0 advertencias, 0 errores; no se ejecutó la suite.
- `Comercializadora.Api` y sus contratos/dominio compilaron: 0 advertencias, 0 errores.
- El inicializador de la comercializadora compiló: 0 advertencias, 0 errores.
- `Comercializadora.Pruebas` compiló: 0 advertencias, 0 errores; no se ejecutó la suite.
- Los scripts de cada proyecto se aplicaron sin errores. No se iniciaron APIs ni se hicieron llamadas HTTP en esta corrección.
- Consola, WPF, MVC y MAUI Android siguen pendientes.

## Entorno local

SQL Server 2025 Developer está activo en la instancia predeterminada para desarrollo local. SQL Server Express sigue siendo el destino previsto de entrega. Los artefactos y paquetes de ambos proyectos quedan bajo `C:\Users\GABI\Documents\ESPE VIII SII2026\tools`.
