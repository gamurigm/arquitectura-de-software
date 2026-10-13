$ErrorActionPreference = 'Stop'

$dotnet = 'D:\tools\dotnet10\dotnet.exe'
$herramientas = 'D:\tools'
$proyecto = Join-Path $PSScriptRoot 'WSConversionUnidades_DotNet_SOAP_GR09\WSConversionUnidades_DotNet_SOAP_GR09.csproj'
$artefactos = Join-Path $herramientas 'GR09-SOAP-DotNet\artifacts'

$env:DOTNET_ROOT = 'D:\tools\dotnet10'
$env:DOTNET_CLI_HOME = Join-Path $herramientas 'dotnet10-cli'
$env:NUGET_PACKAGES = Join-Path $herramientas 'nuget-packages'
$env:TEMP = Join-Path $herramientas 'dotnet10-tmp'
$env:TMP = $env:TEMP

foreach ($directorio in @($env:DOTNET_CLI_HOME, $env:NUGET_PACKAGES, $env:TEMP, $artefactos)) {
    New-Item -ItemType Directory -Force -Path $directorio | Out-Null
}

& $dotnet run --project $proyecto --artifacts-path $artefactos
