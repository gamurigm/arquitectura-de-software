[CmdletBinding()]
param([string]$Raiz=$PSScriptRoot)
Set-StrictMode -Version Latest
$ErrorActionPreference='Stop'

$variants=@(
    @{Folder='1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR09';Key='SOAP_JAVA';Stack='Java';Protocol='SOAP';Server='Jakarta EE, JAX-WS y Payara Server Community 7';Clients='consola, Swing, JSP/Servlet y Android';Db='MySQL';Suffix='Java';Endpoint='http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka';EndpointVar='EUREKABANK_SOAP_URL';DbVars='EUREKABANK_DB_URL, EUREKABANK_DB_USER y EUREKABANK_DB_PASSWORD'},
    @{Folder='1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR09';Key='SOAP_DOTNET';Stack='.NET';Protocol='SOAP';Server='.NET 10 y CoreWCF';Clients='consola, WPF, ASP.NET Core MVC y MAUI';Db='SQL Server';Suffix='DotNet';Endpoint='http://localhost:5080/WSEureka';EndpointVar='EUREKABANK_SOAP_DOTNET_URL';DbVars='EUREKABANK_DB_CONNECTION_STRING'},
    @{Folder='1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR09';Key='RESTFUL_JAVA';Stack='Java';Protocol='REST';Server='Jakarta EE, Jakarta REST y Payara Server Community 7';Clients='consola, Swing, JSP/Servlet y Android';Db='MySQL';Suffix='Java';Endpoint='http://127.0.0.1:8080/eurekabank-rest-java-gr09/api/eureka';EndpointVar='EUREKABANK_REST_URL';DbVars='EUREKABANK_DB_URL, EUREKABANK_DB_USER y EUREKABANK_DB_PASSWORD'},
    @{Folder='1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09';Key='RESTFUL_DOTNET';Stack='.NET';Protocol='REST';Server='.NET 10 y ASP.NET Core';Clients='consola, WPF, ASP.NET Core MVC y MAUI';Db='SQL Server';Suffix='DotNet';Endpoint='http://localhost:5081/api/eureka';EndpointVar='EUREKABANK_REST_DOTNET_URL';DbVars='EUREKABANK_DB_CONNECTION_STRING'}
)

function Split-Sql([string]$s){
    $out=[Collections.Generic.List[string]]::new();$depth=0;$quoted=$false;$start=0
    for($i=0;$i -lt $s.Length;$i++){$c=$s[$i]
        if($c -eq "'"){if($quoted -and $i+1 -lt $s.Length -and $s[$i+1] -eq "'"){$i++;continue};$quoted=-not $quoted}
        elseif(-not $quoted){if($c -eq '('){$depth++}elseif($c -eq ')'){$depth--}elseif($c -eq ',' -and $depth -eq 0){$out.Add($s.Substring($start,$i-$start).Trim());$start=$i+1}}
    }
    if($start -lt $s.Length){$out.Add($s.Substring($start).Trim())};return ,$out
}
function Read-Schema([string]$path){
    $sql=[IO.File]::ReadAllText($path);$tables=[ordered]@{}
    foreach($m in [regex]::Matches($sql,'(?im)CREATE\s+TABLE\s+([A-Za-z]\w*)\s*\(')){
        $name=$m.Groups[1].Value;$open=$sql.IndexOf('(',$m.Index);$d=0;$q=$false;$close=-1
        for($i=$open;$i -lt $sql.Length;$i++){$c=$sql[$i];if($c -eq "'"){$q=-not $q}elseif(-not $q){if($c -eq '('){$d++}elseif($c -eq ')'){$d--;if($d -eq 0){$close=$i;break}}}}
        if($close -lt 0){throw "Definición SQL incompleta: $name"}
        $t=[pscustomobject]@{Name=$name;Columns=[Collections.Generic.List[object]]::new();PK=[Collections.Generic.List[string]]::new();FK=[Collections.Generic.List[object]]::new()}
        foreach($item in (Split-Sql $sql.Substring($open+1,$close-$open-1))){
            if($item -match '^\s*(?:CONSTRAINT\s+\w+\s+)?PRIMARY\s+KEY\s*\(([^)]*)\)'){foreach($c in ($Matches[1] -split ',')){$t.PK.Add($c.Trim())};continue}
            if($item -match '^\s*(?:CONSTRAINT\s+\w+\s+)?FOREIGN\s+KEY(?:\s+\w+)?\s*\(([^)]*)\)\s*REFERENCES\s+(\w+)\s*\(([^)]*)\)'){$t.FK.Add([pscustomobject]@{Columns=@($Matches[1] -split ','|%{$_.Trim()});Parent=$Matches[2]});continue}
            if($item -match '^\s*([A-Za-z]\w*)\s+([A-Za-z]+(?:\s*\([^)]*\))?)(.*)$' -and $Matches[1] -notin @('KEY','INDEX','UNIQUE','CONSTRAINT','CHECK','PRIMARY','FOREIGN')){
                $t.Columns.Add([pscustomobject]@{Name=$Matches[1];Type=($Matches[2] -replace '\s+','').ToUpperInvariant();Nullable=($Matches[3] -notmatch '\bNOT\s+NULL\b')})
            }
        }
        $tables[$name]=$t
    }
    foreach($m in [regex]::Matches($sql,'(?im)ALTER\s+TABLE\s+(\w+)\s+ADD\s+COLUMN\s+(\w+)\s+([A-Za-z]+(?:\s*\([^)]*\))?)([^;]*);')){
        $t=$tables[$m.Groups[1].Value];if($null -eq $t){throw 'ALTER TABLE apunta a una entidad desconocida'}
        if(-not($t.Columns|Where-Object Name -eq $m.Groups[2].Value)){$t.Columns.Add([pscustomobject]@{Name=$m.Groups[2].Value;Type=($m.Groups[3].Value -replace '\s+','').ToUpperInvariant();Nullable=($m.Groups[4].Value -notmatch '\bNOT\s+NULL\b')})}
    }
    foreach($m in [regex]::Matches($sql,'(?is)ALTER\s+TABLE\s+(\w+)\s+ADD\s+CONSTRAINT\s+\w+\s+FOREIGN\s+KEY\s*\(([^)]*)\)\s*REFERENCES\s+(\w+)\s*\(([^)]*)\)')){
        $t=$tables[$m.Groups[1].Value];if($null -eq $t){throw 'ALTER TABLE apunta a una entidad desconocida'}
        $t.FK.Add([pscustomobject]@{Columns=@($m.Groups[2].Value -split ','|%{$_.Trim()});Parent=$m.Groups[3].Value})
    }
    $script:ColumnCount=@($tables.Values|%{$_.Columns}).Count
    if($tables.Count -ne 19 -or $script:ColumnCount -lt 80){throw "Esquema incompleto: $($tables.Count) entidades, $script:ColumnCount campos"}
    return ,$tables
}

$labels=@{Cliente='Cliente';Credenciales='Credenciales';Cuenta='Cuenta bancaria';Moneda='Moneda';Movimiento='Movimiento';Transferencias='Transferencia';TipoMovimiento='Tipo de movimiento';Empleado='Empleado';Usuario='Usuario';Permiso='Permiso';Modulo='Módulo';LOGSESSION='Registro de sesión';Sucursal='Sucursal';Asignado='Asignación';CargoMantenimiento='Cargo de mantenimiento';CostoMovimiento='Costo de movimiento';InteresMensual='Interés mensual';Parametro='Parámetro';Contador='Contador'}
function Label-Table([string]$n){if($labels.ContainsKey($n)){return $labels[$n]};return ($n -replace '_',' ')}
function Describe-Table([string]$n){switch($n){
    'Transferencias'{return 'Relaciona cliente, clave idempotente, cuentas de origen y destino e importe.'}
    'Movimiento'{return 'Registra débitos, créditos, depósitos e importes asociados a una cuenta.'}
    'Cuenta'{return 'Conserva titularidad, moneda, estado y saldo de cada cuenta bancaria.'}
    'Cliente'{return 'Identifica al titular de cuentas y credenciales.'}
    'Credenciales'{return 'Almacena identificador de acceso y verificador de contraseña.'}
    'TipoMovimiento'{return 'Clasifica movimientos, incluidos 009 SALIDA y 008 INGRESO.'}
    default{return 'Conserva información del dominio bancario y sus relaciones.'}
}}
function Describe-Column([string]$n,[bool]$pk,[bool]$fk){
    if($pk){return 'Identificador principal de la entidad.'}
    if($n -match 'saldo|importe|monto|valor|tasa|interes|costo|cargo'){return 'Importe o valor monetario.'}
    if($n -match 'fecha|hora|dtt'){return 'Fecha y hora del registro o evento.'}
    if($n -match 'cuenta|origen|destino'){if($fk){return 'Referencia a una cuenta bancaria.'};return 'Número de cuenta relacionado.'}
    if($n -match 'cliente|moneda|usuario|empleado|sucursal|modulo|permiso|movimiento|transferencia|tipo'){if($fk){return 'Referencia a la entidad relacionada.'};return 'Código o clasificación relacionada.'}
    if($n -match 'estado|activo|situacion'){return 'Estado operativo del registro.'}
    if($n -match 'clave|token|hash|password|contrasena'){return 'Dato de control protegido; no contiene contraseña en texto claro.'}
    if($fk){return 'Referencia a la entidad relacionada.'};return 'Atributo propio de la entidad.'
}
function Column-Label([string]$n){return (($n -replace '^(chr|vch|int|dec|dtt|fec|num|var)_','') -replace '_',' ')}
function DbType([string]$type,$v,[string]$column){
    if($v.Db -eq 'SQL Server'){if($type -eq 'INTEGER'){$type='INT'};if($type -eq 'DATETIME'){$type='DATETIME2'};if($column -in @('id_transferencia','clave_operacion')){$type='UNIQUEIDENTIFIER'}}
    return $type
}
function P($doc,[string]$text,[int]$style=-1,[int]$align=0,[int]$size=10,[bool]$bold=$false){
    $start=[int]$doc.Content.End-1;$r=$doc.Range($start,$start);$r.InsertAfter($text+[char]13)
    $p=$doc.Range($start,$start+$text.Length+1).Paragraphs.Item(1);$p.Style=$style;$p.Alignment=$align;$p.Format.SpaceAfter=6;$p.Format.KeepTogether=-1
    $p.Range.Font.Name='Arial';$p.Range.Font.Size=$size;if($bold){$p.Range.Font.Bold=-1};return $p
}
function T($doc,[string[]]$headers,[object[]]$rows,[int[]]$widths=@()){
    $data=[Collections.Generic.List[object]]::new();$data.Add($headers);foreach($row in $rows){$data.Add($row)}
    $at=[int]$doc.Content.End-1;$table=$doc.Tables.Add($doc.Range($at,$at),$data.Count,$headers.Count)
    $table.Borders.Enable=1;$table.AllowAutoFit=$true;$table.AutoFitBehavior(2)
    for($r=0;$r -lt $data.Count;$r++){for($c=0;$c -lt $headers.Count;$c++){
        $rowData=$data[$r];if($rowData.Count -ne $headers.Count){throw "Fila de tabla $r tiene $($rowData.Count) valores; esperados $($headers.Count)"}
        $cell=$table.Cell($r+1,$c+1);$cell.Range.Text=[string]$rowData[$c];$cell.Range.Font.Name='Arial'
        $fontSize=if($headers.Count -ge 5){[double]7.5}else{[double]9}
        $cell.Range.Font.Size=$fontSize;$cell.VerticalAlignment=1
        if($r -eq 0){$cell.Range.Bold=-1;$cell.Shading.BackgroundPatternColor=15132390}
    }}
    if($widths.Count -eq $headers.Count){for($c=0;$c -lt $widths.Count;$c++){try{$table.Columns.Item($c+1).Width=$widths[$c]}catch{}}}
    try{$table.Rows.Item(1).HeadingFormat=-1}catch{}
    $tail=$doc.Range([int]$doc.Content.End-1,[int]$doc.Content.End-1);$tail.InsertAfter([char]13);return $table
}
function Pic($doc,[string]$path,[float]$max=450){
    if(-not(Test-Path -LiteralPath $path -PathType Leaf)){throw 'Falta imagen requerida'}
    $at=[int]$doc.Content.End-1;$pic=$doc.Range($at,$at).InlineShapes.AddPicture($path,$false,$true);$pic.LockAspectRatio=-1
    if($pic.Width -gt $max){$pic.Width=$max};$pic.Range.ParagraphFormat.Alignment=1
    $tail=$doc.Range([int]$doc.Content.End-1,[int]$doc.Content.End-1);$tail.InsertAfter([char]13)
}
function Setup-Word($doc){
    $doc.PageSetup.TopMargin=56;$doc.PageSetup.BottomMargin=52;$doc.PageSetup.LeftMargin=54;$doc.PageSetup.RightMargin=54
    $f=$doc.Sections.Item(1).Footers.Item(1).Range;$f.Text='EUREKABANK  |  GRUPO 09  |  NRC 36884';$f.Font.Name='Arial';$f.Font.Size=8;$f.ParagraphFormat.Alignment=2
}
function Cover($doc,$v,[string]$title,[string]$subtitle){
    [void](P $doc 'EUREKABANK' -63 1 29 $true);[void](P $doc 'GRUPO 09 · NRC 36884' -1 1 13 $true);[void](P $doc '' -1 1 12)
    [void](P $doc $title -2 1 21 $true);[void](P $doc $subtitle -1 1 13);[void](P $doc '' -1 1 10)
    [void](P $doc ('Protocolo: '+$v.Protocol) -1 1 11);[void](P $doc ('Tecnología: '+$v.Stack) -1 1 11)
    [void](P $doc ('Base de datos: '+$v.Db) -1 1 11);[void](P $doc '22 de octubre de 2026' -1 1 10)
    $n=[int]$doc.Content.End-1;$doc.Range($n,$n).InsertBreak(7)
}
function New-Doc($word,[string]$path,[string]$title,[scriptblock]$build){
    $doc=$word.Documents.Add();Setup-Word $doc
    try{
        & $build $doc
        try{$doc.BuiltInDocumentProperties.Item('Title').Value=$title;$doc.BuiltInDocumentProperties.Item('Author').Value='Grupo 09'}catch{}
        $doc.SaveAs2($path,16)
    }
    finally{$doc.Close($false);[void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($doc)}
}

function Dictionary($doc,$v,$tables){
    Cover $doc $v 'DICCIONARIO DE DATOS' ('Modelo EurekaBank · '+$v.Db)
    [void](P $doc '1. Descripción' -2 0 15 $true)
    [void](P $doc ('Entidades y atributos, tipos de '+$v.Db+', nulabilidad, claves y significado funcional para la variante '+$v.Stack+' '+$v.Protocol+'.'))
    [void](P $doc 'PK identifica clave primaria; FK identifica referencia foránea. Nulable indica si el motor permite ausencia de valor. Los UUID se representan como UNIQUEIDENTIFIER en SQL Server.')
    [void](P $doc '2. Entidades y atributos' -2 0 15 $true)
    foreach($table in $tables.Values){
        [void](P $doc ((Label-Table $table.Name)+' ('+$table.Name+')') -3 0 12 $true);[void](P $doc (Describe-Table $table.Name) -1 0 9)
        $rows=[Collections.Generic.List[object]]::new()
        foreach($col in $table.Columns){
            $pk=$table.PK -contains $col.Name;$fk=$table.FK|Where-Object{$_.Columns -contains $col.Name}|Select-Object -First 1
            $key=if($pk){'PK'}else{''};if($null -ne $fk){$key=if($key){$key+', FK '+$fk.Parent}else{'FK '+$fk.Parent}}
            $rows.Add(@((Column-Label $col.Name),(DbType $col.Type $v $col.Name),$(if($col.Nullable){'Sí'}else{'No'}),$key,(Describe-Column $col.Name $pk ($null -ne $fk))))
        }
        [void](T $doc @('Campo','Tipo','Nulable','Clave','Descripción') $rows.ToArray() @(88,88,43,84,174))
    }
    [void](P $doc '3. Transferencia contable' -2 0 15 $true)
    [void](P $doc 'Cada transferencia confirmada registra 009 SALIDA en origen y 008 INGRESO en destino. Ambos movimientos se enlazan con Transferencias y se guardan en una transacción; cualquier error revierte la operación completa.')
}
function Manual($doc,$v,[string]$screenshot,[string[]]$mer){
    Cover $doc $v 'MANUAL TÉCNICO' ('Cliente '+$v.Stack+' · '+$v.Protocol)
    [void](P $doc '1. Propósito y alcance' -2 0 15 $true)
    [void](P $doc ('Describe arquitectura, operación y mantenimiento de EurekaBank para '+$v.Stack+' con '+$v.Protocol+'. Permite iniciar sesión, consultar movimientos, registrar depósitos, transferir fondos y cerrar sesión. Los clientes consumen el servicio y no abren conexiones directas a la base de datos.'))
    [void](P $doc '2. Arquitectura y tecnologías' -2 0 15 $true)
    [void](P $doc ('Servicio: '+$v.Server+'. Persistencia: '+$v.Db+'. Clientes '+$v.Stack+': '+$v.Clients+'.'))
    [void](T $doc @('Capa','Responsabilidad') @(
        @('Vista','Captura datos, muestra resultados y errores.'),
        @('Controlador','Valida interacción, administra navegación y coordina servicios.'),
        @('Modelo','Representa cuentas, movimientos, sesiones, depósitos y transferencias.'),
        @('Cliente de servicio',('Serializa solicitudes y procesa respuestas '+$v.Protocol+'.')),
        @('Servicio y repositorio','Aplica reglas, controla transacciones y accede al motor.')
    ) @(112,365))
    $architectureNote=if($v.Stack -eq 'Java'){'Los clientes Java usan MVC y conservan la estructura de paquetes ec.edu.monster. El servidor mantiene autorización, reglas bancarias y persistencia.'}else{'Los clientes .NET separan vistas, controladores y servicios con MVC. El servidor mantiene autorización, reglas bancarias y persistencia.'}
    [void](P $doc $architectureNote)
    [void](P $doc '3. Contrato de comunicación' -2 0 15 $true)
    if($v.Protocol -eq 'SOAP'){
        [void](P $doc 'WSEureka publica un contrato WSDL con mensajes XML. Operaciones: login, crearSesion, traerMovimientos, regDeposito, transferir y cerrarSesion. El token temporal identifica la sesión autenticada.')
        [void](T $doc @('Operación','Uso') @(
            @('login / crearSesion','Validar credenciales y emitir token temporal.'),
            @('traerMovimientos','Consultar movimientos de una cuenta.'),
            @('regDeposito','Registrar un depósito.'),
            @('transferir','Enviar token, origen, destino, importe y clave idempotente.'),
            @('cerrarSesion','Revocar el token.')
        ) @(145,332))
    }else{
        [void](P $doc 'El servicio HTTP intercambia JSON bajo api/eureka. La transferencia envía token Bearer en Authorization. Las respuestas distinguen solicitud inválida, sesión rechazada y error del servicio.')
        [void](T $doc @('Método y recurso','Uso') @(
            @('POST /api/eureka/login','Autenticar credenciales.'),
            @('GET /api/eureka/movimientos?cuenta={cuenta}','Consultar movimientos.'),
            @('POST /api/eureka/depositos','Registrar depósito.'),
            @('POST /api/eureka/transferencias','Transferir con token e idempotencia.'),
            @('POST /api/eureka/logout','Revocar sesión.')
        ) @(220,257))
    }
    [void](P $doc ('Endpoint local: '+$v.Endpoint+'. Configuración del cliente: '+$v.EndpointVar+'. Para emulador o teléfono, defina una dirección alcanzable desde el dispositivo.'))
    [void](P $doc '4. Transferencias seguras' -2 0 15 $true)
    [void](P $doc 'Destino propio o de terceros; origen de titularidad autenticada. Débito, crédito, movimientos y transferencia forman una unidad atómica.')
    foreach($line in @(
        'Validar cuentas distintas de ocho dígitos e importe positivo con hasta dos decimales.'
        'Verificar sesión, titularidad del origen, existencia, estado activo y moneda común.'
        'Bloquear cuentas en orden determinista y comprobar saldo disponible.'
        'Guardar transferencia, débito 009 SALIDA y crédito 008 INGRESO en una transacción.'
        'Revertir todos los cambios ante fallo. Reintentar con la misma clave idempotente.'
    )){[void](P $doc ('• '+$line))}
    [void](P $doc 'Consultas parametrizadas reducen el riesgo de inyección. La misma clave con los mismos datos no duplica movimientos; la clave con datos distintos se rechaza.')
    [void](P $doc '5. Persistencia y MER' -2 0 15 $true)
    [void](P $doc ('El esquema tiene 19 entidades y '+$script:ColumnCount+' atributos. El MER conceptual muestra entidades y relaciones; el lógico detalla atributos y claves; el físico adapta tipos a '+$v.Db+'. Los modelos editables PowerDesigner acompañan las imágenes.'))
    [void](P $doc 'Ilustración 1. MER conceptual: cuenta de origen y transferencia.' -1 1 9);Pic $doc $mer[0]
    [void](P $doc 'Ilustración 2. MER lógico: cuenta de origen y transferencia.' -1 1 9);Pic $doc $mer[1]
    [void](P $doc ('Ilustración 3. MER físico para '+$v.Db+': cuenta de origen y transferencia.') -1 1 9);Pic $doc $mer[2]
    [void](P $doc '6. Configuración y construcción' -2 0 15 $true)
    [void](P $doc ('El servidor '+$v.Stack+' se configura con '+$v.DbVars+'. Mantenga los valores reales en variables de entorno; nunca en código, capturas, registros o documentación.'))
    if($v.Stack -eq 'Java'){[void](P $doc 'Verifique JDK y Maven. Ejecute mvn test y mvn package desde el módulo. Inicie Payara, despliegue el servicio y configure el endpoint antes de abrir un cliente.')}
    else{[void](P $doc 'Verifique .NET 10 y sus dependencias. Ejecute dotnet build y dotnet test desde la solución o módulo. Inicie el servicio y configure su endpoint antes de usar las interfaces.')}
    [void](P $doc 'Se requiere servicio activo, esquema creado y migraciones de transferencia aplicadas.')
    [void](P $doc '7. Captura de interfaz' -2 0 15 $true)
    [void](P $doc 'Captura real del formulario de inicio de sesión con campos vacíos. No representa autenticación contra un servicio activo.')
    Pic $doc $screenshot 440
    [void](P $doc '8. Diagnóstico' -2 0 15 $true)
    [void](T $doc @('Síntoma','Verificación') @(
        @('Conexión rechazada','Compruebe servicio activo, endpoint configurado y puerto accesible.'),
        @('Autenticación rechazada','Revise credenciales, estado y disponibilidad del repositorio.'),
        @('Transferencia rechazada','Revise sesión, titularidad, estado, moneda, saldo e importe.'),
        @('Respuesta incierta','Consulte resultado antes de repetir; use la misma clave idempotente.'),
        @('Error de base de datos','Revise configuración, esquema, permisos y migraciones; no muestre secretos.')
    ) @(150,327))
    [void](P $doc 'No registre contraseñas, tokens completos ni cadenas de conexión. Registre hora, código de respuesta e identificador de correlación.')
}

$schemaPath=Join-Path (Join-Path (Join-Path $Raiz $variants[0].Folder) '03. BDD') 'Crea_BD.sql';$tables=Read-Schema $schemaPath
$word=$null
try{
    $word=New-Object -ComObject Word.Application;$word.Visible=$false;$word.DisplayAlerts=0
    foreach($v in $variants){
        $root=Join-Path $Raiz $v.Folder;$docs=Join-Path $root '05. DOCUMENTACION'
        $dictDir=Join-Path $docs '01. DICCIONARIO DE DATOS';$manualDir=Join-Path $docs '02. MANUAL TECNICO';$pptDir=Join-Path $docs '03. PRESENTACION'
        $captureDir=Join-Path $manualDir 'Capturas';New-Item -ItemType Directory -Force -Path $dictDir,$manualDir,$pptDir,$captureDir|Out-Null
        $phys=if($v.Stack -eq 'Java'){'Java_MySQL'}else{'DotNet_SQLServer'};$merRoot=Join-Path $root '02. MER'
        $mer=@((Join-Path (Join-Path $merRoot '01. CONCEPTUAL') ('GR09_EurekaBank_Conceptual_'+$v.Suffix+'_02_Transferencia_Origen.png')),(Join-Path (Join-Path $merRoot '02. LOGICO') ('GR09_EurekaBank_Logical_'+$v.Suffix+'_02_Transferencia_Origen.png')),(Join-Path (Join-Path $merRoot '03. FISICO') ('GR09_EurekaBank_Physical_'+$phys+'_02_Transferencia_Origen.png')))
        $shot=Join-Path $captureDir 'InterfazInicioSesion.png';foreach($asset in @($shot)+$mer){if(-not(Test-Path -LiteralPath $asset -PathType Leaf)){throw "Falta captura o MER para $($v.Key)"}}
        $dict=Join-Path $dictDir ('DICCIONARIO_DATOS_EUREKABANK_'+$v.Key+'_GR09.docx');$manual=Join-Path $manualDir ('MANUAL_TECNICO_EUREKABANK_'+$v.Key+'_GR09.docx')
        New-Doc $word $dict ('Diccionario EurekaBank '+$v.Stack+' '+$v.Protocol) {param($d) Dictionary $d $v $tables}
        New-Doc $word $manual ('Manual EurekaBank '+$v.Stack+' '+$v.Protocol) {param($d) Manual $d $v $shot $mer}
        Write-Output ('Generado: '+$v.Key+' · diccionario y manual')
    }
}finally{
    if($word){try{$word.Quit()}catch{};[void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($word)}
    [GC]::Collect();[GC]::WaitForPendingFinalizers()
}

$presentationGenerator=Join-Path $PSScriptRoot 'GenerarPresentacionesEurekaBankGR09.py'
if(-not(Test-Path -LiteralPath $presentationGenerator -PathType Leaf)){throw 'Falta el generador de presentaciones basado en la plantilla original.'}
$python=Get-Command python.exe -ErrorAction Stop
& $python.Source $presentationGenerator --root $Raiz
if($LASTEXITCODE -ne 0){throw "Falló la generación de presentaciones: código $LASTEXITCODE"}
