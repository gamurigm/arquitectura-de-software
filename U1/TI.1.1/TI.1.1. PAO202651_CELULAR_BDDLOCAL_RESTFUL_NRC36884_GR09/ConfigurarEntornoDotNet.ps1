$ErrorActionPreference = 'Stop'
$toolsRoot = 'C:\Users\GABI\Documents\ESPE VIII SII2026\tools'
$env:DOTNET_ROOT = 'C:\Program Files\dotnet'
$env:DOTNET_CLI_HOME = Join-Path $toolsRoot 'dotnet-cli'
$env:NUGET_PACKAGES = Join-Path $toolsRoot 'nuget-packages'
$env:TEMP = Join-Path $toolsRoot 'dotnet-temp'
$env:TMP = $env:TEMP
$env:CelularBuildRoot = Join-Path $toolsRoot 'CelularGR09'
$env:CelularPackagesRoot = $env:NUGET_PACKAGES
if (-not (Get-Command dotnet -ErrorAction SilentlyContinue)) {
    throw 'No se encuentra dotnet en el PATH. Abra una terminal nueva y vuelva a intentarlo.'
}
foreach ($ruta in @($env:DOTNET_CLI_HOME, $env:NUGET_PACKAGES, $env:TEMP, $env:CelularBuildRoot)) {
    New-Item -ItemType Directory -Force -Path $ruta | Out-Null
}
