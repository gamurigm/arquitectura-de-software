function Initialize-NetBeansJavaEnvironment {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$RepositoryRoot
    )

    $toolsRoot = Join-Path (Split-Path -Parent $RepositoryRoot) 'tools'
    $javaInstalls = Join-Path $toolsRoot 'mise\installs\java'
    $jdk25 = Get-ChildItem -LiteralPath $javaInstalls -Directory |
        Where-Object { $_.Name -match '^temurin-25\.\d+\.\d+\+' } |
        Sort-Object Name -Descending |
        Select-Object -First 1
    $jdk21 = Get-ChildItem -LiteralPath $javaInstalls -Directory |
        Where-Object { $_.Name -match '^temurin-21\.\d+\.\d+\+' } |
        Sort-Object Name -Descending |
        Select-Object -First 1
    $mavenBin = Join-Path $env:ProgramFiles 'Apache NetBeans\java\maven\bin'
    $maven = Join-Path $mavenBin 'mvn.cmd'
    $mavenRepository = Join-Path $toolsRoot 'maven-repo'

    if (-not $jdk25 -or -not $jdk21) {
        throw "Se requieren los JDK 25 y 21 en $javaInstalls."
    }
    if (-not (Test-Path -LiteralPath $maven)) {
        throw "No se encontró el Maven incluido con NetBeans: $maven"
    }

    $env:JAVA_HOME = $jdk25.FullName
    $env:PATH = (@((Join-Path $jdk25.FullName 'bin'), $mavenBin, $env:PATH) -join ';')
    $mavenRepositoryArgument = '-Dmaven.repo.local="' + $mavenRepository + '"'
    $env:MAVEN_OPTS = (@($env:MAVEN_OPTS, $mavenRepositoryArgument) |
        Where-Object { -not [string]::IsNullOrWhiteSpace($_) }) -join ' '

    New-Item -ItemType Directory -Force -Path $mavenRepository | Out-Null
    return [pscustomobject]@{
        RepositoryRoot = $RepositoryRoot
        ToolsRoot = $toolsRoot
        Jdk25 = $jdk25.FullName
        Jdk21 = $jdk21.FullName
        Maven = $maven
        MavenRepository = $mavenRepository
        PayaraHome = Join-Path $toolsRoot 'payara7'
    }
}

function Get-PayaraAdminPort {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$PayaraHome,
        [Parameter(Mandatory = $true)]
        [string]$Domain
    )

    $domainConfiguration = Join-Path $PayaraHome ("glassfish\domains\$Domain\config\domain.xml")
    if (-not (Test-Path -LiteralPath $domainConfiguration)) {
        throw "No se encontró la configuración del dominio Payara: $domainConfiguration"
    }
    $domainXml = [xml](Get-Content -LiteralPath $domainConfiguration -Raw)
    $listener = $domainXml.SelectSingleNode('/domain/configs/config[@name="server-config"]/network-config/network-listeners/network-listener[@name="admin-listener"]')
    if (-not $listener) { throw "El dominio $Domain no define admin-listener en server-config." }
    $portText = $listener.GetAttribute('port')
    if ($portText -match '^\$\{([^}]+)\}$') {
        $propertyName = $Matches[1]
        $property = $null
        foreach ($scope in @('/domain/servers/server[@name="server"]/system-property', '/domain/configs/config[@name="server-config"]/system-property', '/domain/system-property')) {
            $property = @($domainXml.SelectNodes($scope) | Where-Object { $_.GetAttribute('name') -eq $propertyName }) | Select-Object -First 1
            if ($property) { break }
        }
        if (-not $property) { throw "No se pudo resolver el puerto administrativo $portText del dominio $Domain." }
        $portText = $property.GetAttribute('value')
    }
    $port = 0
    if (-not [int]::TryParse($portText, [ref]$port) -or $port -lt 1 -or $port -gt 65535) {
        throw "Puerto administrativo inválido en el dominio $Domain."
    }
    return $port
}
