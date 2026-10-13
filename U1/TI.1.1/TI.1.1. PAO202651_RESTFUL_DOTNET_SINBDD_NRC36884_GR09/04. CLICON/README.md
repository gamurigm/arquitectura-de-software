# Cliente de consola

Cliente REST de consola. MVC separado en Modelos, Vistas, Controladores y Servicios.

Desde la raíz del proyecto:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
& $env:DOTNET_ROOT\dotnet.exe run --project '.\04. CLICON\ClienteConsola\ClienteConsolaGR09\ClienteConsolaGR09.csproj' --artifacts-path 'D:\tools\GR09-REST-DotNet\artifacts'
~~~

Pide usuario y clave. Oculta los caracteres de la clave mientras se escribe.
