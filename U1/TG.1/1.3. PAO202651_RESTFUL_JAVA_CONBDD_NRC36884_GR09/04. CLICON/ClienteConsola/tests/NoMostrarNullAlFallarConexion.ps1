$ErrorActionPreference = 'Stop'
$proyecto = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$repositoryRoot = $PSScriptRoot
while ($repositoryRoot -and -not (Test-Path -LiteralPath (Join-Path $repositoryRoot 'scripts\Initialize-NetBeansJavaEnvironment.ps1'))) {
    $repositoryRoot = Split-Path -Parent $repositoryRoot
}
if (-not $repositoryRoot) { throw 'No se encontró scripts\Initialize-NetBeansJavaEnvironment.ps1 en el repositorio.' }
$mavenAnterior = $env:MAVEN_OPTS
$javaHomeAnterior = $env:JAVA_HOME
$mvndJavaHomeAnterior = $env:MVND_JAVA_HOME
$pathAnterior = $env:PATH
. (Join-Path $repositoryRoot 'scripts\Initialize-NetBeansJavaEnvironment.ps1')
$localJava = Initialize-NetBeansJavaEnvironment -RepositoryRoot $repositoryRoot
$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, 0)
$listener.Start()
$puerto = $listener.LocalEndpoint.Port
$listener.Stop()

$urlAnterior = $env:EUREKABANK_REST_URL
$javaHome = $localJava.Jdk25
$env:EUREKABANK_REST_URL = "http://127.0.0.1:$puerto/api/eureka"
$env:JAVA_HOME = $javaHome
$env:MVND_JAVA_HOME = $javaHome
$env:PATH = (Join-Path $javaHome 'bin') + ';' + $pathAnterior
Push-Location $proyecto
try {
    $entrada = "usuario-prueba`nclave-invalida`n"
    $salida = $entrada | & $localJava.Maven --batch-mode --no-transfer-progress -q compile exec:java 2>&1
    $texto = $salida -join [Environment]::NewLine
    if ($LASTEXITCODE -ne 0) { throw "No se pudo ejecutar el cliente de consola. Salida: $texto" }
    if ($texto -match 'EurekaBank: null') { throw 'El cliente mostró una excepción sin mensaje.' }
    if ($texto -notmatch 'servicio REST no disponible') {
        throw "El cliente no explicó que el servidor REST no está disponible. Salida: $texto"
    }
    Write-Output 'OK: el fallo de conexión muestra una instrucción útil y no imprime null.'
} finally {
    Pop-Location
    if ($null -eq $urlAnterior) { Remove-Item Env:EUREKABANK_REST_URL -ErrorAction SilentlyContinue }
    else { $env:EUREKABANK_REST_URL = $urlAnterior }
    if ($null -eq $mavenAnterior) { Remove-Item Env:MAVEN_OPTS -ErrorAction SilentlyContinue }
    else { $env:MAVEN_OPTS = $mavenAnterior }
    if ($null -eq $javaHomeAnterior) { Remove-Item Env:JAVA_HOME -ErrorAction SilentlyContinue }
    else { $env:JAVA_HOME = $javaHomeAnterior }
    if ($null -eq $mvndJavaHomeAnterior) { Remove-Item Env:MVND_JAVA_HOME -ErrorAction SilentlyContinue }
    else { $env:MVND_JAVA_HOME = $mvndJavaHomeAnterior }
    $env:PATH = $pathAnterior
}
