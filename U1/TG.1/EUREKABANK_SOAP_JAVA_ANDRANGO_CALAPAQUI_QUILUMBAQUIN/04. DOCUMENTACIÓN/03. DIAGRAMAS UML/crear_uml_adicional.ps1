param(
    [Parameter(Mandatory)][ValidateSet('consola','escritorio','movil','web')][string]$Cliente,
    [switch]$Force
)

$ErrorActionPreference = 'Stop'
trap { Write-Error $_ -ErrorAction Continue; break }
if ([Environment]::Is64BitProcess) { throw 'Ejecute con Windows PowerShell de 32 bits.' }

$base = $PSScriptRoot
$useCaseDir = Join-Path $base '01. CASOS DE USO'
$prefix = switch ($Cliente) { consola { 'Consola' } escritorio { 'Escritorio' } movil { 'Movil' } web { 'Web' } }
$modelPath = Join-Path $useCaseDir ("EurekaBank_{0}_CasosUso.oom" -f $prefix)
if (-not (Test-Path -LiteralPath $modelPath)) { throw "Falta el modelo de casos de uso: $modelPath" }

$activityDir = Join-Path $base '02. ACTIVIDAD'
$sequenceDir = Join-Path $base '03. SECUENCIA'
$classDir = Join-Path $base '04. CLASES'
$deploymentDir = Join-Path $base '05. ARQUITECTURA'
foreach ($dir in @($activityDir,$sequenceDir,$classDir,$deploymentDir)) { [void](New-Item -ItemType Directory -Path $dir -Force) }
$outputs = @(
    (Join-Path $activityDir ("EurekaBank_{0}_Actividad.oom" -f $prefix)),
    (Join-Path $activityDir ("EurekaBank_{0}_Actividad.png" -f $prefix)),
    (Join-Path $sequenceDir ("EurekaBank_{0}_Secuencia.oom" -f $prefix)),
    (Join-Path $sequenceDir ("EurekaBank_{0}_Secuencia.png" -f $prefix)),
    (Join-Path $classDir ("EurekaBank_{0}_Clases.oom" -f $prefix)),
    (Join-Path $classDir ("EurekaBank_{0}_Clases.png" -f $prefix)),
    (Join-Path $deploymentDir ("EurekaBank_{0}_Despliegue.oom" -f $prefix)),
    (Join-Path $deploymentDir ("EurekaBank_{0}_Despliegue.png" -f $prefix))
)
foreach ($path in $outputs) {
    if (Test-Path -LiteralPath $path) {
        if (-not $Force) { throw "Ya existe '$path'. Use -Force para regenerarlo." }
        Remove-Item -LiteralPath $path -Force
    }
}

$flowsByClient = @{
    consola = @('Iniciar cliente','Ingresar credenciales','Invocar login SOAP','Validar resultado login','Abrir menú','Seleccionar operación','Invocar consulta o depósito','Mostrar respuesta','Salir')
    escritorio = @('Abrir ventana de acceso','Ingresar credenciales','Invocar login SOAP','Validar resultado login','Mostrar menú Swing','Abrir movimientos o depósitos','Invocar operación SOAP','Presentar resultado','Cerrar ventana')
    movil = @('Abrir app Android','Ingresar credenciales','Invocar login kSOAP','Validar resultado login','Abrir menú móvil','Elegir movimientos o depósito','Invocar operación kSOAP','Mostrar respuesta','Cerrar app')
    web = @('Abrir página web','Ingresar credenciales','Enviar petición login SOAP','Validar resultado login','Abrir menú','Elegir movimientos o depósito','Enviar consulta o depósito SOAP','Mostrar respuesta en página','Finalizar navegación')
}
$classesByClient = @{
    consola = @(@('WSEurekaClienteConsola','Coordina el menú y la entrada estándar'),@('EurekaService','Invoca operaciones del proxy JAX-WS'),@('Login','Interpreta el resultado de autenticación'),@('Movimiento','Representa el movimiento recibido'))
    escritorio = @(@('LoginVista','Formulario Swing de autenticación'),@('MenuVista','Navegación a consulta y depósito'),@('EurekaControlador','Delegación de operaciones al servicio'),@('EurekaService','Invocaciones JAX-WS a EurekaBank'))
    movil = @(@('Menu','Activity con navegación declarada parcialmente'),@('DepositosSOAP','Adaptador kSOAP de depósito'),@('MovimientosSOAP','Adaptador kSOAP de consulta'),@('EurekaBankService','Contrato remoto esperado'))
    web = @(@('DepositoServlet','Ruta POST local del WAR'),@('EurekaControlador','Controlador usado por la ruta servlet'),@('EurekaService','Persistencia JDBC de la ruta servlet'),@('AccesoDB','Conexión MySQL usada por EurekaService'))
}
$app = New-Object -ComObject PowerDesigner.Application
$app.ShowMode = $false
$model = $null
try {
    $model = $app.OpenModel($modelPath,0)

    # Diagrama de actividad con el flujo de extremo a extremo del cliente.
    $activity = $model.ActivityDiagrams.CreateNew()
    $activity.Name = "Actividad - cliente $Cliente"
    $activity.Code = "ACT_EUREKABANK_$($prefix.ToUpperInvariant())"
    $nodes = @()
    $start = $model.Starts.CreateNew(); $start.Name='Inicio'; $start.Code='START'
    $nodes += $start
    $labels = $flowsByClient[$Cliente]
    foreach ($i in 0..($labels.Count-1)) {
        $action = $model.Activities.CreateNew(); $action.Name=$labels[$i]; $action.Code="A$i"; $action.Description='Flujo principal observado en el cliente; la respuesta depende del servicio alojado en Payara Server Community 7.'; $nodes += $action
    }
    $end = $model.Ends.CreateNew(); $end.Name='Fin'; $end.Code='END'; $nodes += $end
    foreach ($node in $nodes) { $activity.AttachObject($node) | Out-Null }
    for ($i=0; $i -lt $nodes.Count-1; $i++) {
        $flow = $model.Flows.CreateNew(); $flow.Object1=$nodes[$i]; $flow.Object2=$nodes[$i+1]
        $activity.AttachLinkObject($flow,$nodes[$i],$nodes[$i+1]) | Out-Null
    }
    $activity.AutoLayout() | Out-Null
    $activity.ExportImage($outputs[1]) | Out-Null
    $model.Save($outputs[0],$false,'')

    # Diagrama de clases construido desde las clases/adaptadores existentes.
    $classModelPath = $outputs[2]
    $classDiagram = $model.ClassDiagrams.CreateNew()
    $classDiagram.Name = "Clases - cliente $Cliente"
    $classDiagram.Code = "CLS_EUREKABANK_$($prefix.ToUpperInvariant())"
    $classObjects = @()
    foreach ($item in $classesByClient[$Cliente]) {
        $class = $model.Classes.CreateNew(); $class.Name=$item[0]; $class.Code=($item[0] -replace '[^A-Za-z0-9_]','_'); $class.Description=$item[1]
        $classDiagram.AttachObject($class) | Out-Null; $classObjects += $class
    }
    for ($i=0; $i -lt $classObjects.Count-1; $i++) {
        $assoc = $model.Associations.CreateNew(); $assoc.Object1=$classObjects[$i]; $assoc.Object2=$classObjects[$i+1]
        $classDiagram.AttachLinkObject($assoc,$classObjects[$i],$classObjects[$i+1]) | Out-Null
    }
    $classDiagram.AutoLayout() | Out-Null
    $classDiagram.ExportImage($outputs[5]) | Out-Null
    $model.Save($outputs[4],$false,'')

    # Diagrama de secuencia del flujo SOAP principal del cliente.
    $sequence = $model.SequenceDiagrams.CreateNew()
    $sequence.Name = "Secuencia SOAP - cliente $Cliente"
    $sequence.Code = "SEQ_EUREKABANK_$($prefix.ToUpperInvariant())"
    $sequenceClientClass = $model.Classes.CreateNew(); $sequenceClientClass.Name='Cliente'; $sequenceClientClass.Code='SeqCliente'
    $sequenceAdapterClass = $model.Classes.CreateNew(); $sequenceAdapterClass.Name='Adaptador SOAP'; $sequenceAdapterClass.Code='SeqAdaptador'
    $clientObject = $model.Objects.CreateNew(); $clientObject.Name='cliente'; $clientObject.Class=$sequenceClientClass
    $adapterObject = $model.Objects.CreateNew(); $adapterObject.Name='adaptador'; $adapterObject.Class=$sequenceAdapterClass
    $serviceClass = $model.Classes.CreateNew(); $serviceClass.Name='Servicio SOAP'; $serviceClass.Code='ServicioEurekaBank'; $serviceClass.Description='Servicio SOAP desplegado en Payara Server Community 7.'
    $serviceObject = $model.Objects.CreateNew(); $serviceObject.Name='Payara 7'; $serviceObject.Class=$serviceClass
    foreach ($participant in @($clientObject,$adapterObject,$serviceObject)) { $sequence.AttachObject($participant) | Out-Null }
    $conversation = @(
        @($clientObject,$adapterObject,'invocarOperacion'),
        @($adapterObject,$serviceObject,'solicitudSOAP'),
        @($serviceObject,$adapterObject,'respuestaSOAP'),
        @($adapterObject,$clientObject,'presentarResultado')
    )
    foreach ($exchange in $conversation) {
        $message = $model.Messages.CreateNew(); $message.Sender=$exchange[0]; $message.Receiver=$exchange[1]; $message.Object1=$exchange[0]; $message.Object2=$exchange[1]; $message.Name=$exchange[2]
        $sequence.AttachLinkObject($message,$exchange[0].Symbols.Item(0),$exchange[1].Symbols.Item(0)) | Out-Null
    }
    $sequence.AutoLayout() | Out-Null
    $sequence.RedrawAllViews() | Out-Null
    $model.Save($outputs[2],$false,'')

    # Diagrama UML de despliegue: cliente, servidor Payara 7 y MySQL remoto.
    $deployment = $model.DeploymentDiagrams.CreateNew()
    $deployment.Name = "Despliegue - cliente $Cliente"
    $deployment.Code = "DEP_EUREKABANK_$($prefix.ToUpperInvariant())"
    foreach ($name in @("Cliente $Cliente",'Payara Server Community 7','MySQL')) {
        $node = $model.Nodes.CreateNew(); $node.Name=$name; $node.Code=($name -replace '[^A-Za-z0-9]','_')
        $node.Description='Entorno de ejecución de EurekaBank. El cliente consume el servicio SOAP publicado en Payara 7.'
        $deployment.AttachObject($node) | Out-Null
    }
    $deployment.AutoLayout() | Out-Null
    $deployment.ExportImage($outputs[7]) | Out-Null
    $model.Save($outputs[6],$false,'')

    Write-Output "Diagramas UML PowerDesigner generados para cliente $Cliente"
    $outputs | ForEach-Object { Write-Output $_ }
}
finally {
    if ($null -ne $model) { $model.Close($false) }
    if ($null -ne $app) { [void][Runtime.InteropServices.Marshal]::ReleaseComObject($app) }
}
$layoutScript = Join-Path $base 'ajustar_secuencia.vbs'
$cscript = Join-Path $env:WINDIR 'SysWOW64\cscript.exe'
& $cscript //NoLogo $layoutScript $outputs[2] $outputs[3]
if ($LASTEXITCODE -ne 0) { throw 'No se pudo ajustar el diagrama de secuencia.' }
