# Cliente web GR09

ASP.NET Core MVC para .NET 10. El navegador envía solicitudes al cliente web; el servidor MVC llama al servicio SOAP y conserva el token en sesión con vencimiento local de 15 minutos.

Ejecuta el servidor SOAP en otra terminal desde la carpeta 04. SERVIDOR con IniciarServidor.ps1.
Desde esta carpeta ejecuta:
$env:DOTNET_ROOT = 'D:\tools\dotnet10'
$env:DOTNET_CLI_HOME = 'D:\tools\dotnet10-cli'
$env:NUGET_PACKAGES = 'D:\tools\nuget-packages'
& 'D:\tools\dotnet10\dotnet.exe' run --project .\ClienteWebGR09.csproj --artifacts-path D:\tools\GR09-SOAP-DotNet\artifacts

Abre http://127.0.0.1:5080. El endpoint SOAP se configura con CONVERSOR_SOAP_URL.
