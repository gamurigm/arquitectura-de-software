# Cliente de consola GR09

Requiere .NET 10 y el servidor SOAP activo en el endpoint predeterminado.
El cliente implementa inicio de sesión, cambio de contraseña y conversiones de longitud, masa y temperatura.

Comando desde esta carpeta:
$env:DOTNET_ROOT = 'D:\tools\dotnet10'
$env:DOTNET_CLI_HOME = 'D:\tools\dotnet10-cli'
$env:NUGET_PACKAGES = 'D:\tools\nuget-packages'
& 'D:\tools\dotnet10\dotnet.exe' run --project .\ClienteConsolaGR09.csproj --artifacts-path D:\tools\GR09-SOAP-DotNet\artifacts

Para cambiar la dirección del servidor, define CONVERSOR_SOAP_URL.
