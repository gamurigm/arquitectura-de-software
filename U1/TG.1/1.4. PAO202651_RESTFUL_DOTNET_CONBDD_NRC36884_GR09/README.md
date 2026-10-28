# EurekaBank REST .NET

API ASP.NET Core 10 conectada a la base de datos SQL Server existente, con clientes de consola, escritorio WPF, Android MAUI y web ASP.NET MVC. Las aplicaciones cliente consumen la API; solo el servidor accede a SQL Server.

Configure localmente EUREKABANK_DB_CONNECTION_STRING para el servidor y EUREKABANK_REST_DOTNET_URL para los clientes. La API usa http://localhost:5081/; MAUI usa http://10.0.2.2:5081/ en el emulador Android. No guarde cadenas de conexión ni contraseñas en el repositorio.

`run-api.ps1` carga la configuración local y arranca la API. La base de datos ya debe existir; el servidor no crea ni inicializa tablas o datos.

Para cargar datos de prueba en una base vacía, ejecute una vez `..\1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR09\03. BDD\Cargar_Datos_SQLServer.sql`. `Monster` / `Monster9` queda asociado al cliente `00005` y muestra las cuentas `00100001` y `00100002`.

Las credenciales existentes usan hashes pbkdf2-sha256 con 600 000 iteraciones, 16 bytes de sal y 32 bytes de salida. El inicio de sesión responde autenticado; los depósitos responden registrado; los movimientos contienen numero, cuenta, fecha ISO 8601, tipo, accion e importe. No se emiten tokens.

El contrato expone `POST /api/eureka/login` con `usuario` y `contrasena`, `GET /api/eureka/movimientos?cuenta=...` y `POST /api/eureka/depositos` con `cuenta` e `importe`. El importe debe ser positivo y tener como máximo dos decimales. Las aplicaciones mantienen el inicio de sesión local solo después de una respuesta autenticada y limpian ese estado al cerrar sesión.

Antes de abrir un cliente desde PowerShell, ejecute `CargarVariables.ps1` en esa misma terminal para cargar la configuración local común.

## Servicio de crédito BanQuito

Este proyecto bancario también contiene `BanQuito.Api`, un servicio REST de crédito consumido por el proyecto separado de celulares `U1/TI.1.1/TI.1.1. PAO202651_CELULAR_BDDLOCAL_RESTFUL_NRC36884_GR09`. Su código, reglas, pruebas y base `BanQuitoCore` están bajo este directorio bancario. La tienda no referencia sus ensamblados ni inicializa su base.

BanQuito se ejecuta independientemente en `http://127.0.0.1:19101/`. Desde la raíz de este proyecto, use `run-banquito.ps1`. Puede configurar `BANQUITO_DB_CONNECTION_STRING`; sin ella, el servicio usa `SQLEXPRESS` como destino por defecto. Para usar la base local Developer en PowerShell:

~~~powershell
$env:BANQUITO_DB_CONNECTION_STRING = 'Server=.;Database=BanQuitoCore;Integrated Security=True;Encrypt=True;TrustServerCertificate=True'
& '.\run-banquito.ps1'
~~~

El contrato bancario ofrece `GET /api/salud`, `GET /api/credito/elegibilidad?cedula=...`, `GET /api/credito/clientes/{codigoCliente}/monto-maximo`, `POST /api/creditos` y `GET /api/creditos/{id}/amortizacion`. El otorgamiento vuelve a comprobar elegibilidad y máximo dentro de una transacción. Repetir `referenciaVenta` con los mismos datos devuelve el crédito existente.

Inicialice el esquema y sus datos desde la raíz bancaria con:

~~~powershell
$env:BANQUITO_SQL_MASTER = 'Server=.;Database=master;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;Connect Timeout=8'
dotnet run --project '.\04. SERVIDOR\BanQuito.Administracion\BanQuito.Administracion.csproj' -- --aplicar '.\03. BDD\BanQuito_01_CrearBase.sql' '.\03. BDD\BanQuito_02_DatosPrueba.sql'
~~~

BanQuito expone `GET /api/salud`, elegibilidad por cédula, monto máximo por código de cliente, otorgamiento idempotente y consulta de amortización. El contrato completo está en `01. UML/CasosUsoCreditoBanQuito.md`. Sus datos están descritos en `02. MER/ModeloDatosBanQuito.md`.
