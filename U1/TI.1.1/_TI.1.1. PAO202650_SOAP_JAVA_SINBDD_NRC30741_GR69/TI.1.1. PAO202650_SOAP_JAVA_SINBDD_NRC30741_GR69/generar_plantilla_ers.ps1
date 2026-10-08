[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateNotNullOrEmpty()]
    [string]$OutputPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$templatePath = Join-Path $PSScriptRoot '01. UML\01. ERS\ERS_SOAP_JAVA_SINBDD_GR69_PLANTILLA.docx'
if (-not (Test-Path -LiteralPath $templatePath -PathType Leaf)) {
    throw "No se encontró la plantilla Word: $templatePath"
}

$outputFullPath = [IO.Path]::GetFullPath($OutputPath)
if ([IO.Path]::GetExtension($outputFullPath) -ine '.docx') {
    throw 'OutputPath debe terminar en .docx.'
}
if ([IO.Directory]::Exists($outputFullPath)) {
    throw 'OutputPath debe ser una ruta de archivo .docx.'
}
$outputDirectory = [IO.Path]::GetDirectoryName($outputFullPath)
if (-not (Test-Path -LiteralPath $outputDirectory -PathType Container)) {
    throw "No existe el directorio de salida: $outputDirectory"
}
if ([string]::Equals([IO.Path]::GetFullPath($templatePath), $outputFullPath, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'OutputPath no puede apuntar al archivo de plantilla.'
}
if (Test-Path -LiteralPath $outputFullPath -PathType Leaf) {
    throw "El archivo de salida ya existe y no se sobrescribirá: $outputFullPath"
}

Copy-Item -LiteralPath $templatePath -Destination $outputFullPath
Write-Output "Plantilla generada: $outputFullPath"