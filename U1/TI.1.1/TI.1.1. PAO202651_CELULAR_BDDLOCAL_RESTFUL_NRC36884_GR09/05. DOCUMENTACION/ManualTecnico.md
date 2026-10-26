# Manual técnico del proyecto de celulares

## Entorno

El proyecto usa .NET SDK 10 y C# 14. En este equipo está disponible SDK 10.0.402. Los artefactos se guardan en `C:\Users\GABI\Documents\ESPE VIII SII2026\tools\CelularGR09` y los paquetes NuGet en `C:\Users\GABI\Documents\ESPE VIII SII2026\tools\nuget-packages`, configurados en `Directory.Build.props`.

El destino de entrega es SQL Server 2025 Express (`SQLEXPRESS`). El desarrollo local usa SQL Server 2025 Developer en la instancia predeterminada (`.`).

## Inicializar la base de la tienda

Los scripts de este proyecto crean y siembran únicamente `ComercializadoraCelulares`. Desde la raíz del proyecto celular:

~~~powershell
$env:CELULARES_SQL_MASTER = 'Server=.;Database=master;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;Connect Timeout=8'
dotnet run --project '.\04. SERVIDOR\Administracion\Administracion.csproj' -- --aplicar '.\03. BDD\01_CrearBases.sql' '.\03. BDD\02_DatosPrueba.sql'
~~~

Los scripts no eliminan datos. No aplicar scripts bancarios desde este proyecto.

## Servicio del banco

BanQuito.Api y `BanQuitoCore` pertenecen al proyecto bancario `U1/TG.1/1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09`. Sus scripts se aplican desde esa raíz con `BanQuito.Administracion`; las instrucciones están en el README bancario. El banco debe estar disponible en `http://127.0.0.1:19101/` para ventas a crédito. La tienda se conecta mediante `Servicios:BanQuitoUrl`; no referencia ensamblados del banco.

## Ejecutar la API de la tienda

Desde la raíz del proyecto celular, configura la conexión local y ejecuta:

~~~powershell
$env:ConnectionStrings__BaseDatos = 'Server=.;Database=ComercializadoraCelulares;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;Connect Timeout=8'
$env:Servicios__BanQuitoUrl = 'http://127.0.0.1:19101/'
dotnet run --project '.\04. SERVIDOR\Comercializadora.Api\Comercializadora.Api.csproj'
~~~

La URL de tienda es `http://127.0.0.1:19102`.

- `GET /api/celulares` lista el catálogo; POST, PUT y DELETE mantienen los celulares. DELETE desactiva el registro para conservar facturas históricas.
- `POST /api/facturas` acepta `EFECTIVO` o `CREDITO_DIRECTO`. En efectivo aplica 42% de descuento. A crédito, primero persiste la factura pendiente y después solicita elegibilidad y crédito a BanQuito con `IdOperacion` como clave idempotente.
- `GET /api/facturas/{id}/amortizacion` consulta las cuotas del banco para un crédito aprobado.

Un rechazo o una caída del banco no confirma la venta. La factura puede quedar pendiente para reintento con la misma `IdOperacion`.

## Estado de verificación

La compilación se registra en `05. DOCUMENTACION/AVANCE.md`. Esta guía no declara una verificación HTTP completa ni ejecución de pruebas automatizadas.
