$ErrorActionPreference = 'Stop'
$raiz = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
. (Join-Path $raiz 'ConfigurarEntornoDotNet.ps1')
$proyecto = Join-Path $PSScriptRoot 'WSConversionUnidades_DotNet_REST_GR09\WSConversionUnidades_DotNet_REST_GR09.csproj'
& (Join-Path $env:DOTNET_ROOT 'dotnet.exe') run --no-launch-profile --project $proyecto --artifacts-path 'D:\tools\GR09-REST-DotNet\artifacts'
