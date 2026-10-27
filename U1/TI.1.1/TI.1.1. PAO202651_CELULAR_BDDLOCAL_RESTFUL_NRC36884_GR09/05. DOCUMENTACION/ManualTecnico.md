# Manual técnico

.NET SDK 10 está en `D:\tools\dotnet10`; los paquetes se guardan en `D:\NuGet\packages`. El destino es SQL Server 2025 Express en `SQLEXPRESS`. En este equipo, las dos bases quedaron inicializadas con sus scripts de creación y datos de prueba.

## Instalar SQL Server

Abrir PowerShell como administrador y ejecutar desde esta carpeta:

```powershell
& '.\03. BDD\InstalarSQL2025.ps1' -Media 'D:\Downloads\sqlserver2025\engine\SETUP.EXE'
```

El instalador verifica la firma Microsoft y no reemplaza una instancia SQLEXPRESS existente. Revisar su resultado antes de continuar.

## Inicializar las bases

Desde esta carpeta, en una terminal normal:

```powershell
$dotnet = 'D:\tools\dotnet10\dotnet.exe'
$banco = '..\..\TG.1\1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09'
& $dotnet run --project (Join-Path $banco '01. SERVIDOR\BanQuito.Administracion\BanQuito.Administracion.csproj') -- --aplicar (Join-Path $banco '03. BDD\BanQuito_01_CrearBase.sql') (Join-Path $banco '03. BDD\BanQuito_02_DatosPrueba.sql')
& $dotnet run --project '.\04. SERVIDOR\Administracion\Administracion.csproj' -- --aplicar '.\03. BDD\01_CrearBases.sql' '.\03. BDD\02_DatosPrueba.sql'
```

Los inicializadores usan autenticación Windows y `SQLEXPRESS`. Los scripts son idempotentes; revise los datos existentes antes de aplicarlos.

## Iniciar servicios y clientes

Desde la carpeta del proyecto, en terminales separadas:

```powershell
& 'D:\tools\dotnet10\dotnet.exe' run --project '..\..\TG.1\1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09\01. SERVIDOR\BanQuito.Api\BanQuito.Api.csproj'
& 'D:\tools\dotnet10\dotnet.exe' run --project '.\04. SERVIDOR\Comercializadora.Api\Comercializadora.Api.csproj'
& 'D:\tools\dotnet10\dotnet.exe' run --project '.\04. CLIWEB\Celulares.Web\Celulares.Web.csproj'
```

BanQuito escucha en `127.0.0.1:19101`, la tienda en `127.0.0.1:19102` y la web en `http://127.0.0.1:19103`. También se puede ejecutar `04. CLICON/Celulares.Consola` o `04. CLIESC/Celulares.Escritorio` con `dotnet run --project` y su archivo `.csproj`.

El emulador Android utiliza `http://10.0.2.2:19102/`. Para un teléfono físico, iniciar la API de tienda con `-- --urls http://0.0.0.0:19102`, permitir el puerto en la red local y escribir la IP del equipo en el cliente móvil. Esta opción expone la API en la red local; utilizar solo una red confiable.

## Compilar e instalar Android

El SDK Android 36 está en `D:\tools\android-sdk`. En Debug, Fast Deployment instala ensamblados auxiliares junto con la app. Para probar en un emulador, usa el objetivo `Run` de MSBuild:

```powershell
& 'D:\tools\android-sdk\platform-tools\adb.exe' devices
& 'D:\tools\dotnet10\dotnet.exe' build '.\04. CLIMOV\Celulares.Movil\Celulares.Movil.csproj' -t:Run -p:AndroidSdkDirectory='D:\tools\android-sdk'
```

En el emulador, la aplicación se conecta a `10.0.2.2:19102`. Una instalación manual del APK Debug omite los ensamblados Fast Deployment; ejecuta desde MSBuild o usa una compilación Release.

Los clientes permiten catálogo, ventas y consulta de facturas y amortización. Una venta pendiente debe reintentarse sin cambiar sus datos para conservar su identificador de operación.
