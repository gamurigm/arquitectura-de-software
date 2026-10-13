# Cliente de escritorio GR09

WPF para .NET 10. Inicia sesión, convierte longitud, masa y temperatura, y permite cambiar la contraseña.

Ejecuta el servidor en otra terminal desde la carpeta 04. SERVIDOR con IniciarServidor.ps1.
Desde esta carpeta ejecuta:
$env:DOTNET_ROOT = 'D:\tools\dotnet10'
$env:DOTNET_CLI_HOME = 'D:\tools\dotnet10-cli'
$env:NUGET_PACKAGES = 'D:\tools\nuget-packages'
& 'D:\tools\dotnet10\dotnet.exe' run --project .\ClienteEscritorioGR09.csproj --artifacts-path D:\tools\GR09-SOAP-DotNet\artifacts

El endpoint predeterminado se puede cambiar mediante CONVERSOR_SOAP_URL.
