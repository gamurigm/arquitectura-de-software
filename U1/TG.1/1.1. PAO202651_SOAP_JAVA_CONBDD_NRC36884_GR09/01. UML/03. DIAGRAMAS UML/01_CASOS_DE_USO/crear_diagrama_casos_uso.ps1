param(
    [switch] $Force
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

if ([Environment]::Is64BitProcess) {
    throw 'Ejecute este archivo con Windows PowerShell de 32 bits: %WINDIR%\SysWOW64\WindowsPowerShell\v1.0\powershell.exe'
}

$powerDesignerPath = 'C:\Program Files (x86)\Sybase\PowerDesigner 16'
$modelPath = Join-Path $PSScriptRoot 'GR09_CasosUso_EurekaBank.oom'
$imagePath = Join-Path $PSScriptRoot 'GR09_CasosUso_EurekaBank.png'

foreach ($path in @($modelPath, $imagePath)) {
    if (Test-Path -LiteralPath $path) {
        if (-not $Force) {
            throw "Ya existe '$path'. Use -Force para regenerar el modelo y la imagen."
        }
        Remove-Item -LiteralPath $path -Force
    }
}

Add-Type -Path (Join-Path $powerDesignerPath 'Interop.PdCommon.dll')
$powerDesigner = New-Object -ComObject PowerDesigner.Application
$powerDesigner.ShowMode = $false
$model = $powerDesigner.CreateModel(403775584, '|Diagram=UseCaseDiagram', 0)
$model.Name = 'EurekaBank SOAP Java - Casos de uso - Grupo 09'
$model.Code = 'EUREKABANK_CASOS_USO_GR09'
$model.Description = 'Modelo UML de casos de uso basado en el servicio EurekaBank SOAP Java con MySQL.'
$diagram = $model.UseCaseDiagrams.Item(0)
$diagram.Name = 'Diagrama de casos de uso - EurekaBank'
$diagram.Code = 'CU_EUREKABANK_GR09'
$diagram.Comment = 'Operaciones incluidas en la línea base del grupo 09.'

function Add-DiagramObject($object) {
    $diagram.AttachObject($object) | Out-Null
}

function New-UseCase([string] $code, [string] $name, [string] $description, [string] $pre, [string] $post) {
    $useCase = $model.UseCases.CreateNew()
    $useCase.Code = $code
    $useCase.Name = $name
    $useCase.Description = $description
    $useCase.PreCondition = $pre
    $useCase.PostCondition = $post
    Add-DiagramObject $useCase
    return $useCase
}

function New-Association($from, $to, [string] $name) {
    $link = $model.UseCaseAssociations.CreateNew()
    $link.Name = $name
    $link.Object1 = $from
    $link.Object2 = $to
    $diagram.AttachLinkObject($link, $from, $to) | Out-Null
}

function New-Include($from, $to, [string] $name) {
    $link = $model.Dependencies.CreateNew()
    $link.Name = $name
    $link.Object1 = $from
    $link.Object2 = $to
    $link.Stereotype = 'include'
    $diagram.AttachLinkObject($link, $from, $to) | Out-Null
}

$actor = $model.Actors.CreateNew()
$actor.Code = 'USUARIO_AUTORIZADO'
$actor.Name = 'Usuario autorizado'
$actor.Description = 'Empleado u operador que utiliza el cliente EurekaBank.'
Add-DiagramObject $actor

$caseLogin = New-UseCase 'CU01' 'CU-01 Iniciar sesión' 'Valida las credenciales y habilita el flujo local del cliente.' 'Servicio SOAP y base de datos disponibles.' 'El cliente presenta el menú si la operación devuelve 1; no se crea una sesión remota.'
$caseMovements = New-UseCase 'CU02' 'CU-02 Consultar movimientos' 'Consulta y presenta los movimientos asociados a una cuenta.' 'El usuario llegó al menú del cliente.' 'Se muestran movimientos ordenados o una lista vacía.'
$caseDeposit = New-UseCase 'CU03' 'CU-03 Registrar depósito' 'Acredita un importe en una cuenta activa y registra el movimiento.' 'El usuario llegó al menú; la cuenta está activa.' 'Saldo y movimiento quedan confirmados en una transacción.'
$caseLogout = New-UseCase 'CU04' 'CU-04 Cerrar sesión local' 'Sale del menú y retorna al ingreso o finaliza el cliente.' 'El usuario está en el menú local.' 'El cliente elimina su estado local; no se llama al servidor.'

$caseCredentials = New-UseCase 'CU05' 'CU-05 Validar credenciales' 'Comprueba las entradas y verifica el hash almacenado.' 'Se recibieron usuario y contraseña.' 'La credencial se acepta o se rechaza.'
$caseAccount = New-UseCase 'CU06' 'CU-06 Validar cuenta' 'Rechaza una cuenta vacía o de más de ocho caracteres.' 'Se recibió un identificador de cuenta.' 'La cuenta puede consultarse o procesarse.'
$caseAmount = New-UseCase 'CU07' 'CU-07 Validar datos del depósito' 'Exige importe positivo, exacto a centavos y de precisión admitida.' 'Se recibió un importe.' 'Los datos cumplen las reglas del servicio.'
$caseApply = New-UseCase 'CU08' 'CU-08 Aplicar depósito' 'Actualiza saldo y contador, inserta el movimiento y confirma la transacción.' 'La cuenta existe y está activa.' 'El depósito se confirma por completo o se revierte.'

New-Association $actor $caseLogin 'Usuario - iniciar sesión'
New-Association $actor $caseMovements 'Usuario - consultar movimientos'
New-Association $actor $caseDeposit 'Usuario - registrar depósito'
New-Association $actor $caseLogout 'Usuario - cerrar sesión local'
New-Include $caseLogin $caseCredentials 'Iniciar sesión incluye validar credenciales'
New-Include $caseMovements $caseAccount 'Consultar incluye validar cuenta'
New-Include $caseDeposit $caseAccount 'Depositar incluye validar cuenta'
New-Include $caseDeposit $caseAmount 'Depositar incluye validar importe'
New-Include $caseDeposit $caseApply 'Depositar incluye aplicar depósito'

$diagram.AutoLayout() | Out-Null
$model.Save($modelPath, $false, '')

$layoutScript = Join-Path $PSScriptRoot 'ajustar_diagrama_casos_uso.vbs'
$cscript = Join-Path $env:WINDIR 'SysWOW64\cscript.exe'
& $cscript //NoLogo $layoutScript $modelPath $imagePath
if ($LASTEXITCODE -ne 0) {
    throw "El ajuste o la exportación del diagrama en PowerDesigner falló con código $LASTEXITCODE."
}

Write-Output "Modelo PowerDesigner: $modelPath"
Write-Output "Diagrama exportado: $imagePath"
