$ErrorActionPreference = 'Stop'

$repositoryRoot = (Resolve-Path -LiteralPath $PSScriptRoot).Path
$toolsRoot = Join-Path (Split-Path -Parent $repositoryRoot) 'tools'
$netbeansTemp = Join-Path $toolsRoot 'netbeans-temp'
New-Item -ItemType Directory -Force -Path $netbeansTemp | Out-Null
$env:TEMP = $netbeansTemp
$env:TMP = $netbeansTemp
$javaInstalls = Join-Path $toolsRoot 'mise\installs\java'
$jdk25 = Get-ChildItem -LiteralPath $javaInstalls -Directory |
    Where-Object { $_.Name -match '^temurin-25\.\d+\.\d+\+' } |
    Sort-Object Name -Descending |
    Select-Object -First 1
$jdk21 = Get-ChildItem -LiteralPath $javaInstalls -Directory |
    Where-Object { $_.Name -match '^temurin-21\.\d+\.\d+\+' } |
    Sort-Object Name -Descending |
    Select-Object -First 1
$jdk21JetBrains = Get-ChildItem -LiteralPath $javaInstalls -Directory |
    Where-Object { $_.Name -match '^jetbrains-21\.\d+\.\d+-b\d+\.\d+$' } |
    Sort-Object Name -Descending |
    Select-Object -First 1

if (-not $jdk25 -or -not $jdk21) {
    throw "Se requieren los JDK 25 y 21 en $javaInstalls."
}

# Register the runtimes the active projects need: JDK 21 for Android Gradle
# and JDK 25 for Payara and Maven.
$netbeansPlatforms = Join-Path $env:APPDATA 'NetBeans\31\config\Services\Platforms\org-netbeans-api-java-Platform'
foreach ($platform in @(
    [pscustomobject]@{ FileName = 'Temurin_21.xml'; Name = 'Temurin JDK 21'; AntName = 'JDK_21'; Jdk = $jdk21; Version = '21' },
    [pscustomobject]@{ FileName = 'Temurin_25.xml'; Name = 'Temurin JDK 25'; AntName = 'JDK_25'; Jdk = $jdk25; Version = '25' }
)) {
    $platformPath = Join-Path $netbeansPlatforms $platform.FileName
    $platformUri = ([System.Uri]$platform.Jdk.FullName).AbsoluteUri.TrimEnd('/') + '/'
    $platformXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE platform PUBLIC "-//NetBeans//DTD Java PlatformDefinition 1.0//EN" "http://www.netbeans.org/dtds/java-platformdefinition-1_0.dtd">
<platform default="no" name="$($platform.Name)">
    <jdkhome>
        <resource>$platformUri</resource>
    </jdkhome>
    <properties>
        <property name="platform.ant.name" value="$($platform.AntName)"/>
    </properties>
    <sysproperties>
        <property name="java.specification.version" value="$($platform.Version)"/>
    </sysproperties>
</platform>
"@
    New-Item -ItemType Directory -Force -Path $netbeansPlatforms | Out-Null
    if (Test-Path -LiteralPath $platformPath) {
        $registeredPlatform = Get-Content -LiteralPath $platformPath -Raw
        if ($registeredPlatform -notmatch ('<property\s+name="platform\.ant\.name"\s+value="' + [regex]::Escape($platform.AntName) + '"') -or $registeredPlatform -notmatch [regex]::Escape($platformUri)) {
            throw "Ya existe una plataforma $($platform.AntName) distinta en NetBeans: $platformPath"
        }
    } else {
        [System.IO.File]::WriteAllText($platformPath, $platformXml, [System.Text.UTF8Encoding]::new($false))
    }
}

$netbeans = Join-Path $env:ProgramFiles 'Apache NetBeans\bin\netbeans64.exe'
$mavenBin = Join-Path $env:ProgramFiles 'Apache NetBeans\java\maven\bin'
$maven = Join-Path $mavenBin 'mvn.cmd'
$androidSdk = Join-Path $toolsRoot 'android-sdk'
$payaraHome = Join-Path $toolsRoot 'payara7'
$gradleHome = Join-Path $toolsRoot 'gradle-home'
$mavenRepository = Join-Path $toolsRoot 'maven-repo'

foreach ($requiredPath in @($netbeans, $maven, (Join-Path $androidSdk 'platform-tools\adb.exe'), (Join-Path $payaraHome 'bin\asadmin.bat'))) {
    if (-not (Test-Path -LiteralPath $requiredPath)) {
        throw "No se encontró una herramienta requerida: $requiredPath"
    }
}

$env:JAVA_HOME = $jdk25.FullName
$env:GRADLE_USER_HOME = $gradleHome
$env:ANDROID_HOME = $androidSdk
$env:ANDROID_SDK_ROOT = $androidSdk
$env:ANDROID_AVD_HOME = Join-Path $toolsRoot 'android-avd'
$env:PAYARA_HOME = $payaraHome
$env:PAYARA_JAVA_HOME = $jdk25.FullName
$env:AS_JAVA = $jdk25.FullName
if ([string]::IsNullOrWhiteSpace($env:PAYARA_DOMAIN)) {
    $env:PAYARA_DOMAIN = 'gr09-payara'
}

$env:PATH = (@(
    (Join-Path $jdk25.FullName 'bin'),
    $mavenBin,
    (Join-Path $androidSdk 'platform-tools'),
    (Join-Path $androidSdk 'emulator'),
    $env:PATH
) -join ';')

$mavenRepositoryArgument = '-Dmaven.repo.local="' + $mavenRepository + '"'
$env:MAVEN_OPTS = (@($env:MAVEN_OPTS, $mavenRepositoryArgument) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }) -join ' '

# El modelo Maven del editor no lee MAVEN_OPTS. Usa la misma caché para
# resolver los POM en NetBeans y para ejecutar Maven desde el IDE.
$mavenSettingsPath = Join-Path (Join-Path $env:APPDATA 'NetBeans\31\config') 'maven-settings.xml'
$mavenSettingsDirectory = Split-Path -Parent $mavenSettingsPath
New-Item -ItemType Directory -Force -Path $mavenSettingsDirectory, $mavenRepository | Out-Null
$baseMavenSettings = Join-Path $env:USERPROFILE '.m2\settings.xml'
$mavenSettings = New-Object System.Xml.XmlDocument
$mavenSettings.PreserveWhitespace = $true
if (Test-Path -LiteralPath $mavenSettingsPath) {
    $mavenSettings.Load($mavenSettingsPath)
} elseif (Test-Path -LiteralPath $baseMavenSettings) {
    $mavenSettings.Load($baseMavenSettings)
} else {
    $mavenSettings.LoadXml('<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"/>')
}
$localRepositoryElement = $mavenSettings.DocumentElement.SelectSingleNode('*[local-name()="localRepository"]')
if (-not $localRepositoryElement) {
    $localRepositoryElement = $mavenSettings.CreateElement('localRepository', $mavenSettings.DocumentElement.NamespaceURI)
    [void]$mavenSettings.DocumentElement.AppendChild($localRepositoryElement)
}
$localRepositoryElement.InnerText = $mavenRepository
$mavenSettings.Save($mavenSettingsPath)
$mavenPreferencesPath = Join-Path $env:APPDATA 'NetBeans\31\config\Preferences\org\netbeans\modules\maven.properties'
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $mavenPreferencesPath) | Out-Null
$mavenPreferences = @()
if (Test-Path -LiteralPath $mavenPreferencesPath) {
    $mavenPreferences = @(Get-Content -LiteralPath $mavenPreferencesPath)
}
$defaultOptionsLine = $mavenPreferences | Where-Object { $_ -match '^defaultOptions=' } | Select-Object -First 1
$defaultOptions = if ($defaultOptionsLine) { $defaultOptionsLine.Substring('defaultOptions='.Length) } else { '--no-transfer-progress' }
$defaultOptions = [regex]::Replace($defaultOptions, '(?:^|\s)-Dmaven\.repo\.local=(?:"[^"]*"|\S+)', '').Trim()
if (-not ('NetBeansPathShortener' -as [type])) {
    Add-Type -TypeDefinition @'
using System.Text;
using System.Runtime.InteropServices;
public static class NetBeansPathShortener {
    [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
    public static extern uint GetShortPathName(string longPath, StringBuilder shortPath, uint bufferLength);
}
'@
}
$shortRepositoryBuffer = New-Object System.Text.StringBuilder 1024
$shortRepositoryLength = [NetBeansPathShortener]::GetShortPathName($mavenRepository, $shortRepositoryBuffer, [uint32]$shortRepositoryBuffer.Capacity)
if ($shortRepositoryLength -eq 0 -or $shortRepositoryLength -ge $shortRepositoryBuffer.Capacity) {
    throw "No se pudo obtener una ruta Maven sin espacios: $mavenRepository"
}
$repositoryOption = '-Dmaven.repo.local=' + $shortRepositoryBuffer.ToString().Replace('\', '/')
$mavenPreferences = @($mavenPreferences | Where-Object { $_ -notmatch '^(defaultOptions|userSettingsXml)=' })
$mavenPreferences += 'defaultOptions=' + ((@($defaultOptions, $repositoryOption) | Where-Object { $_ }) -join ' ')
$mavenPreferences += 'userSettingsXml=' + $mavenSettingsPath.Replace('\', '/')
[System.IO.File]::WriteAllLines($mavenPreferencesPath, [string[]]$mavenPreferences, [System.Text.UTF8Encoding]::new($false))

New-Item -ItemType Directory -Force -Path $gradleHome, $env:ANDROID_AVD_HOME, $mavenRepository | Out-Null
$gradlePropertiesPath = Join-Path $gradleHome 'gradle.properties'
$gradleProperties = @()
if (Test-Path -LiteralPath $gradlePropertiesPath) {
    $gradleProperties = @(Get-Content -LiteralPath $gradlePropertiesPath)
}
$gradleJdkProperty = 'org.gradle.java.home=' + $jdk21.FullName.Replace('\', '/')
$gradleInstallPaths = @($jdk21.FullName)
if ($jdk21JetBrains) {
    $gradleInstallPaths += $jdk21JetBrains.FullName
}
$gradleInstallationsProperty = 'org.gradle.java.installations.paths=' + (($gradleInstallPaths | ForEach-Object { $_.Replace('\', '/') }) -join ',')
$javaHomeLine = $gradleProperties | Where-Object { $_ -match '^org\.gradle\.java\.home\s*=' } | Select-Object -First 1
if ($javaHomeLine) {
    $gradleProperties = @($gradleProperties | ForEach-Object {
        if ($_ -match '^org\.gradle\.java\.home\s*=') { $gradleJdkProperty } else { $_ }
    })
} else {
    $gradleProperties += $gradleJdkProperty
}
$installationsLine = $gradleProperties | Where-Object { $_ -match '^org\.gradle\.java\.installations\.paths\s*=' } | Select-Object -First 1
if ($installationsLine) {
    $gradleProperties = @($gradleProperties | ForEach-Object {
        if ($_ -match '^org\.gradle\.java\.installations\.paths\s*=') { $gradleInstallationsProperty } else { $_ }
    })
} else {
    $gradleProperties += $gradleInstallationsProperty
}
[System.IO.File]::WriteAllLines($gradlePropertiesPath, [string[]]$gradleProperties, [System.Text.UTF8Encoding]::new($false))

$sdkProperty = 'sdk.dir=' + $androidSdk.Replace('\', '/')
$androidProjects = @(
    'U1\TG.1\1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR09\04. CLIMOV\ClienteMovil',
    'U1\TG.1\1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR09\04. CLIMOV\ClienteMovil',
    'U1\TI.1.1\TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09\04. CLIMOV',
    'U1\TI.1.1\TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09\04. CLIMOV\ClienteMovil'
)
foreach ($relativePath in $androidProjects) {
    $projectPath = Join-Path $repositoryRoot $relativePath
    if (-not (Test-Path -LiteralPath $projectPath)) {
        throw "No se encontró el proyecto Android: $projectPath"
    }
    $localPropertiesPath = Join-Path $projectPath 'local.properties'
    $localProperties = @()
    if (Test-Path -LiteralPath $localPropertiesPath) {
        $localProperties = @(Get-Content -LiteralPath $localPropertiesPath)
    }
    $sdkLine = $localProperties | Where-Object { $_ -match '^sdk\.dir\s*=' } | Select-Object -First 1
    if ($sdkLine) {
        $localProperties = @($localProperties | ForEach-Object {
            if ($_ -match '^sdk\.dir\s*=') { $sdkProperty } else { $_ }
        })
    } else {
        $localProperties += $sdkProperty
    }
    [System.IO.File]::WriteAllLines($localPropertiesPath, [string[]]$localProperties, [System.Text.UTF8Encoding]::new($false))

    $gradlePropertiesPath = Join-Path $projectPath 'gradle.properties'
    $gradleProperties = @()
    if (Test-Path -LiteralPath $gradlePropertiesPath) {
        $gradleProperties = @(Get-Content -LiteralPath $gradlePropertiesPath)
    }
    $runtimeHint = 'netbeans.hint.jdkPlatform=JDK_21'
    $gradleProperties = @($gradleProperties | Where-Object { $_ -notmatch '^netbeans\.hint\.jdkPlatform\s*=' })
    $gradleProperties += $runtimeHint
    [System.IO.File]::WriteAllLines($gradlePropertiesPath, [string[]]$gradleProperties, [System.Text.UTF8Encoding]::new($false))
}

# Carga variables locales del proyecto bancario solo en este proceso; nunca las imprime ni las guarda en Git.
$bankEnvironmentFile = Join-Path $repositoryRoot 'U1\TG.1\.env'
if (Test-Path -LiteralPath $bankEnvironmentFile) {
    foreach ($line in [System.IO.File]::ReadAllLines($bankEnvironmentFile)) {
        if ($line -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*?)\s*$' -and -not $Matches[2].StartsWith('#')) {
            $name = $Matches[1]
            $value = $Matches[2]
            if ($value.Length -ge 2 -and (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'")))) {
                $value = $value.Substring(1, $value.Length - 2)
            }
            Set-Item -Path ("Env:$name") -Value $value
        }
    }
}

$mavenProjectRootRelativePaths = @(
    'U1\TG.1\1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR09',
    'U1\TG.1\1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR09',
    'U1\TI.1.1\TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09',
    'U1\TI.1.1\TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09'
)
$mavenProjectRelativePaths = @()
$repositoryPrefix = $repositoryRoot.TrimEnd([char]92) + [char]92
foreach ($relativeRoot in $mavenProjectRootRelativePaths) {
    $mavenRootPath = Join-Path $repositoryRoot $relativeRoot
    $rootPomPath = Join-Path $mavenRootPath 'pom.xml'
    if (-not (Test-Path -LiteralPath $rootPomPath)) {
        throw "No se encontró el POM agregador GR09: $rootPomPath"
    }

    $rootPom = [xml](Get-Content -LiteralPath $rootPomPath -Raw)
    $modules = @($rootPom.project.modules.module)
    if ($modules.Count -eq 0) {
        throw "El POM GR09 no declara módulos Maven: $rootPomPath"
    }

    $mavenProjectRelativePaths += $relativeRoot
    $mavenRootPrefix = $mavenRootPath.TrimEnd([char]92) + [char]92
    foreach ($module in $modules) {
        $modulePath = [System.IO.Path]::GetFullPath((Join-Path $mavenRootPath ([string]$module).Replace('/', [char]92)))
        if (-not $modulePath.StartsWith($mavenRootPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw "El módulo Maven sale de su agregador GR09: $modulePath"
        }

        $modulePomPath = Join-Path $modulePath 'pom.xml'
        if (-not (Test-Path -LiteralPath $modulePomPath)) {
            throw "No se encontró el POM del módulo Maven declarado por $rootPomPath : $modulePomPath"
        }
        $mavenProjectRelativePaths += $modulePath.Substring($repositoryPrefix.Length)
    }
}

$projectRelativePaths = @($mavenProjectRelativePaths + $androidProjects | Sort-Object -Unique)
$projectPaths = @($projectRelativePaths | ForEach-Object { Join-Path $repositoryRoot $_ })
foreach ($projectPath in $projectPaths) {
    if (-not (Test-Path -LiteralPath $projectPath)) {
        throw "No se encontró un proyecto GR09 que se iba a abrir: $projectPath"
    }
}

# F6 / Run Main Project apunta a un módulo ejecutable. Los POM agregadores no
# ejecutan una aplicación; este cliente Maven abre una ventana Swing al iniciar.
$mainProjectPath = Join-Path $repositoryRoot 'U1\TI.1.1\TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09\04. CLIESC\ClienteEscritorio'
if ($projectPaths -notcontains $mainProjectPath -or -not (Test-Path -LiteralPath (Join-Path $mainProjectPath 'nbactions.xml'))) {
    throw "El proyecto Maven principal no está incluido o no define Run Project: $mainProjectPath"
}
$mainProjectUrl = ([System.Uri]($mainProjectPath.TrimEnd([char]92) + [char]92)).AbsoluteUri -replace '^file:///', 'file:/'
$projectUiPreferencesPath = Join-Path $env:APPDATA 'NetBeans\31\config\Preferences\org\netbeans\modules\projectui.properties'
$projectUiPreferencesDirectory = Split-Path -Parent $projectUiPreferencesPath
New-Item -ItemType Directory -Force -Path $projectUiPreferencesDirectory | Out-Null
$projectUiPreferences = @()
if (Test-Path -LiteralPath $projectUiPreferencesPath) {
    $projectUiPreferences = @(Get-Content -LiteralPath $projectUiPreferencesPath)
}
$projectUiPreferences = @($projectUiPreferences | Where-Object { $_ -notmatch '^mainProjectURL=' })
$projectUiPreferences += 'mainProjectURL=' + $mainProjectUrl
[System.IO.File]::WriteAllLines($projectUiPreferencesPath, [string[]]$projectUiPreferences, [System.Text.UTF8Encoding]::new($false))

$quotedPaths = @($projectPaths | ForEach-Object { '"' + $_.Replace('"', '\"') + '"' })
$argumentLine = '--jdkhome "' + $jdk25.FullName + '" --open ' + ($quotedPaths -join ' ')
Start-Process -FilePath $netbeans -ArgumentList $argumentLine -WindowStyle Normal | Out-Null
$mavenProjectCount = $mavenProjectRelativePaths.Count
$androidProjectCount = $androidProjects.Count
Write-Output ("Se solicitó a NetBeans abrir {0} proyectos activos de GR09: {1} Maven y {2} Android. Proyecto principal: ClienteEscritorio REST sin BDD." -f $projectPaths.Count, $mavenProjectCount, $androidProjectCount)
