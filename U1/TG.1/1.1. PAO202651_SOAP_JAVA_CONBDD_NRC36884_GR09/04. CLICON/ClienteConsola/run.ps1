$ErrorActionPreference = 'Stop'

$env:JAVA_HOME = 'D:\tools\jdk-25'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:MAVEN_OPTS = '-Dmaven.repo.local=D:\tools\maven-repo'

if (-not (Test-Path $env:JAVA_HOME)) {
    throw "No se encontró Java 25 en $env:JAVA_HOME"
}

Set-Location -LiteralPath $PSScriptRoot
$maven = Get-Command mvn -ErrorAction Stop
& $maven.Source compile exec:java
