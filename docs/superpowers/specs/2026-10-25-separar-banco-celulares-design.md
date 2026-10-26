# Separación del banco y la comercializadora

## Intención

Mantener el servicio de crédito y sus datos bajo el proyecto bancario, y dejar el proyecto de celulares como consumidor HTTP independiente. Esto evita que el proyecto celular compile o ejecute reglas bancarias.

## Objetivos

- Alojar BanQuito.Api, su dominio, contratos, inicializador, pruebas y esquema bajo `U1/TG.1/1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09`.
- Mantener en el proyecto celular catálogo, facturación, su base y un cliente REST con DTOs propios.
- Separar los scripts para que cada proyecto inicialice únicamente su base.
- Documentar que las bases BanQuitoCore y ComercializadoraCelulares son propiedad de proyectos distintos y no se enlazan con claves foráneas.

## No objetivos

- Cambiar el contrato financiero ya implementado ni las reglas acordadas.
- Fusionar BanQuitoCore con la base EurekaBank existente: el esquema y los datos requeridos por crédito son diferentes.
- Borrar o recrear las bases locales ya creadas.
- Implementar los clientes de consola, WPF, MVC o MAUI en esta corrección.

## Restricciones

- .NET 10, C# 14, ASP.NET Core REST y SQL Server.
- La comercializadora se comunica con el banco mediante HTTP y no referencia ensamblados bancarios.
- BanQuito.Api mantiene la URL `http://127.0.0.1:19101/`; la comercializadora mantiene `http://127.0.0.1:19102/`.
- Se conservan los cambios locales existentes y no se ejecutan pruebas.

## Enfoques considerados

1. Dejar BanQuito.Api dentro de la carpeta celular: conserva el código, pero incumple la separación indicada.
2. Integrar las tablas y reglas BanQuito directamente a EurekaBank.Api y su base actual: comparte un ejecutable, pero mezcla esquemas y cambia el comportamiento de una API existente.
3. **Mover el servicio BanQuito y sus datos al proyecto bancario como servicio hermano**, manteniendo el contrato REST y la base BanQuitoCore separados de la tienda. Elegido por conservar la API EurekaBank existente y establecer una frontera clara entre proyectos.

## Responsabilidades

- Banco: contratos BanQuito, elegibilidad y crédito, cálculo de amortización, persistencia BanQuitoCore, scripts de banco e inicialización.
- Celulares: contratos de tienda y DTOs del cliente remoto, catálogo, facturas, descuento en efectivo, referencia remota al crédito y comunicación HTTP.
- La factura conserva `COD_CREDITO` como referencia remota; no hay dependencia de proyecto ni clave foránea entre las bases.
