#Requires -RunAsAdministrator
param(
    [string]$Media = 'D:\downloads\sqlserver2025\engine\SETUP.EXE',
    [string]$Usuario = [Security.Principal.WindowsIdentity]::GetCurrent().Name
)
$ErrorActionPreference='Stop'
$logs='D:\tools\CelularGR09\sql-setup'
New-Item -ItemType Directory -Force -Path $logs | Out-Null
Start-Transcript -Path (Join-Path $logs 'instalacion.log') -Append
try {
    if(Get-Service -Name 'MSSQL$SQLEXPRESS' -ErrorAction SilentlyContinue) { throw 'SQLEXPRESS ya existe; no se reemplaza automáticamente.' }
    $firma=Get-AuthenticodeSignature -LiteralPath $Media
    if($firma.Status -ne 'Valid' -or $firma.SignerCertificate.Subject -notmatch 'Microsoft Corporation') { throw 'No se pudo validar la firma Microsoft del instalador.' }
    $argumentos=@('/Q','/ACTION=Install','/FEATURES=SQLENGINE','/INSTANCENAME=SQLEXPRESS',
        '/IACCEPTSQLSERVERLICENSETERMS','/SQLSVCSTARTUPTYPE=Automatic','/TCPENABLED=0','/NPENABLED=0',
        '/INSTANCEDIR=D:\tools\SQLServer2025','/INSTALLSHAREDDIR=D:\tools\SQLServer2025\Shared',
        '/INSTALLSHAREDWOWDIR=D:\tools\SQLServer2025\Shared32','/INSTALLSQLDATADIR=D:\data\sqlserver2025',
        '/SQLUSERDBDIR=D:\data\sqlserver2025\data','/SQLUSERDBLOGDIR=D:\data\sqlserver2025\log',
        '/SQLTEMPDBDIR=D:\data\sqlserver2025\temp','/SQLTEMPDBLOGDIR=D:\data\sqlserver2025\temp',
        '/SQLBACKUPDIR=D:\data\sqlserver2025\backup',('/SQLSYSADMINACCOUNTS="'+$Usuario+'"'))
    $p=Start-Process -FilePath $Media -WindowStyle Hidden -ArgumentList $argumentos -PassThru -Wait
    @{exitCode=$p.ExitCode;time=(Get-Date -Format o)} | ConvertTo-Json | Set-Content (Join-Path $logs 'resultado.json')
    if($p.ExitCode -notin @(0,3010)) { throw "SQL Setup devolvió $($p.ExitCode). Revisar Summary.txt de SQL Setup." }
    Get-Service -Name 'MSSQL$SQLEXPRESS'
} finally { Stop-Transcript }

