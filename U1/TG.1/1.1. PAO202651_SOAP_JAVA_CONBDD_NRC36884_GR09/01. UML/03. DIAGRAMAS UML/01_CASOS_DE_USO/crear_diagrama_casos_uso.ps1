param(
    [switch] $Force
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

if ([Environment]::Is64BitProcess) {
    throw 'Ejecute este archivo con Windows PowerShell de 32 bits: %WINDIR%\SysWOW64\WindowsPowerShell\v1.0\powershell.exe'
}

$modelPath = Join-Path $PSScriptRoot 'GR09_CasosUso_EurekaBank.oom'
$imagePath = Join-Path $PSScriptRoot 'GR09_CasosUso_EurekaBank.png'
if ((Test-Path -LiteralPath $modelPath) -or (Test-Path -LiteralPath $imagePath)) {
    if (-not $Force) { throw 'Ya existe el modelo o su exportación. Use -Force para actualizarlos.' }
}
if (-not (Test-Path -LiteralPath $modelPath -PathType Leaf)) {
    throw "No se encontró el modelo PowerDesigner existente: $modelPath"
}

$caseSpecs = @(
    @{ Source = 'CU01'; Code = 'CU01'; Name = 'CU-01 Iniciar sesión'; Description = 'Valida credenciales y crea un token temporal asociado al cliente.'; Pre = 'Servicio SOAP y autenticación disponibles; usuario vinculado a un cliente.'; Post = 'El cliente conserva un token vigente o informa el rechazo.' },
    @{ Source = $null; Code = 'CU02'; Name = 'CU-02 Consultar cuentas'; Description = 'Presenta las cuentas activas del cliente autenticado.'; Pre = 'El usuario tiene una sesión vigente.'; Post = 'Se muestran las cuentas activas del cliente o una lista vacía.' },
    @{ Source = 'CU02'; Code = 'CU03'; Name = 'CU-03 Consultar movimientos'; Description = 'Consulta el historial de una cuenta activa del cliente autenticado.'; Pre = 'Existe una sesión vigente y la cuenta pertenece al cliente.'; Post = 'Se muestran movimientos en orden descendente o una lista vacía.' },
    @{ Source = 'CU03'; Code = 'CU04'; Name = 'CU-04 Registrar depósito'; Description = 'Acredita un importe válido y registra el movimiento de depósito en una transacción.'; Pre = 'Token vigente; cuenta activa del cliente; importe válido.'; Post = 'Saldo y movimiento quedan confirmados juntos o se revierte la operación.' },
    @{ Source = $null; Code = 'CU05'; Name = 'CU-05 Transferir fondos'; Description = 'Debita origen y acredita destino de forma atómica, con clave de idempotencia.'; Pre = 'Token vigente; cuentas activas, misma moneda; origen del cliente y con fondos.'; Post = 'Dos saldos y movimientos quedan confirmados juntos; una repetición idéntica no duplica.' },
    @{ Source = 'CU04'; Code = 'CU06'; Name = 'CU-06 Cerrar sesión'; Description = 'Revoca en el servidor el token utilizado por el cliente.'; Pre = 'El cliente conserva un token de sesión.'; Post = 'La sesión se elimina y el cliente vuelve al ingreso o termina.' },
    @{ Source = 'CU05'; Code = 'CU07'; Name = 'CU-07 Validar credenciales'; Description = 'Comprueba entradas, hash PBKDF2 y asociación del usuario con un cliente.'; Pre = 'Se recibieron usuario y contraseña.'; Post = 'Las credenciales se aceptan o rechazan; no se emite sesión si no hay cliente asociado.' },
    @{ Source = 'CU06'; Code = 'CU08'; Name = 'CU-08 Validar cuenta'; Description = 'Comprueba cuenta, estado ACTIVO y titularidad según la operación.'; Pre = 'Se recibió un identificador de cuenta.'; Post = 'La cuenta cumple las reglas de acceso y estado.' },
    @{ Source = 'CU07'; Code = 'CU09'; Name = 'CU-09 Validar datos del depósito'; Description = 'Exige importe positivo, exacto a centavos y precisión admitida.'; Pre = 'Se recibió un importe de depósito.'; Post = 'Los datos cumplen las reglas del servicio.' },
    @{ Source = 'CU08'; Code = 'CU10'; Name = 'CU-10 Aplicar depósito'; Description = 'Actualiza saldo y contador, inserta movimiento tipo 003 y confirma la transacción.'; Pre = 'La cuenta propia está activa y el importe es válido.'; Post = 'El depósito queda confirmado por completo o se revierte.' }
)

function Find-UseCase([string] $code) {
    foreach ($item in $model.UseCases) {
        if ($item.Code -eq $code) { return $item }
    }
    return $null
}

function New-UseCase([hashtable] $spec) {
    $item = $model.UseCases.CreateNew()
    $item.Code = $spec.Code
    $item.Name = $spec.Name
    $item.Description = $spec.Description
    $item.PreCondition = $spec.Pre
    $item.PostCondition = $spec.Post
    $diagram.AttachObject($item) | Out-Null
    return $item
}

function Set-UseCase($item, [hashtable] $spec) {
    $item.Code = $spec.Code
    $item.Name = $spec.Name
    $item.Description = $spec.Description
    $item.PreCondition = $spec.Pre
    $item.PostCondition = $spec.Post
}

function Ensure-Association($from, $to, [string] $name) {
    foreach ($link in $model.UseCaseAssociations) {
        if ($link.Object1.Code -eq $from.Code -and $link.Object2.Code -eq $to.Code) {
            $link.Name = $name
            return
        }
    }
    $link = $model.UseCaseAssociations.CreateNew()
    $link.Name = $name
    $link.Object1 = $from
    $link.Object2 = $to
    $diagram.AttachLinkObject($link, $from, $to) | Out-Null
}

function Ensure-Include($from, $to, [string] $name) {
    foreach ($link in $model.Dependencies) {
        if ($link.Object1.Code -eq $from.Code -and $link.Object2.Code -eq $to.Code -and $link.Stereotype -eq 'include') {
            $link.Name = $name
            return
        }
    }
    $link = $model.Dependencies.CreateNew()
    $link.Name = $name
    $link.Object1 = $from
    $link.Object2 = $to
    $link.Stereotype = 'include'
    $diagram.AttachLinkObject($link, $from, $to) | Out-Null
}

$powerDesigner = New-Object -ComObject PowerDesigner.Application
$powerDesigner.ShowMode = $false
$model = $powerDesigner.OpenModel($modelPath, 0)
$diagram = $model.UseCaseDiagrams.Item(0)
$diagram.Name = 'Diagrama de casos de uso - EurekaBank'
$diagram.Code = 'CU_EUREKABANK_GR09'
$diagram.Comment = 'Línea base con seis objetivos de usuario, token temporal y transferencia idempotente.'
$actor = $model.Actors.Item(0)
$actor.Name = 'Usuario'
$actor.Description = 'Persona autenticada que utiliza cualquiera de los cuatro clientes EurekaBank.'

$versioned = $model.Description -like '*USE_CASES_V2*'
if ($versioned) {
    $oldSession = Find-UseCase 'CU08'
    if ($null -ne $oldSession -and $oldSession.Name -eq 'CU-08 Validar sesión') {
        $oldAccount = Find-UseCase 'CU09'
        $oldDepositData = Find-UseCase 'CU10'
        $oldApplyDeposit = Find-UseCase 'CU11'
        foreach ($link in @($model.Dependencies)) {
            if ($link.Object1.Code -eq 'CU08' -or $link.Object2.Code -eq 'CU08') { $link.Delete() }
        }
        $oldAccount.Code = 'TMP_CU09'
        $oldDepositData.Code = 'TMP_CU10'
        $oldApplyDeposit.Code = 'TMP_CU11'
        $oldSession.Delete()
        $oldAccount.Code = 'CU08'
        $oldDepositData.Code = 'CU09'
        $oldApplyDeposit.Code = 'CU10'
    }
}
$bySource = @{}
if (-not $versioned) {
    foreach ($item in $model.UseCases) { $bySource[$item.Code] = $item }
    foreach ($spec in $caseSpecs | Where-Object { $null -ne $_.Source }) {
        if (-not $bySource.ContainsKey($spec.Source)) { throw "Falta el caso existente $($spec.Source); no se modificó el modelo." }
    }
    foreach ($item in $model.UseCases) { $item.Code = ('TMP_' + $item.Code) }
    foreach ($spec in $caseSpecs | Where-Object { $null -ne $_.Source }) {
        Set-UseCase $bySource[$spec.Source] $spec
    }
}
foreach ($spec in $caseSpecs) {
    $item = Find-UseCase $spec.Code
    if ($null -eq $item) {
        $item = New-UseCase $spec
    }
    else {
        Set-UseCase $item $spec
    }
}
$model.Description = 'Modelo UML de casos de uso de EurekaBank SOAP Java, Grupo 09. USE_CASES_V2'
$caseByCode = @{}
foreach ($item in $model.UseCases) { $caseByCode[$item.Code] = $item }

$primaryNames = @{
    'CU01' = 'Usuario - iniciar sesión'
    'CU02' = 'Usuario - consultar cuentas'
    'CU03' = 'Usuario - consultar movimientos'
    'CU04' = 'Usuario - registrar depósito'
    'CU05' = 'Usuario - transferir fondos'
    'CU06' = 'Usuario - cerrar sesión'
}
foreach ($code in $primaryNames.Keys) { Ensure-Association $actor $caseByCode[$code] $primaryNames[$code] }

$includeSpecs = @(
    @{ From = 'CU01'; To = 'CU07'; Name = 'Iniciar sesión incluye validar credenciales' },
    @{ From = 'CU03'; To = 'CU08'; Name = 'Consultar movimientos incluye validar cuenta' },
    @{ From = 'CU04'; To = 'CU08'; Name = 'Depositar incluye validar cuenta' },
    @{ From = 'CU04'; To = 'CU09'; Name = 'Depositar incluye validar importe' },
    @{ From = 'CU04'; To = 'CU10'; Name = 'Depositar incluye aplicar depósito' },
    @{ From = 'CU05'; To = 'CU08'; Name = 'Transferir incluye validar cuentas' }
)
$desiredIncludePairs = @{}
foreach ($spec in $includeSpecs) { $desiredIncludePairs[($spec.From + '|' + $spec.To)] = $true }
foreach ($link in @($model.Dependencies)) {
    if ($link.Stereotype -eq 'include') {
        $pair = $link.Object1.Code + '|' + $link.Object2.Code
        if (-not $desiredIncludePairs.ContainsKey($pair)) { $link.Delete() }
    }
}
foreach ($spec in $includeSpecs) {
    Ensure-Include $caseByCode[$spec.From] $caseByCode[$spec.To] $spec.Name
}

$diagram.AutoLayout() | Out-Null
$model.Save($modelPath, $false, '')
$model.Close($false)
$null = [Runtime.InteropServices.Marshal]::ReleaseComObject($model)
# Keep the active PowerDesigner automation process available to the layout script.
$layoutScript = Join-Path $PSScriptRoot 'ajustar_diagrama_casos_uso.vbs'
$cscript = Join-Path $env:WINDIR 'SysWOW64\cscript.exe'
& $cscript //NoLogo $layoutScript $modelPath $imagePath
if ($LASTEXITCODE -ne 0) {
    throw "El ajuste o la exportación del diagrama en PowerDesigner falló con código $LASTEXITCODE."
}
$null = [Runtime.InteropServices.Marshal]::ReleaseComObject($powerDesigner)
Write-Output "Modelo PowerDesigner actualizado sin reconstruir su identidad: $modelPath"
Write-Output "Diagrama exportado: $imagePath"