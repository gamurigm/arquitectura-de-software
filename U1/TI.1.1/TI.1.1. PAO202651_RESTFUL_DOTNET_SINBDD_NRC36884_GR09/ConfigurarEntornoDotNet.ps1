$ErrorActionPreference = 'Stop'

$env:DOTNET_ROOT = 'D:\tools\dotnet10'
$env:DOTNET_CLI_HOME = 'D:\tools\dotnet10-cli'
$env:NUGET_PACKAGES = 'D:\tools\nuget-packages'
$env:TEMP = 'D:\tools\dotnet10-tmp'
$env:TMP = $env:TEMP

New-Item -ItemType Directory -Force -Path @(
    $env:DOTNET_CLI_HOME,
    $env:NUGET_PACKAGES,
    $env:TEMP,
    'D:\tools\GR09-REST-DotNet\artifacts'
) | Out-Null
