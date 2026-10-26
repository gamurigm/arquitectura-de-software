$ErrorActionPreference = 'Stop'

$repositoryRoot = $PSScriptRoot
while ($repositoryRoot -and -not (Test-Path -LiteralPath (Join-Path $repositoryRoot 'scripts\Initialize-NetBeansJavaEnvironment.ps1'))) {
    $repositoryRoot = Split-Path -Parent $repositoryRoot
}
if (-not $repositoryRoot) {
    throw 'No se encontró scripts\Initialize-NetBeansJavaEnvironment.ps1 en el repositorio.'
}
. (Join-Path $repositoryRoot 'scripts\Initialize-NetBeansJavaEnvironment.ps1')
$localJava = Initialize-NetBeansJavaEnvironment -RepositoryRoot $repositoryRoot

$envFilePath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\..\.env'))
if (Test-Path -LiteralPath $envFilePath) {
    foreach ($line in [System.IO.File]::ReadAllLines($envFilePath)) {
        if ($line -match '^\s*EUREKABANK_SOAP_URL\s*=\s*(.*?)\s*$' -and -not $Matches[1].StartsWith('#')) {
            $env:EUREKABANK_SOAP_URL = $Matches[1]
        }
    }
}

Set-Location -LiteralPath $PSScriptRoot
& $localJava.Maven --batch-mode --no-transfer-progress compile exec:java
if ($LASTEXITCODE -ne 0) { throw "Maven terminó con código $LASTEXITCODE." }
