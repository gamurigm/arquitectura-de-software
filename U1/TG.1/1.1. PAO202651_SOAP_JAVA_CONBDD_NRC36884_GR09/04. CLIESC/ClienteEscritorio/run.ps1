$ErrorActionPreference = 'Stop'

$javaHomePath = 'D:\tools\jdk-25'
if (-not (Test-Path -LiteralPath (Join-Path $javaHomePath 'bin\java.exe'))) {
    throw "JDK 25 no encontrado en $javaHomePath."
}
$env:JAVA_HOME = $javaHomePath
$env:PATH = (Join-Path $javaHomePath 'bin') + ';' + $env:PATH
$env:MAVEN_OPTS = (($env:MAVEN_OPTS + ' -Dmaven.repo.local=D:\tools\maven-repo').Trim())

$envFilePath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\..\.env'))
if (Test-Path -LiteralPath $envFilePath) {
    foreach ($line in [System.IO.File]::ReadAllLines($envFilePath)) {
        if ($line -match '^\s*EUREKABANK_SOAP_URL\s*=\s*(.*?)\s*$' -and -not $Matches[1].StartsWith('#')) {
            $env:EUREKABANK_SOAP_URL = $Matches[1]
        }
    }
}

Push-Location $PSScriptRoot
try {
    & mvn --batch-mode --no-transfer-progress '-Dstyle.color=never' compile exec:java
    if ($LASTEXITCODE -ne 0) { throw "Maven terminó con código $LASTEXITCODE." }
} finally {
    Pop-Location
}
