# Cliente de escritorio

Cliente WPF sobre .NET 10. La vista VentanaPrincipal está en Vistas; ControladorConversor coordina modelos y ClienteRest.

Desde la raíz del proyecto:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
& $env:DOTNET_ROOT\dotnet.exe run --project '.\04. CLIESC\ClienteEscritorio\ClienteEscritorioGR09\ClienteEscritorioGR09.csproj' --artifacts-path 'D:\tools\GR09-REST-DotNet\artifacts'
~~~
