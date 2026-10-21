[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('ConfigureStage', 'ConfigureFinal', 'Start', 'Stop', 'Deploy', 'Status')]
    [string]$Action,

    [ValidateSet('Soap', 'Rest', 'All')]
    [string]$Target = 'All'
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$toolsDir = Join-Path (Split-Path $repoRoot -Parent) 'tools'
$javaHome = Join-Path $toolsDir 'mise\installs\java\temurin-25.0.4+101.0.LTS'
$payaraHome = Join-Path $toolsDir 'payara7'
$domainsRoot = Join-Path $payaraHome 'glassfish\domains'
$domainName = 'gr09-payara'
$domainDir = Join-Path $domainsRoot $domainName
$modeFile = Join-Path $toolsDir 'payara-gr09-mode.txt'
$adminPort = 14848
$stagePort = 18080
$restPort = 8080
$soapPort = 19080

$soapRoot = Join-Path $repoRoot 'U1\TI.1.1\TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09'
$restRoot = Join-Path $repoRoot 'U1\TI.1.1\TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09'
$artifacts = @{
    SoapService = Join-Path $soapRoot '04. SERVIDOR\WSConversionUnidades_Java_SOAP_GR09\target\WSConversionUnidades_Java_SOAP_GR09.war'
    SoapWeb = Join-Path $soapRoot '04. CLIWEB\Cliente_Web\target\Cliente_Web.war'
    RestService = Join-Path $restRoot '04. SERVIDOR\WS_ConUni_Java_REST\target\WS_ConUni_Java_REST.war'
    RestWeb = Join-Path $restRoot '04. CLIWEB\Cliente_Web\target\Cliente_Web.war'
}

if (-not (Test-Path -LiteralPath (Join-Path $javaHome 'bin\java.exe'))) {
    throw "No se encontró Temurin JDK 25 en $javaHome"
}
$env:JAVA_HOME = $javaHome
$env:Path = (Join-Path $javaHome 'bin') + ';' + $env:Path

$asadminCandidates = @(
    (Join-Path $payaraHome 'bin\asadmin.bat'),
    (Join-Path $payaraHome 'glassfish\bin\asadmin.bat')
)
$asadminPath = $asadminCandidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
if (-not $asadminPath) {
    throw "No se encontró asadmin.bat en la instalación local de Payara: $payaraHome"
}
$script:AsadminPath = $asadminPath

function Invoke-Asadmin {
    param([Parameter(Mandatory = $true)][string[]]$AsadminArgs)

    $output = & $script:AsadminPath @AsadminArgs 2>&1
    $exitCode = $LASTEXITCODE
    if ($exitCode -ne 0) {
        throw "asadmin terminó con código $exitCode.`n$($output -join "`n")"
    }
    if ($output) { $output | ForEach-Object { Write-Output $_ } }
}

function Get-AsadminText {
    param([Parameter(Mandatory = $true)][string[]]$AsadminArgs)

    $output = & $script:AsadminPath @AsadminArgs 2>&1
    $exitCode = $LASTEXITCODE
    if ($exitCode -ne 0) {
        throw "asadmin terminó con código $exitCode.`n$($output -join "`n")"
    }
    return ($output | Out-String).Trim()
}

function Invoke-RemoteAsadmin {
    param([Parameter(Mandatory = $true)][string[]]$AsadminArgs)

    $prefix = @('--host', '127.0.0.1', '--port', "$adminPort", '--user', 'admin', '--interactive=false')
    Invoke-Asadmin -AsadminArgs ($prefix + $AsadminArgs)
}

function Assert-DomainExists {
    if (-not (Test-Path -LiteralPath (Join-Path $domainDir 'config\domain.xml'))) {
        throw "No existe el dominio $domainName en $domainDir. Ejecute ConfigureStage primero."
    }
}

function Get-ListeningConnections {
    param([Parameter(Mandatory = $true)][int]$Port)
    return @(Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue)
}

function Assert-PortFree {
    param([Parameter(Mandatory = $true)][int]$Port)
    $connections = @(Get-ListeningConnections -Port $Port)
    if ($connections.Count -gt 0) {
        $owners = ($connections | Select-Object -ExpandProperty OwningProcess -Unique) -join ', '
        throw "El puerto $Port está ocupado (PID $owners). No se cambió el dominio."
    }
}

function Test-DomainRunning {
    Assert-DomainExists
    $output = Get-AsadminText -AsadminArgs @('list-domains', '--domaindir', $domainsRoot)
    return $output -match ("(?im)^\s*" + [regex]::Escape($domainName) + '\s+running\b')
}

function Set-DomainListenerAddresses {
    param([Parameter(Mandatory = $true)][string]$DomainXmlPath)

    $settings = New-Object System.Xml.XmlReaderSettings
    $settings.DtdProcessing = [System.Xml.DtdProcessing]::Ignore
    $settings.XmlResolver = $null
    $reader = [System.Xml.XmlReader]::Create($DomainXmlPath, $settings)
    $xml = New-Object System.Xml.XmlDocument
    $xml.PreserveWhitespace = $true
    try { $xml.Load($reader) } finally { $reader.Dispose() }

    $listeners = $xml.SelectNodes("//*[local-name()='network-listener']")
    if ($listeners.Count -eq 0) { throw "No se encontraron network-listener en $DomainXmlPath" }
    foreach ($listener in $listeners) {
        $listener.SetAttribute('address', '127.0.0.1')
    }
    $writerSettings = New-Object System.Xml.XmlWriterSettings
    $writerSettings.Encoding = New-Object System.Text.UTF8Encoding($false)
    $writerSettings.Indent = $false
    $writer = [System.Xml.XmlWriter]::Create($DomainXmlPath, $writerSettings)
    try { $xml.Save($writer) } finally { $writer.Dispose() }
}

function Get-Mode {
    if (Test-Path -LiteralPath $modeFile) {
        return ([System.IO.File]::ReadAllText($modeFile)).Trim().ToLowerInvariant()
    }
    return 'final'
}

function Start-PayaraDomain {
    Assert-DomainExists
    $mode = Get-Mode
    if ($mode -eq 'stage') {
        $env:WS_BASE_URL = "http://127.0.0.1:$stagePort/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09"
    } else {
        $env:WS_BASE_URL = "http://127.0.0.1:$soapPort/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09"
    }
    Write-Output "WS_BASE_URL configurada para Payara: $env:WS_BASE_URL"
    Invoke-Asadmin -AsadminArgs @('start-domain', '--domaindir', $domainsRoot, $domainName)
}

function Deploy-War {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$ContextRoot,
        [Parameter(Mandatory = $true)][string]$WarPath
    )

    if (-not (Test-Path -LiteralPath $WarPath)) { throw "No existe el WAR requerido: $WarPath" }
    Invoke-RemoteAsadmin -AsadminArgs @('deploy', '--force=true', '--name', $Name, '--contextroot', $ContextRoot, $WarPath)
}

function Deploy-Target {
    param([Parameter(Mandatory = $true)][ValidateSet('Soap', 'Rest', 'All')][string]$DeployTarget)

    if (-not (Test-DomainRunning)) { throw "El dominio $domainName no está activo. Ejecute Start primero." }
    switch ($DeployTarget) {
        'Soap' {
            Deploy-War -Name 'soap-gr09-service' -ContextRoot '/04.SERVIDOR' -WarPath $artifacts.SoapService
            Deploy-War -Name 'soap-gr09-web' -ContextRoot '/soap-web' -WarPath $artifacts.SoapWeb
        }
        'Rest' {
            Deploy-War -Name 'rest-gr09-api' -ContextRoot '/' -WarPath $artifacts.RestService
            Deploy-War -Name 'rest-gr09-web' -ContextRoot '/rest-web' -WarPath $artifacts.RestWeb
        }
        'All' {
            Deploy-Target -DeployTarget 'Soap'
            Deploy-Target -DeployTarget 'Rest'
        }
    }
}

switch ($Action) {
    'ConfigureStage' {
        if (Test-Path -LiteralPath (Join-Path $domainDir 'config\domain.xml')) {
            throw "El dominio $domainName ya existe en $domainDir; no se sobrescribió."
        }
        Assert-PortFree -Port $stagePort
        Assert-PortFree -Port $adminPort
        Invoke-Asadmin -AsadminArgs @(
            'create-domain', '--domaindir', $domainsRoot, '--adminport', "$adminPort",
            '--instanceport', "$stagePort", '--nopassword=true', '--checkports=true', $domainName
        )
        Set-DomainListenerAddresses -DomainXmlPath (Join-Path $domainDir 'config\domain.xml')
        [System.IO.File]::WriteAllText($modeFile, 'stage', [System.Text.UTF8Encoding]::new($false))
        Write-Output "Dominio $domainName configurado para preparación: HTTP $stagePort, administración local $adminPort."
    }
    'Start' {
        Start-PayaraDomain
    }
    'Stop' {
        Assert-DomainExists
        if (Test-DomainRunning) {
            Invoke-Asadmin -AsadminArgs @('stop-domain', '--domaindir', $domainsRoot, $domainName)
        } else {
            Write-Output "El dominio $domainName ya está detenido."
        }
    }
    'Deploy' {
        Deploy-Target -DeployTarget $Target
    }
    'ConfigureFinal' {
        Assert-DomainExists
        if (-not (Test-DomainRunning)) { throw "Inicie Payara en preparación antes de ConfigureFinal." }
        Assert-PortFree -Port $restPort
        Assert-PortFree -Port $soapPort

        Invoke-RemoteAsadmin -AsadminArgs @(
            'set', "server-config.network-config.network-listeners.network-listener.http-listener-1.port=$restPort"
        )
        Invoke-RemoteAsadmin -AsadminArgs @(
            'set', 'server-config.network-config.network-listeners.network-listener.http-listener-1.address=127.0.0.1'
        )
        $listeners = Get-AsadminText -AsadminArgs @(
            '--host', '127.0.0.1', '--port', "$adminPort", '--user', 'admin', '--interactive=false',
            'list-network-listeners', 'server'
        )
        if ($listeners -notmatch '(?m)^\s*soap-http-listener\s*$') {
            Invoke-RemoteAsadmin -AsadminArgs @(
                'create-network-listener', '--address', '127.0.0.1', '--listenerport', "$soapPort",
                '--protocol', 'http-listener-1', '--transport', 'tcp', 'soap-http-listener'
            )
        }
        Invoke-Asadmin -AsadminArgs @('stop-domain', '--domaindir', $domainsRoot, $domainName)
        [System.IO.File]::WriteAllText($modeFile, 'final', [System.Text.UTF8Encoding]::new($false))
        Write-Output "Dominio configurado para operación estable: REST $restPort, SOAP e interfaces web $soapPort. Payara quedó detenido; ejecute Start."
    }
    'Status' {
        Invoke-Asadmin -AsadminArgs @('version')
        if (-not (Test-Path -LiteralPath (Join-Path $domainDir 'config\domain.xml'))) {
            Write-Output "No existe el dominio $domainName."
            break
        }
        Get-AsadminText -AsadminArgs @('list-domains', '--domaindir', $domainsRoot) | Write-Output
        if (Test-DomainRunning) {
            Get-AsadminText -AsadminArgs @(
                '--host', '127.0.0.1', '--port', "$adminPort", '--user', 'admin', '--interactive=false',
                'list-network-listeners', 'server'
            ) | Write-Output
            Get-AsadminText -AsadminArgs @(
                '--host', '127.0.0.1', '--port', "$adminPort", '--user', 'admin', '--interactive=false',
                'list-applications'
            ) | Write-Output
        }
        $mode = Get-Mode
        Write-Output "Contextos previstos: /04.SERVIDOR, /soap-web, / (API REST), /rest-web. Modo: $mode."
    }
}
