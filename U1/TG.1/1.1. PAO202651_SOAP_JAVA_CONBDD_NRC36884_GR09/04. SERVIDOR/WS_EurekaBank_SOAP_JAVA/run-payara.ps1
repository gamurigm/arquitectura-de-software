$ErrorActionPreference = 'Stop'

$envFile = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\..\.env'))
if (-not (Test-Path -LiteralPath $envFile)) {
    throw "Falta el archivo común U1/TG.1/.env: $envFile"
}

foreach ($line in [System.IO.File]::ReadAllLines($envFile)) {
    $trimmed = $line.Trim()
    if (-not $trimmed -or $trimmed.StartsWith('#')) {
        continue
    }
    if ($line -notmatch '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$') {
        throw 'Hay una línea inválida en .env.'
    }
    [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2].Trim(), 'Process')
}

foreach ($name in @('EUREKABANK_DB_URL', 'EUREKABANK_DB_USER', 'EUREKABANK_DB_PASSWORD', 'PAYARA_HOME', 'PAYARA_DOMAIN', 'PAYARA_JAVA_HOME')) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($name, 'Process'))) {
        throw "Falta configurar $name en .env."
    }
}

$asadmin = Join-Path $env:PAYARA_HOME 'bin\asadmin.bat'
$java = Join-Path $env:PAYARA_JAVA_HOME 'bin\java.exe'
$war = Join-Path $PSScriptRoot 'target\WS_EurekaBank_SOAP_JAVA_GR09.war'
if (-not (Test-Path -LiteralPath $asadmin)) {
    throw 'PAYARA_HOME no apunta a una instalación válida de Payara.'
}
if (-not (Test-Path -LiteralPath $java)) {
    throw 'PAYARA_JAVA_HOME no apunta a un JDK válido.'
}
if (-not (Test-Path -LiteralPath $war)) {
    throw 'Falta el WAR. Compila primero el proyecto del servidor con Java 25.'
}

$env:JAVA_HOME = $env:PAYARA_JAVA_HOME
$env:AS_JAVA = $env:PAYARA_JAVA_HOME
$env:PATH = (Join-Path $env:PAYARA_JAVA_HOME 'bin') + ';' + $env:PATH
$env:MAVEN_OPTS = (($env:MAVEN_OPTS + ' -Dmaven.repo.local=D:\tools\maven-repo').Trim())
Push-Location $PSScriptRoot
try {
    & mvn --batch-mode --no-transfer-progress -DskipTests clean package
    if ($LASTEXITCODE -ne 0) {
        throw 'Maven no pudo compilar el servidor.'
    }
} finally {
    Pop-Location
}

$domains = & $asadmin --interactive=false list-domains
if ($LASTEXITCODE -ne 0) {
    throw 'Payara no pudo listar sus dominios.'
}
if ($domains -match "(?im)^\s*$([regex]::Escape($env:PAYARA_DOMAIN))\s+running\b") {
    & $asadmin --interactive=false stop-domain $env:PAYARA_DOMAIN
    if ($LASTEXITCODE -ne 0) {
        throw 'Payara no pudo detener el dominio.'
    }
}

& $asadmin --interactive=false start-domain $env:PAYARA_DOMAIN
if ($LASTEXITCODE -ne 0) {
    throw 'Payara no pudo iniciar el dominio.'
}
& $asadmin --interactive=false deploy --force=true $war
if ($LASTEXITCODE -ne 0) {
    throw 'Payara no pudo desplegar el WAR.'
}

Write-Output 'Servidor iniciado y WAR desplegado con variables de .env.'
