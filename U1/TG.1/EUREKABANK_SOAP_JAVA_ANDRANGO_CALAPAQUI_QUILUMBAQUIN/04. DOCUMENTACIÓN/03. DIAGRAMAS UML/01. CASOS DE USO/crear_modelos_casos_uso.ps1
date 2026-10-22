param(
    [Parameter(Mandatory)][ValidateSet('consola','escritorio','movil','web')][string]$Cliente,
    [switch]$Force
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
if ([Environment]::Is64BitProcess) {
    throw 'Ejecute este archivo con Windows PowerShell de 32 bits.'
}

$pd = 'C:\Program Files (x86)\Sybase\PowerDesigner 16'
$prefix = switch ($Cliente) { consola { 'Consola' } escritorio { 'Escritorio' } movil { 'Movil' } web { 'Web' } }
$folder = $PSScriptRoot
$modelPath = Join-Path $folder ("EurekaBank_{0}_CasosUso.oom" -f $prefix)
$imagePath = Join-Path $folder ("EurekaBank_{0}_CasosUso.png" -f $prefix)
foreach ($path in @($modelPath,$imagePath)) {
    if (Test-Path -LiteralPath $path) {
        if (-not $Force) { throw "Ya existe '$path'. Use -Force para regenerarlo." }
        Remove-Item -LiteralPath $path -Force
    }
}

$definitions = @{
    consola = @{
        title='Cliente de consola EurekaBank'; actor='Operador'; secondary='Servicio SOAP EurekaBank'
        description='Cliente Java de consola para iniciar sesión, consultar movimientos, registrar depósitos y salir.'
        cases=@(
            @('CU01','Iniciar sesión','Solicita credenciales y consulta login al servicio SOAP.'),
            @('CU02','Consultar movimientos','Solicita una cuenta y presenta sus movimientos.'),
            @('CU03','Registrar depósito','Solicita cuenta e importe e informa el entero devuelto.'),
            @('CU04','Salir','Finaliza el menú local.'),
            @('CU05','Verificar credenciales','Invoca la operación login del WSDL.'),
            @('CU06','Obtener movimientos','Invoca traerMovimientos y convierte la fecha XML.'),
            @('CU07','Enviar depósito','Invoca regDeposito con cuenta e importe.')
        )
        includes=@(@('CU01','CU05'),@('CU02','CU06'),@('CU03','CU07'))
        user=@('CU01','CU02','CU03','CU04'); service=@('CU05','CU06','CU07')
    }
    escritorio = @{
        title='Cliente de escritorio EurekaBank'; actor='Operador'; secondary='Servicio SOAP EurekaBank'
        description='Aplicación Java Swing para iniciar sesión, consultar movimientos, registrar depósitos y salir.'
        cases=@(
            @('CU01','Iniciar sesión','Solicita usuario y contraseña e invoca login SOAP.'),
            @('CU02','Abrir menú','Presenta accesos a consulta de movimientos y depósitos tras aceptar el login.'),
            @('CU03','Consultar movimientos','Solicita una cuenta y presenta los movimientos en una tabla.'),
            @('CU04','Registrar depósito','Solicita cuenta e importe e informa el resultado entero retornado.'),
            @('CU05','Cerrar ventana','Finaliza la ventana seleccionada.'),
            @('CU06','Validar credenciales','Invoca la operación login publicada por SOAP.'),
            @('CU07','Obtener movimientos','Invoca traerMovimientos y transforma fechas XML.'),
            @('CU08','Enviar depósito','Invoca regDeposito con cuenta e importe.')
        )
        includes=@(@('CU01','CU06'),@('CU03','CU07'),@('CU04','CU08'))
        user=@('CU01','CU02','CU03','CU04','CU05'); service=@('CU06','CU07','CU08')
    }
    movil = @{
        title='Cliente móvil EurekaBank'; actor='Usuario móvil'; secondary='Servicio SOAP EurekaBank'
        description='Aplicación Android que incluye pantallas y adaptadores SOAP para acceso móvil a EurekaBank.'
        cases=@(
            @('CU01','Abrir la aplicación','Presenta el formulario de inicio definido en los recursos Android.'),
            @('CU02','Acceder al menú','Muestra opciones de depósito y movimientos; navegación pendiente de completar.'),
            @('CU03','Consultar movimientos','El adaptador kSOAP solicita y transforma la respuesta de movimientos.'),
            @('CU04','Registrar depósito','El adaptador kSOAP envía cuenta e importe.'),
            @('CU05','Validar credenciales','El adaptador móvil envía usuario y contraseña al login SOAP.'),
            @('CU06','Recibir resultado remoto','Interpreta la respuesta del servicio; la integración con pantallas está pendiente.')
        )
        includes=@(@('CU01','CU05'),@('CU03','CU06'),@('CU04','CU06'))
        user=@('CU01','CU02','CU03','CU04'); service=@('CU05','CU06')
    }
    web = @{
        title='Cliente web EurekaBank'; actor='Operador'; secondary='Servicio SOAP EurekaBank'
        description='Interfaz web con formularios para login, consulta de movimientos y depósito mediante peticiones SOAP.'
        cases=@(
            @('CU01','Iniciar sesión','Envía usuario y contraseña desde la página de entrada.'),
            @('CU02','Consultar movimientos','Consulta una cuenta y presenta sus registros en una tabla.'),
            @('CU03','Registrar depósito por SOAP','Envía cuenta e importe al endpoint SOAP y presenta el resultado.'),
            @('CU04','Navegar el menú','Abre las vistas de movimientos o depósito.'),
            @('CU05','Registrar depósito por servlet','Usa una ruta local del WAR y JDBC, independiente del depósito SOAP.'),
            @('CU06','Validar credenciales','Invoca la operación login.'),
            @('CU07','Obtener movimientos','Invoca traerMovimientos.'),
            @('CU08','Procesar depósito SOAP','Invoca regDeposito con el contrato remoto.')
        )
        includes=@(@('CU01','CU06'),@('CU02','CU07'),@('CU03','CU08'))
        user=@('CU01','CU02','CU03','CU04','CU05'); service=@('CU06','CU07','CU08')
    }
}
$definition = $definitions[$Cliente]
Add-Type -Path (Join-Path $pd 'Interop.PdCommon.dll')
$app = New-Object -ComObject PowerDesigner.Application
$app.ShowMode = $false
$model = $app.CreateModel(403775584, '|Diagram=UseCaseDiagram', 0)
$model.Name = "EurekaBank - $($definition.title)"
$model.Code = "EUREKABANK_CLIENTE_$($prefix.ToUpperInvariant())"
$model.Description = $definition.description
$diagram = $model.UseCaseDiagrams.Item(0)
$diagram.Name = "Casos de uso - $($definition.title)"
$diagram.Code = "CU_EUREKABANK_$($prefix.ToUpperInvariant())"
$diagram.Comment = $definition.description

function Attach-Object($object) { $diagram.AttachObject($object) | Out-Null }
function Add-Actor([string]$code,[string]$name,[string]$description) {
    $actor = $model.Actors.CreateNew(); $actor.Code=$code; $actor.Name=$name; $actor.Description=$description
    Attach-Object $actor; return $actor
}
function Add-UseCase([string]$code,[string]$name,[string]$description) {
    $case = $model.UseCases.CreateNew(); $case.Code=$code; $case.Name=$name; $case.Description=$description
    Attach-Object $case; return $case
}
function Add-Association($from,$to) {
    $link=$model.UseCaseAssociations.CreateNew(); $link.Object1=$from; $link.Object2=$to
    $diagram.AttachLinkObject($link,$from,$to) | Out-Null
}
function Find-UseCase([string]$code) {
    foreach ($case in $model.UseCases) { if ($case.Code -eq $code) { return $case } }
    throw "No se encontró el caso de uso $code"
}
function Add-Include([string]$fromCode,[string]$toCode) {
    $from=Find-UseCase $fromCode; $to=Find-UseCase $toCode
    $link=$model.Dependencies.CreateNew(); $link.Object1=$from; $link.Object2=$to; $link.Stereotype='include'
    $diagram.AttachLinkObject($link,$from,$to) | Out-Null
}

$user=Add-Actor 'ACTOR_USUARIO' $definition.actor 'Persona que opera el cliente de EurekaBank.'
$service=Add-Actor 'ACTOR_SOAP' $definition.secondary 'Servicio remoto publicado en Payara Server Community 7.'
foreach ($caseDef in $definition.cases) { [void](Add-UseCase $caseDef[0] $caseDef[1] $caseDef[2]) }
foreach ($code in $definition.user) { Add-Association $user (Find-UseCase $code) }
foreach ($code in $definition.service) { Add-Association $service (Find-UseCase $code) }
foreach ($pair in $definition.includes) { Add-Include $pair[0] $pair[1] }
$diagram.AutoLayout() | Out-Null
$model.Save($modelPath,$false,'')
$model.Close($false)
$layoutScript = Join-Path $PSScriptRoot 'ajustar_diagrama_clientes.vbs'
$cscript = Join-Path $env:WINDIR 'SysWOW64\cscript.exe'
& $cscript //NoLogo $layoutScript $modelPath $imagePath $Cliente
if ($LASTEXITCODE -ne 0) { throw "El ajuste PowerDesigner falló con código $LASTEXITCODE." }
Write-Output "Modelo PowerDesigner: $modelPath"
Write-Output "Imagen exportada: $imagePath"
