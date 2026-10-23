# Manual técnico del avance GR09

## Herramientas

.NET SDK 10.0.401 en D:\tools\dotnet10. NuGet y salidas de compilación se redirigen a D: mediante Directory.Build.props y NuGet.Config.
SQL Server 2025 Express: instalación mediante 03. BDD/InstalarSQL2025.ps1, ejecutado como administrador.
No ejecutar el instalador sobre una instancia SQLEXPRESS existente: el script lo rechaza.

## Preparar SQL

Después de verificar la instalación y el servicio MSSQL$SQLEXPRESS, desde la raíz del proyecto:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
& "$env:DOTNET_ROOT\dotnet.exe" run --project '.\04. SERVIDOR\Administracion\Administracion.csproj' -- --aplicar '.\03. BDD\01_CrearBases.sql' '.\03. BDD\02_DatosPrueba.sql'
~~~

Los scripts no borran datos. Si faltan permisos o el esquema existente es incompatible, resolver el error antes de continuar.
CELULARES_SQL_MASTER permite cambiar la conexión del inicializador.
La API usa ConnectionStrings:BaseDatos y admite el override estándar ConnectionStrings__BaseDatos.

## API bancaria

~~~powershell
& "$env:DOTNET_ROOT\dotnet.exe" run --project '.\04. SERVIDOR\BanQuito.Api\BanQuito.Api.csproj'
~~~

URL por defecto http://127.0.0.1:19101.
GET /api/salud comprueba conexión SQL.
GET /api/credito/elegibilidad?cedula=0102030405 consulta elegibilidad.
GET /api/credito/clientes/1/monto-maximo calcula límite.
POST /api/creditos recibe codigoCliente, precioTelefono, numeroCuotas y opcional referenciaVenta (GUID).
GET /api/creditos/{id}/amortizacion devuelve cuotas persistidas.

Errores: 400 validación, 404 inexistente, 409 rechazo/conflicto, 503 SQL no disponible.
El crédito y sus cuotas se insertan juntos; el índice filtrado impide más de un activo por cliente.
Reusar referenciaVenta con los mismos datos devuelve el crédito existente.

## Pruebas

~~~powershell
& "$env:DOTNET_ROOT\dotnet.exe" test '.\04. SERVIDOR\Pruebas\Celulares.Pruebas.csproj'
~~~

La suite verifica reglas sin sustituir la prueba de integración SQL. La API comercializadora y las cuatro interfaces todavía están pendientes.

