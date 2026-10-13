# Conversor de Unidades Monster · REST .NET · GR09

Servidor REST y cuatro clientes independientes: consola, escritorio WPF, web MVC y Android MAUI. Todo el proyecto usa .NET 10, C# 14 y MVC en los clientes. No utiliza base de datos.

## Estructura

- 01. UML: ERS y diagramas.
- 02. MER: no aplica; no hay persistencia.
- 03. BDD: notas del alcance sin base de datos.
- 04. SERVIDOR: API REST ASP.NET Core.
- 04. CLICON: cliente de consola.
- 04. CLIESC: cliente WPF.
- 04. CLIWEB: cliente ASP.NET Core MVC.
- 04. CLIMOV: cliente Android .NET MAUI.
- 05. DOCUMENTACION: manual técnico.

## Inicio manual

Desde PowerShell, en esta carpeta:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
.\04. SERVIDOR\IniciarServidor.ps1
~~~

El servidor queda en primer plano. API: http://127.0.0.1:19082; OpenAPI: http://127.0.0.1:19082/openapi/v1.json.

Abre otra terminal, configura el entorno en D y ejecuta el cliente elegido:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
& $env:DOTNET_ROOT\dotnet.exe run --project '.\04. CLICON\ClienteConsola\ClienteConsolaGR09\ClienteConsolaGR09.csproj' --artifacts-path 'D:\tools\GR09-REST-DotNet\artifacts'
~~~

Los comandos de los demás clientes están en 05. DOCUMENTACION/02. MANUAL TECNICO.
