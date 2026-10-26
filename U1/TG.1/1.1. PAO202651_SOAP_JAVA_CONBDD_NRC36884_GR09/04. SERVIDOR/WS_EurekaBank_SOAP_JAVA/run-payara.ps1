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

if ([string]::IsNullOrWhiteSpace($env:PAYARA_HOME)) { $env:PAYARA_HOME = $localJava.PayaraHome }
if ([string]::IsNullOrWhiteSpace($env:PAYARA_DOMAIN)) { $env:PAYARA_DOMAIN = 'gr09-payara' }
if ([string]::IsNullOrWhiteSpace($env:PAYARA_JAVA_HOME)) { $env:PAYARA_JAVA_HOME = $localJava.Jdk25 }

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
$env:JAVA_HOME = $localJava.Jdk25
$env:PATH = (Join-Path $localJava.Jdk25 'bin') + ';' + $env:PATH
Push-Location $PSScriptRoot
try {
    & $localJava.Maven --batch-mode --no-transfer-progress -DskipTests clean package
    if ($LASTEXITCODE -ne 0) {
        throw 'Maven no pudo compilar el servidor.'
    }
} finally {
    Pop-Location
}
if (-not (Test-Path -LiteralPath $war)) { throw 'Maven no generó el WAR esperado.' }

$env:JAVA_HOME = $env:PAYARA_JAVA_HOME
$env:AS_JAVA = $env:PAYARA_JAVA_HOME
$env:PATH = (Join-Path $env:PAYARA_JAVA_HOME 'bin') + ';' + $env:PATH
$adminPort = Get-PayaraAdminPort -PayaraHome $env:PAYARA_HOME -Domain $env:PAYARA_DOMAIN

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
& $asadmin --port $adminPort --interactive=false deploy --force=true $war
if ($LASTEXITCODE -ne 0) {
    throw 'Payara no pudo desplegar el WAR.'
}

Write-Output 'Servidor iniciado y WAR desplegado con variables de .env.'
