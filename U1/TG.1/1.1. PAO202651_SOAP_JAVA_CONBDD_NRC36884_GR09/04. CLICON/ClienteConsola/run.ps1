$ErrorActionPreference = 'Stop'

$env:JAVA_HOME = 'D:\tools\jdk-25'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:MAVEN_OPTS = '-Dmaven.repo.local=D:\tools\maven-repo'

if (-not (Test-Path $env:JAVA_HOME)) {
    throw "No se encontró Java 25 en $env:JAVA_HOME"
}

$envFilePath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\..\.env'))
if (Test-Path -LiteralPath $envFilePath) {
    foreach ($line in [System.IO.File]::ReadAllLines($envFilePath)) {
        if ($line -match '^\s*EUREKABANK_SOAP_URL\s*=\s*(.*?)\s*$' -and -not $Matches[1].StartsWith('#')) {
            $env:EUREKABANK_SOAP_URL = $Matches[1]
        }
    }
}

Set-Location -LiteralPath $PSScriptRoot
$maven = Get-Command mvn -ErrorAction Stop
& $maven.Source compile exec:java
