$ErrorActionPreference = 'Stop'

$toolsRoot = Join-Path (Split-Path -Parent $PSScriptRoot) 'tools'
$androidSdk = Join-Path $toolsRoot 'android-sdk'
$env:ANDROID_HOME = $androidSdk
$env:ANDROID_SDK_ROOT = $androidSdk
$env:ANDROID_AVD_HOME = Join-Path $toolsRoot 'android-avd'

$emulator = Join-Path $androidSdk 'emulator\emulator.exe'
$avdConfiguration = Join-Path $env:ANDROID_AVD_HOME 'GR09_API_36.ini'
if (-not (Test-Path -LiteralPath $emulator)) {
    throw "No se encontró Android Emulator en $emulator"
}
if (-not (Test-Path -LiteralPath $avdConfiguration)) {
    throw "No se encontró el emulador GR09_API_36 en $env:ANDROID_AVD_HOME"
}

Start-Process -FilePath $emulator -ArgumentList '-avd GR09_API_36' -WindowStyle Normal | Out-Null
Write-Output 'Emulador GR09_API_36 iniciado.'
