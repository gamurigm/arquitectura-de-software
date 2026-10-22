[CmdletBinding()]
param(
    [string]$Raiz = $PSScriptRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$perfiles = @(
    @{ Carpeta = '1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR09'; Clave = 'SOAP_JAVA'; Protocolo = 'SOAP'; Lenguaje = 'Java';
       Tecnologia = 'Jakarta EE con JAX-WS y Payara Server Community 7. Clientes Java: consola, escritorio Swing, web JSP/Servlet y Android.'; BaseDatos = 'MySQL'; Endpoint = 'http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka'; EndpointVar = 'EUREKABANK_SOAP_URL' },
    @{ Carpeta = '1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR09'; Clave = 'SOAP_DOTNET'; Protocolo = 'SOAP'; Lenguaje = '.NET';
       Tecnologia = '.NET 10 con CoreWCF. Clientes: consola, escritorio WPF, web ASP.NET Core MVC y móvil .NET MAUI.'; BaseDatos = 'SQL Server'; Endpoint = 'http://localhost:5080/WSEureka'; EndpointVar = 'EUREKABANK_SOAP_DOTNET_URL' },
    @{ Carpeta = '1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR09'; Clave = 'RESTFUL_JAVA'; Protocolo = 'REST'; Lenguaje = 'Java';
       Tecnologia = 'Jakarta EE con Jakarta REST y Payara Server Community 7. Clientes Java: consola, escritorio Swing, web JSP/Servlet y Android.'; BaseDatos = 'MySQL'; Endpoint = 'http://127.0.0.1:8080/eurekabank-rest-java-gr09/api/eureka'; EndpointVar = 'EUREKABANK_REST_URL' },
    @{ Carpeta = '1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09'; Clave = 'RESTFUL_DOTNET'; Protocolo = 'REST'; Lenguaje = '.NET';
       Tecnologia = '.NET 10 con ASP.NET Core. Clientes: consola, escritorio WPF, web ASP.NET Core MVC y móvil .NET MAUI.'; BaseDatos = 'SQL Server'; Endpoint = 'http://localhost:5081/api/eureka'; EndpointVar = 'EUREKABANK_REST_DOTNET_URL' }
)

$casos = @(
    @{ Id = 'ECUD1'; Codigo = 'Login'; Nombre = 'Iniciar sesión';
       Descripcion = 'Permite que el usuario valide sus credenciales ante el servicio EurekaBank y acceda a las funciones autorizadas.';
       Meta = 'Establecer una sesión local después de recibir una autenticación correcta.';
       Pre = 'El servicio está disponible y el usuario tiene credenciales vigentes.';
       Post = 'Con éxito, el cliente conserva la identidad de sesión necesaria para operar. La contraseña no se conserva.';
       Disparador = 'El usuario abre la aplicación y selecciona Iniciar sesión.';
       Actor = 'Usuario';
       Flujo = @(
          @('Usuario', 'Elige Iniciar sesión.'),
          @('Sistema', 'Muestra el formulario de acceso.'),
          @('Usuario', 'Ingresa nombre de usuario y contraseña.'),
          @('Sistema', 'Verifica que ambos campos tengan valor y envía las credenciales.'),
          @('Servicio', 'Valida las credenciales y devuelve el resultado de autenticación.'),
          @('Sistema', 'Si la autenticación tiene éxito, establece sesión local y muestra el menú.')
       );
       Alternos = 'A1. Campos vacíos: el cliente informa el dato requerido y conserva el formulario. A2. Credenciales incorrectas: el servicio rechaza el acceso; el cliente no crea sesión. A3. Servicio no disponible: el cliente muestra un error de comunicación y permite reintentar.' },
    @{ Id = 'ECUD2'; Codigo = 'Movimientos'; Nombre = 'Consultar movimientos';
       Descripcion = 'Permite consultar los movimientos asociados a una cuenta bancaria seleccionada.';
       Meta = 'Presentar movimientos recibidos desde el servicio, ordenados del más reciente al más antiguo.';
       Pre = 'Existe una sesión local autenticada y el usuario dispone de una cuenta consultable.';
       Post = 'El cliente muestra los movimientos recibidos o informa que no hay resultados. No modifica la cuenta.';
       Disparador = 'El usuario selecciona Consultar movimientos e indica una cuenta.';
       Actor = 'Usuario';
       Flujo = @(
          @('Usuario', 'Elige Consultar movimientos.'),
          @('Sistema', 'Solicita el identificador de cuenta.'),
          @('Usuario', 'Selecciona o ingresa la cuenta.'),
          @('Sistema', 'Valida la sesión y el dato de cuenta, y envía la solicitud.'),
          @('Servicio', 'Consulta y devuelve los movimientos de la cuenta.'),
          @('Sistema', 'Presenta fecha, tipo, acción, importe y número de movimiento.')
       );
       Alternos = 'A1. Sesión local ausente: el cliente regresa al acceso. A2. Cuenta sin movimientos: el cliente presenta una lista vacía con un mensaje claro. A3. Cuenta inválida o error del servicio: se informa el problema sin alterar datos.' },
    @{ Id = 'ECUD3'; Codigo = 'Deposito'; Nombre = 'Registrar depósito';
       Descripcion = 'Permite registrar un depósito en una cuenta activa por medio del servicio EurekaBank.';
       Meta = 'Acreditar el importe y registrar el movimiento como una sola operación del servidor.';
       Pre = 'Existe una sesión local autenticada. La cuenta está identificada y el importe es positivo.';
       Post = 'El servicio confirma el depósito después de guardar la actualización y el movimiento. Si falla, revierte la operación.';
       Disparador = 'El usuario selecciona Registrar depósito.';
       Actor = 'Usuario';
       Flujo = @(
          @('Usuario', 'Elige Registrar depósito.'),
          @('Sistema', 'Solicita cuenta e importe.'),
          @('Usuario', 'Ingresa la cuenta y el importe.'),
          @('Sistema', 'Valida sesión, campos completos e importe positivo; solicita confirmación.'),
          @('Usuario', 'Confirma el depósito.'),
          @('Servicio', 'Valida la cuenta activa y registra saldo y movimiento en una transacción.'),
          @('Sistema', 'Muestra confirmación cuando el servicio informa que la operación terminó.')
       );
       Alternos = 'A1. Cuenta o importe inválido: no se envía la solicitud y se indican los campos por corregir. A2. Cuenta inactiva o inexistente: el servicio rechaza la operación y no confirma el depósito. A3. Error antes de confirmar el resultado: el cliente informa que debe verificarse el movimiento antes de reintentar, para evitar duplicados.' },
    @{ Id = 'ECUD4'; Codigo = 'CerrarSesion'; Nombre = 'Cerrar sesión';
       Descripcion = 'Revoca la sesión del servicio y elimina el estado autenticado del cliente.';
       Meta = 'Impedir que se reutilice el token de sesión después de salir.';
       Pre = 'Existe una sesión local autenticada.';
       Post = 'El servicio revoca el token y el cliente elimina los datos de sesión y presenta el acceso.';
       Disparador = 'El usuario selecciona Cerrar sesión.';
       Actor = 'Usuario';
       Flujo = @(
          @('Usuario', 'Elige Cerrar sesión.'),
          @('Sistema', 'Envía la solicitud de revocación de la sesión.'),
          @('Servicio', 'Revoca el token activo.'),
          @('Sistema', 'Elimina la identidad y los datos de sesión locales.'),
          @('Sistema', 'Limpia la vista protegida y presenta el formulario de acceso.')
       );
       Alternos = 'A1. No existe sesión local: el cliente limpia el estado restante y presenta el acceso. A2. El servicio no responde: el cliente elimina el token local; el servicio expira el token según su vigencia.' },
    @{ Id = 'ECUD5'; Codigo = 'Transferencia'; Nombre = 'Transferir entre cuentas';
       Descripcion = 'Mueve fondos desde una cuenta propia hacia otra cuenta propia o de terceros mediante una operación bancaria atómica.';
       Meta = 'Debitar una cuenta de origen y acreditar una cuenta de destino una sola vez.';
       Pre = 'Existe una sesión activa asociada a un cliente. Las cuentas existen, están activas y usan la misma moneda. El cliente es titular de la cuenta de origen.';
       Post = 'El servicio registra la transferencia, un débito 009 SALIDA y un crédito 008 INGRESO en una transacción. Ante fallo, revierte todos los cambios.';
       Disparador = 'El usuario selecciona Transferir.';
       Actor = 'Usuario';
       Flujo = @(
          @('Usuario', 'Elige Transferir e ingresa origen, destino e importe.'),
          @('Sistema', 'Valida cuentas distintas de 8 dígitos, importe positivo con hasta dos decimales y genera una clave idempotente.'),
          @('Usuario', 'Confirma la transferencia.'),
          @('Sistema', 'Envía token, cuentas, importe y clave idempotente al servicio.'),
          @('Servicio', 'Valida el token, titularidad, estado y moneda de ambas cuentas, e identifica una solicitud repetida.'),
          @('Servicio', 'Bloquea las cuentas en orden determinista y comprueba el saldo disponible.'),
          @('Servicio', 'En una transacción, debita origen, acredita destino y registra movimientos 009 SALIDA y 008 INGRESO.'),
          @('Sistema', 'Muestra confirmación solo después de recibir el resultado exitoso.')
       );
       Alternos = 'A1. Campos inválidos o cuentas iguales: no se envía la operación. A2. Token inválido o cuenta de origen ajena: el servicio rechaza la solicitud. A3. Cuenta inexistente, inactiva, con moneda distinta o saldo insuficiente: no se confirma. A4. Misma clave y mismos datos: se devuelve el resultado existente sin duplicar movimientos. A5. Misma clave con otros datos: se rechaza. A6. Error durante el registro: se revierten débito, crédito, movimientos y transferencia; el cliente reintenta con la misma clave.' }
)

function Add-Paragraph {
    param($Document, [string]$Text, [int]$Style = -1, [bool]$PageBreakBefore = $false)
    $position = [int]$Document.Content.End - 1
    $range = $Document.Range($position, $position)
    $range.InsertAfter($Text + "`r")
    $paragraph = $Document.Range($position, $position + $Text.Length + 1).Paragraphs.Item(1)
    $paragraph.Style = if ($Style -eq -63) { -1 } elseif ($Style -eq -64) { -2 } else { $Style }
    if ($Style -eq -1) { $paragraph.Range.Font.Name = 'Arial'; $paragraph.Range.Font.Size = 12; $paragraph.Range.Font.Bold = 0; $paragraph.Range.Font.Italic = 0 }
    if ($Style -eq -2) { $paragraph.Range.Font.Name = 'Arial'; $paragraph.Range.Font.Size = 16; $paragraph.Range.Font.Bold = 1; $paragraph.Range.Font.Italic = 0 }
    if ($Style -eq -3) { $paragraph.Range.Font.Name = 'Arial'; $paragraph.Range.Font.Size = 12; $paragraph.Range.Font.Bold = 1; $paragraph.Range.Font.Italic = 0 }
    if ($Style -eq -35) { $paragraph.Range.Font.Name = 'Arial'; $paragraph.Range.Font.Size = 11; $paragraph.Range.Font.Bold = 0; $paragraph.Range.Font.Italic = 1 }
    if ($Style -eq -63 -or $Style -eq -64) {
        $paragraph.Range.Font.Name = 'Cambria'
        $paragraph.Range.Font.Size = 16
        $paragraph.Range.Font.Bold = 0
        $paragraph.Range.Font.Italic = 0
        $paragraph.Range.Font.Color = 9527350
        $paragraph.Format.Alignment = 0
        $paragraph.Format.SpaceBefore = 12
    }
    $paragraph.Format.SpaceAfter = 6
    if ($PageBreakBefore) { $paragraph.Format.PageBreakBefore = $true }
    return $paragraph
}

function Add-PageBreak {
    param($Document)
    $position = [int]$Document.Content.End - 1
    $range = $Document.Range($position, $position)
    $range.InsertBreak(7)
}

function Add-Field {
    param($Document, [string]$Code)
    $position = [int]$Document.Content.End - 1
    $range = $Document.Range($position, $position)
    [void]$Document.Fields.Add($range, -1, $Code, $true)
    Add-Paragraph $Document '' -1 | Out-Null
}

function Add-Table {
    param($Document, [object[]]$Rows, [string]$Caption = '')
    $position = [int]$Document.Content.End - 1
    $range = $Document.Range($position, $position)
    $table = $Document.Tables.Add($range, $Rows.Count, 2)
    $table.Borders.Enable = 1
    $table.AllowAutoFit = $true
    $table.AutoFitBehavior(2)
    try { $table.Columns.Item(1).Width = 145; $table.Columns.Item(2).Width = 315 } catch { }
    for ($row = 0; $row -lt $Rows.Count; $row++) {
        $left = [string]$Rows[$row][0]
        $right = [string]$Rows[$row][1]
        $table.Cell($row + 1, 1).Range.Text = $left
        $table.Cell($row + 1, 2).Range.Text = $right
        $table.Cell($row + 1, 1).Range.Bold = 1
        $table.Cell($row + 1, 1).Shading.BackgroundPatternColor = 15132390
    }
    if ($Caption) { Add-Paragraph $Document $Caption -35 | Out-Null }
    return $table
}

function Add-GridTable {
    param($Document, [object[]]$Rows, [string]$Caption = '', [int[]]$MergeAllColumnsRows = @(), [int[]]$BoldRows = @())
    if ($Rows.Count -lt 1) { throw 'La tabla requiere al menos una fila.' }
    $columns = $Rows[0].Count
    foreach ($row in $Rows) { if ($row.Count -ne $columns) { throw 'Las filas de la tabla deben tener el mismo número de columnas.' } }
    if ($Caption) { Add-Paragraph $Document $Caption -35 | Out-Null }
    $position = [int]$Document.Content.End - 1
    $range = $Document.Range($position, $position)
    $table = $Document.Tables.Add($range, $Rows.Count, $columns)
    $table.Borders.Enable = 0
    $table.AllowAutoFit = $true
    $table.AutoFitBehavior(2)
    $table.Range.Font.Name = 'Arial'
    $table.Range.Font.Size = if ($columns -ge 6) { 9 } elseif ($columns -ge 4) { 10 } else { 11 }
    $table.Range.Font.Bold = 0
    $table.Range.Font.Italic = 0
    $table.Range.ParagraphFormat.SpaceAfter = 2
    foreach ($rowIndex in $MergeAllColumnsRows) {
        if ($columns -gt 1) { $table.Cell($rowIndex, 1).Merge($table.Cell($rowIndex, $columns)) }
    }
    for ($row = 0; $row -lt $Rows.Count; $row++) {
        $merged = $MergeAllColumnsRows -contains ($row + 1)
        for ($column = 0; $column -lt $columns; $column++) {
            if ($merged -and $column -gt 0) { continue }
            $table.Cell($row + 1, $column + 1).Range.Text = [string]$Rows[$row][$column]
        }
    }
    foreach ($rowIndex in $BoldRows) { $table.Rows.Item($rowIndex).Range.Bold = 1 }
    return $table
}

function Add-Picture {
    param($Document, [string]$Path, [int]$MaxWidth = 450)
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { throw "Falta el diagrama requerido: $Path" }
    $position = [int]$Document.Content.End - 1
    $range = $Document.Range($position, $position)
    $picture = $range.InlineShapes.AddPicture($Path, $false, $true)
    $picture.LockAspectRatio = -1
    if ($picture.Width -gt $MaxWidth) { $picture.Width = $MaxWidth }
    Add-Paragraph $Document '' -1 | Out-Null
    return $picture
}

function Add-TocFrontMatter {
    param($Document, [string]$ReportTitle, [string[]]$Illustrations, [string[]]$Tables)
    $script:FrontMatterEntries = [System.Collections.ArrayList]::new()
    $script:BodyContentStart = 0
    Add-Paragraph $Document $ReportTitle -2 | Out-Null
    Add-Paragraph $Document 'ÍNDICE DE CONTENIDOS' -63 | Out-Null
    Add-Field $Document 'TOC \o "1-3" \h \z \u'
    $position = [int]$Document.Content.End - 1
    $Document.Range($position, $position).InsertBreak(2)
    Set-BodyPageLayout $Document
    Add-Paragraph $Document 'ÍNDICE DE ILUSTRACIONES' -63 | Out-Null
    foreach ($item in $Illustrations) {
        $p = Add-Paragraph $Document $item -1
        $p.Format.TabStops.Add(420, 2, 1) | Out-Null
        [void]$script:FrontMatterEntries.Add([pscustomobject]@{ Caption = $item; Paragraph = $p })
    }
    Add-Paragraph $Document 'ÍNDICE DE TABLAS' -63 | Out-Null
    foreach ($item in $Tables) {
        $p = Add-Paragraph $Document $item -1
        $p.Format.TabStops.Add(420, 2, 1) | Out-Null
        [void]$script:FrontMatterEntries.Add([pscustomobject]@{ Caption = $item; Paragraph = $p })
    }
    Add-PageBreak $Document
    $script:BodyContentStart = [int]$Document.Content.End - 1
}

function Update-FrontMatterPages {
    param($Document)
    $bodyStart = 0
    foreach ($paragraph in $Document.Paragraphs) {
        $text = ($paragraph.Range.Text -replace '[\r\a]', '').Trim()
        if ($paragraph.Style.NameLocal -eq 'Título 1' -and $text -in @('CASO DE ESTUDIO', 'ESPECIFICACIÓN DE CASOS DE USO DETALLADO')) {
            $bodyStart = [int]$paragraph.Range.Start
            break
        }
    }
    if ($bodyStart -eq 0) { throw 'No se encontró el inicio del contenido para actualizar los índices.' }
    for ($i = $script:FrontMatterEntries.Count - 1; $i -ge 0; $i--) {
        $entry = $script:FrontMatterEntries[$i]
        $searchRange = $Document.Range($bodyStart, [int]$Document.Content.End - 1)
        $find = $searchRange.Find
        $find.ClearFormatting()
        $find.Text = $entry.Caption
        $find.Forward = $false
        $find.Wrap = 0
        if (-not $find.Execute()) { throw "No se encontró la ilustración o tabla en el contenido: $($entry.Caption)" }
        $page = [int]$searchRange.Information(3)
        $target = $null
        foreach ($paragraph in $Document.Paragraphs) {
            if ([int]$paragraph.Range.Start -ge $bodyStart) { break }
            $text = ($paragraph.Range.Text -replace '[\r\a]', '').Trim()
            if ($text -eq $entry.Caption -or $text.StartsWith($entry.Caption + "`t")) { $target = $paragraph.Range; break }
        }
        if ($null -eq $target) { throw "No se encontró la entrada de índice: $($entry.Caption)" }
        $target.Text = $entry.Caption + "`t" + $page + "`r"
        $target.Paragraphs.Item(1).Format.TabStops.Add(420, 2, 1) | Out-Null
    }
}

function Replace-RangeText {
    param($Range, [string]$Old, [string]$New)
    $find = $Range.Find
    $find.ClearFormatting()
    $find.Replacement.ClearFormatting()
    $find.Text = $Old
    $find.Replacement.Text = $New
    $find.Forward = $true
    $find.Wrap = 1
    $find.MatchCase = $false
    $find.MatchWildcards = $false
    [void]$find.Execute($Old, $false, $false, $false, $false, $false, $true, 1, $false, $New, 2)
}

function Update-Cover {
    param($Document, [string]$Title, [bool]$IsEcud)
    for ($index = 1; $index -le $Document.Shapes.Count; $index++) {
        $shape = $Document.Shapes.Item($index)
        if ($shape.TextFrame.HasText -eq -1) {
            $textRange = $shape.TextFrame.TextRange
            if ($IsEcud) {
                Replace-RangeText $textRange 'ESPECIFICACIÓN DE CASOS DE USO DETALLADO SISTEMA CONVERSIÓN UNIDADES MONSTER CON SOAP SIN BDD EN JAVA' $Title
                Replace-RangeText $textRange 'RUBEN DARIO BENAVIDES MACIAS' 'GRUPO 09 NRC 36884'
                Replace-RangeText $textRange 'ARIEL DUNKENY GUEVARA HARO' ''
                Replace-RangeText $textRange 'JHON ALEXANDER MUNARCO SANTOS' ''
                Replace-RangeText $textRange '24/04/2026' '22/10/2026'
            } else {
                Replace-RangeText $textRange 'ESPECIFICACIÓN DE REQUERIMIENTOS DE SOFTWARE PARA SOA CON SOAP EN JAVA SIN BDD' $Title
                Replace-RangeText $textRange 'RUBEN DARIO BENAVIDES MACIAS' 'GRUPO 09 NRC 36884'
                Replace-RangeText $textRange 'ARIEL DUNKENY GUEVARA HARO' ''
                Replace-RangeText $textRange 'JHON ALEXANDER MUNARCO SANTOS' ''
                Replace-RangeText $textRange '14/04/2026' '22/10/2026'
            }
            Replace-RangeText $textRange 'Ing. Eduardo Mauricio Campaña Ortega' 'Docente de la asignatura'
            Replace-RangeText $textRange 'MIS.MDU.CCNA.CCIA.' ''
            Replace-RangeText $textRange 'PhD. (c) Ingeniería de Software' ''
            Replace-RangeText $textRange 'PhD. (c) Seguridad Información' ''
        }
    }
    try {
        $Document.BuiltInDocumentProperties.Item('Title').Value = $Title
        $Document.BuiltInDocumentProperties.Item('Author').Value = 'Grupo 09 · NRC 36884'
        $Document.BuiltInDocumentProperties.Item('Subject').Value = 'Arquitectura de software · EurekaBank · Grupo 09 · NRC 36884'
    } catch { }
}

function Replace-CoverImage {
    param([string]$DocumentPath, [string]$Path)
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { throw "Falta la captura de portada: $Path" }
    Add-Type -AssemblyName System.Drawing
    Add-Type -AssemblyName System.IO.Compression
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $archive = [System.IO.Compression.ZipFile]::Open($DocumentPath, [System.IO.Compression.ZipArchiveMode]::Update)
    $sourceImage = $null
    $canvas = $null
    $graphics = $null
    $gradient = $null
    $memory = $null
    try {
        $documentEntry = $archive.GetEntry('word/document.xml')
        $reader = [System.IO.StreamReader]::new($documentEntry.Open())
        try { [xml]$documentXml = $reader.ReadToEnd() } finally { $reader.Dispose() }
        $documentNamespaces = [System.Xml.XmlNamespaceManager]::new($documentXml.NameTable)
        $documentNamespaces.AddNamespace('w', 'http://schemas.openxmlformats.org/wordprocessingml/2006/main')
        $documentNamespaces.AddNamespace('a', 'http://schemas.openxmlformats.org/drawingml/2006/main')
        $relationshipNamespace = 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'
        $coverBlip = $documentXml.SelectSingleNode('//w:drawing[1]//a:blip', $documentNamespaces)
        if ($null -eq $coverBlip) { throw 'La plantilla ERS no contiene una imagen de portada.' }
        $relationshipId = $coverBlip.GetAttribute('embed', $relationshipNamespace)

        $relationshipsEntry = $archive.GetEntry('word/_rels/document.xml.rels')
        $reader = [System.IO.StreamReader]::new($relationshipsEntry.Open())
        try { [xml]$relationshipsXml = $reader.ReadToEnd() } finally { $reader.Dispose() }
        $relationshipNamespaces = [System.Xml.XmlNamespaceManager]::new($relationshipsXml.NameTable)
        $relationshipNamespaces.AddNamespace('rel', 'http://schemas.openxmlformats.org/package/2006/relationships')
        $relationship = $relationshipsXml.SelectSingleNode("//rel:Relationship[@Id='$relationshipId']", $relationshipNamespaces)
        if ($null -eq $relationship -or $relationship.Target -notlike 'media/*') { throw 'No se pudo resolver la imagen de portada de la plantilla.' }
        $mediaPath = 'word/' + $relationship.Target.Replace('\', '/')
        $mediaEntry = $archive.GetEntry($mediaPath)
        if ($null -eq $mediaEntry) { throw "No se encontró el recurso de portada: $mediaPath" }

        $mediaStream = $mediaEntry.Open()
        try {
            $originalImage = [System.Drawing.Image]::FromStream($mediaStream)
            $width = $originalImage.Width
            $height = $originalImage.Height
            $originalImage.Dispose()
        } finally { $mediaStream.Dispose() }

        $sourceImage = [System.Drawing.Image]::FromFile($Path)
        $canvas = [System.Drawing.Bitmap]::new($width, $height)
        $graphics = [System.Drawing.Graphics]::FromImage($canvas)
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $gradient = [System.Drawing.Drawing2D.LinearGradientBrush]::new(
            [System.Drawing.Rectangle]::new(0, 0, $width, $height),
            [System.Drawing.Color]::FromArgb(11, 33, 59),
            [System.Drawing.Color]::FromArgb(25, 103, 139),
            [System.Drawing.Drawing2D.LinearGradientMode]::Horizontal
        )
        $graphics.FillRectangle($gradient, 0, 0, $width, $height)
        $drawHeight = $height
        $drawWidth = [int][Math]::Round($sourceImage.Width * ($drawHeight / $sourceImage.Height))
        $drawLeft = [int](($width - $drawWidth) / 2)
        $graphics.DrawImage($sourceImage, $drawLeft, 0, $drawWidth, $drawHeight)
        $memory = [System.IO.MemoryStream]::new()
        $canvas.Save($memory, [System.Drawing.Imaging.ImageFormat]::Jpeg)

        $mediaEntry.Delete()
        $replacement = $archive.CreateEntry($mediaPath, [System.IO.Compression.CompressionLevel]::Optimal)
        $output = $replacement.Open()
        try { $memory.Position = 0; $memory.CopyTo($output) } finally { $output.Dispose() }
    } finally {
        if ($memory) { $memory.Dispose() }
        if ($gradient) { $gradient.Dispose() }
        if ($graphics) { $graphics.Dispose() }
        if ($canvas) { $canvas.Dispose() }
        if ($sourceImage) { $sourceImage.Dispose() }
        $archive.Dispose()
    }
}

function Set-BodyPageLayout {
    param($Document)
    if ($Document.Sections.Count -lt 3) { throw 'La plantilla requiere secciones separadas para portada, índices y contenido.' }
    $indexPage = $Document.Sections.Item(2).PageSetup
    $indexPage.PaperSize = 7
    $indexPage.Orientation = 0
    $indexPage.TopMargin = 72
    $indexPage.BottomMargin = 72
    $indexPage.LeftMargin = 72
    $indexPage.RightMargin = 72
    $indexNumbers = $Document.Sections.Item(2).Footers.Item(1).PageNumbers
    $indexNumbers.RestartNumberingAtSection = $true
    $indexNumbers.StartingNumber = 1
    $section = $Document.Sections.Item(3)
    $page = $section.PageSetup
    $page.PaperSize = 7
    $page.Orientation = 0
    $page.TopMargin = 37
    $page.BottomMargin = 59
    $page.LeftMargin = 58
    $page.RightMargin = 66
}

function Reset-BodyAfterCover {
    param($Document)
    $secondPage = $Document.GoTo(1, 1, 2)
    $start = [int]$secondPage.Start
    $end = [int]$Document.Content.End - 1
    if ($end -gt $start) { $Document.Range($start, $end).Delete() | Out-Null }
}

function Add-ErsBody {
    param($Document, $Profile, [string]$DiagramPath)
    $tableCaptions = @(
        'Tabla 1. Definiciones', 'Tabla 2. Acrónimos y abreviaturas', 'Tabla 3. Referencias',
        'Tabla 4. Usuario Cliente', 'Tabla 5. Requisitos de interfaces de hardware', 'Tabla 6. Interfaces de software',
        'Tabla 7. Autenticación y Seguridad (Login)', 'Tabla 8. Consulta de movimientos', 'Tabla 9. Registro de depósitos',
        'Tabla 10. Transferencia entre cuentas', 'Tabla 11. Cierre de sesión', 'Tabla 12. Actor: Usuario bancario',
        'Tabla 13. Lista de requisitos funcionales', 'Tabla 14. Gestionar inicio de sesión',
        'Tabla 15. Consultar movimientos', 'Tabla 16. Registrar depósito', 'Tabla 17. Transferir fondos',
        'Tabla 18. Cerrar sesión', 'Tabla 19. Requisito no funcional: tiempo de respuesta',
        'Tabla 20. Requisito no funcional: presentación', 'Tabla 21. Requisito no funcional: facilidad de uso',
        'Tabla 22. Requisito no funcional: seguridad', 'Tabla 23. Requisito no funcional: plataforma',
        'Tabla 24. Requisito no funcional: accesibilidad', 'Tabla 25. Requisito no funcional: mantenimiento'
    )
    $reportTitle = 'ESPECIFICACIÓN DE REQUERIMIENTOS DEL PROYECTO EUREKABANK CON ' + $Profile.Protocolo + ' EN ' + $Profile.Lenguaje.ToUpperInvariant()
    Add-TocFrontMatter $Document $reportTitle @('Ilustración 1. Diagrama de arquitectura de EurekaBank', 'Ilustración 2. Diagrama de casos de uso de EurekaBank') $tableCaptions
    $script:BodyContentStart = [int]$Document.Content.End - 1

    Add-Paragraph $Document 'CASO DE ESTUDIO' -64 | Out-Null
    Add-Paragraph $Document 'EurekaBank integra cuatro canales de cliente con un servicio bancario central. El usuario puede autenticarse, consultar movimientos, registrar depósitos, transferir fondos entre cuentas propias o de terceros y cerrar su sesión.' | Out-Null
    Add-Paragraph $Document ('La variante documentada usa ' + $Profile.Tecnologia + ' La persistencia del servidor utiliza ' + $Profile.BaseDatos + '. El cliente consume el servicio por ' + $Profile.Protocolo + ' y no se conecta directamente a la base de datos.') | Out-Null
    Add-Paragraph $Document 'Las operaciones de dinero se confirman únicamente cuando el servicio devuelve un resultado exitoso. Una transferencia debita la cuenta de origen y acredita la cuenta de destino dentro de la misma transacción.' | Out-Null

    Add-Paragraph $Document '1. Introducción' -2 | Out-Null
    Add-Paragraph $Document ('Este documento especifica los requisitos funcionales, de seguridad y de calidad para EurekaBank con ' + $Profile.Protocolo + ' en ' + $Profile.Lenguaje + '. La solución expone operaciones bancarias comunes a clientes de consola, escritorio, web y móvil.') | Out-Null
    Add-Paragraph $Document 'La especificación fija el comportamiento esperado en las interfaces y en el servicio. La validación de identidad, la autorización de cuentas, las reglas de saldo y la persistencia corresponden al servidor.' | Out-Null

    Add-Paragraph $Document '2. Propósito' -2 | Out-Null
    Add-Paragraph $Document 'Definir requisitos verificables para que el equipo construya y compruebe los clientes y el servicio de EurekaBank, con resultados coherentes en los cuatro canales.' | Out-Null
    Add-Paragraph $Document 'El documento sirve como base para diseño, implementación, pruebas funcionales, revisión de seguridad y aceptación de las operaciones bancarias.' | Out-Null

    Add-Paragraph $Document '3. Alcance' -2 | Out-Null
    Add-Paragraph $Document 'El sistema incluye autenticación, consulta de movimientos, depósitos, transferencias a cuentas propias o de terceros y cierre de sesión. El servicio conserva la autoridad sobre cuentas, saldos, titularidad y movimientos.' | Out-Null
    Add-Paragraph $Document 'Quedan fuera del alcance la apertura y cierre de cuentas, la administración de clientes, la recuperación de contraseñas, la gestión de beneficiarios y la configuración de productos financieros.' | Out-Null
    Add-Paragraph $Document 'Los clientes no guardan credenciales de base de datos ni escriben directamente en la persistencia. La interfaz muestra una operación como exitosa solo cuando recibe una respuesta de confirmación.' | Out-Null

    Add-Paragraph $Document '4. Definiciones, Acrónimos y Abreviaturas' -2 | Out-Null
    Add-Paragraph $Document '4.1 Definiciones' -3 | Out-Null
    $definitions = @(
        @('TÉRMINO', 'DEFINICIÓN'),
        @('Cuenta bancaria', 'Cuenta activa que mantiene saldo en una moneda y pertenece a uno o más titulares.'),
        @('Cuenta de origen', 'Cuenta desde la que se debitan fondos al ejecutar una transferencia.'),
        @('Cuenta de destino', 'Cuenta que recibe los fondos transferidos; puede ser propia o de un tercero.'),
        @('Movimiento', 'Registro financiero asociado a una cuenta, con fecha, tipo, acción e importe.'),
        @('Depósito', 'Operación que acredita un importe positivo en una cuenta activa.'),
        @('Transferencia', 'Operación atómica que debita una cuenta y acredita otra por el mismo importe.'),
        @('Débito 009 SALIDA', 'Movimiento que registra la salida de fondos de la cuenta de origen.'),
        @('Crédito 008 INGRESO', 'Movimiento que registra el ingreso de fondos en la cuenta de destino.'),
        @('Saldo disponible', 'Importe que el servicio permite usar después de considerar operaciones aplicadas.'),
        @('Titularidad', 'Relación que autoriza al usuario autenticado a operar sobre una cuenta.'),
        @('Sesión', 'Estado temporal identificado por un token emitido por el servicio.'),
        @('Token', 'Credencial opaca y temporal que el cliente presenta para operaciones autenticadas.'),
        @('Clave idempotente', 'Identificador de solicitud que evita aplicar dos veces una operación repetida.'),
        @('Idempotencia', 'Propiedad que hace que un reintento válido conserve un único efecto financiero.'),
        @('Transacción', 'Unidad indivisible de persistencia que confirma todos sus cambios o revierte todos.'),
        @('MVC', 'Separación de la interfaz en modelo, vista y controlador.'),
        @('DAO / Repository', 'Componente del servidor que encapsula consultas y operaciones de persistencia.'),
        @('SOAP', 'Protocolo de intercambio de mensajes XML descrito por un contrato de servicios.'),
        @('REST', 'Estilo de interacción HTTP que expone recursos mediante métodos y representaciones.'),
        @('JSON', 'Formato de intercambio de datos usado por la variante REST.'),
        @('WSDL', 'Descripción XML de las operaciones, parámetros y tipos de un servicio SOAP.')
    )
    Add-GridTable $Document $definitions 'Tabla 1. Definiciones' -BoldRows @(1) | Out-Null

    Add-Paragraph $Document '4.2 Acrónimos y abreviaturas' -3 | Out-Null
    $acronyms = @(
        @('Acrónimo/Abreviatura', 'Significado'),
        @('API', 'Interfaz de programación de aplicaciones.'),
        @('DAO', 'Data Access Object; objeto de acceso a datos.'),
        @('ECUD', 'Especificación de casos de uso detallado.'),
        @('ERS', 'Especificación de requisitos de software.'),
        @('HTTP', 'Protocolo de transferencia de hipertexto.'),
        @('JSON', 'JavaScript Object Notation.'),
        @('MVC', 'Modelo-vista-controlador.'),
        @('REST / SOAP', 'Estilos y protocolos de interacción con el servicio.'),
        @('WSDL', 'Web Services Description Language.')
    )
    Add-GridTable $Document $acronyms 'Tabla 2. Acrónimos y abreviaturas' -BoldRows @(1) | Out-Null

    Add-Paragraph $Document '5. Referencias' -2 | Out-Null
    Add-Paragraph $Document 'La especificación considera normas de requisitos y calidad, además de los estándares de comunicación que corresponden a la variante descrita.' | Out-Null
    $references = @(
        @('Referencia', 'Título', 'Descripción', 'Entidad', 'Año'),
        @('1', 'IEEE 830-1998', 'Práctica recomendada para organizar especificaciones de requisitos de software.', 'IEEE', '1998'),
        @('2', 'ISO/IEC 25010', 'Modelo de calidad de producto de software.', 'ISO/IEC', '2011'),
        @('3', 'SOAP 1.2', 'Protocolo de mensajería XML para servicios web.', 'W3C', '2007'),
        @('4', 'HTTP Semantics', 'Semántica y códigos de estado HTTP aplicables a interfaces REST.', 'IETF', '2022')
    )
    Add-GridTable $Document $references 'Tabla 3. Referencias' -BoldRows @(1) | Out-Null

    Add-Paragraph $Document '6. Visión General del Documento' -2 | Out-Null
    Add-Paragraph $Document 'La especificación presenta el contexto del producto, sus interfaces y restricciones; después define requisitos funcionales, reglas bancarias, calidad, rendimiento y criterios para verificar las operaciones.' | Out-Null
    Add-Paragraph $Document 'El ECUD complementa esta especificación con flujos detallados e ilustraciones de casos de uso, actividad y secuencia para las cinco operaciones.' | Out-Null

    Add-Paragraph $Document '7. Descripción General' -2 | Out-Null
    Add-Paragraph $Document '7.1 Perspectiva del Producto' -3 | Out-Null
    Add-Paragraph $Document 'EurekaBank opera como servicio cliente-servidor. Cada canal utiliza controladores MVC y un adaptador de transporte. El servicio valida solicitudes, aplica reglas de negocio y se comunica con la base de datos por repositorios del servidor.' | Out-Null
    Add-Paragraph $Document ('La comunicación de esta variante se realiza mediante ' + $Profile.Protocolo + '. El servicio persiste datos en ' + $Profile.BaseDatos + ' y responde al cliente con el resultado de la operación.') | Out-Null
    Add-Paragraph $Document ('La arquitectura separa los clientes MVC, la comunicación ' + $Profile.Protocolo + ', el servicio de negocio y la persistencia ' + $Profile.BaseDatos + '.') | Out-Null
    Add-Picture $Document $Profile.ArchitectureDiagram 420 | Out-Null
    Add-Paragraph $Document 'Ilustración 1. Diagrama de arquitectura de EurekaBank' -35 | Out-Null

    Add-Paragraph $Document '7.2 Funciones del Producto (software)' -3 | Out-Null
    Add-Paragraph $Document 'Las funciones principales son autenticarse, consultar movimientos, registrar depósitos, transferir fondos y cerrar sesión. La transferencia mantiene correspondencia entre el débito de origen y el crédito de destino.' | Out-Null
    Add-Paragraph $Document '7.3 Condiciones del Entorno' -3 | Out-Null
    Add-Paragraph $Document ('El servidor requiere un entorno compatible con ' + $Profile.Tecnologia + ' y acceso a ' + $Profile.BaseDatos + '. Los clientes requieren conectividad con el endpoint configurado. La disponibilidad efectiva depende de la ejecución del servicio y de la base de datos.') | Out-Null

    Add-Paragraph $Document '7.4 Características de los Usuarios' -3 | Out-Null
    $user = @(
        @('Tipo de usuario', 'Cliente bancario autenticado.'),
        @('Formación', 'No requiere formación técnica.'),
        @('Habilidades', 'Conoce sus credenciales y puede identificar cuentas propias o de terceros para operar.'),
        @('Actividades', 'Inicia sesión, consulta movimientos, registra depósitos, confirma transferencias y cierra sesión.')
    )
    Add-GridTable $Document $user 'Tabla 4. Usuario Cliente' | Out-Null

    Add-Paragraph $Document '7.5 Interfaces Externas' -3 | Out-Null
    Add-Paragraph $Document ('El usuario interactúa con una interfaz de consola, escritorio, web o móvil. La variante integra esos canales con un único contrato de servicio mediante ' + $Profile.Protocolo + '.') | Out-Null
    Add-Paragraph $Document 'El servicio usa una interfaz de persistencia interna. Los clientes no exponen credenciales de la base de datos ni invocan directamente sus protocolos.' | Out-Null

    Add-Paragraph $Document '7.6 Restricciones' -3 | Out-Null
    Add-Paragraph $Document ('La implementación se limita a ' + $Profile.Tecnologia + ' y a la base de datos ' + $Profile.BaseDatos + '. El cliente debe mantener separación MVC y consumir el endpoint definido por configuración. Las credenciales de persistencia solo se configuran en el entorno del servidor.') | Out-Null
    Add-Paragraph $Document '7.7 Suposiciones y Dependencias' -3 | Out-Null
    Add-Paragraph $Document 'Se supone que el servicio y la persistencia están desplegados y que el endpoint configurado corresponde al entorno de ejecución. El servicio debe validar titularidad, estado, moneda, saldo e idempotencia; el cliente no toma esas decisiones por su cuenta.' | Out-Null

    Add-Paragraph $Document '8. Especificación de Requerimientos' -2 | Out-Null
    Add-Paragraph $Document '8.1 Requisitos comunes de las interfaces' -3 | Out-Null
    Add-Paragraph $Document 'Todos los canales comunican el estado de la operación y muestran mensajes comprensibles para validaciones, resultados y fallos de conexión.' | Out-Null
    Add-Paragraph $Document '8.1.1 Interfaces de usuario' -4 | Out-Null
    Add-Paragraph $Document 'La consola usa menús textuales. Escritorio, web y móvil presentan formularios para credenciales, cuentas e importes, y solicitan confirmación antes de depósitos o transferencias.' | Out-Null
    Add-Paragraph $Document '8.1.2 Interfaces de hardware' -4 | Out-Null
    $hardware = @(
        @('Componente', 'Uso', 'Perfil', 'Procesador', 'Memoria', 'Red / almacenamiento'),
        @('Servidor', 'Ejecuta servicio y lógica bancaria.', 'Equipo o máquina virtual.', '2 núcleos o más.', '4 GB o más.', 'Red local; SSD recomendado.'),
        @('Estación de trabajo', 'Ejecuta cliente de consola o escritorio.', 'PC o portátil.', 'Procesador compatible con el SO.', '4 GB o más.', 'Conexión IP al servicio.'),
        @('Monitor', 'Presenta formularios y resultados.', 'Pantalla de escritorio.', 'No aplica.', 'No aplica.', 'Resolución mínima 1280 × 720.'),
        @('Teclado', 'Captura credenciales, cuentas e importes.', 'Periférico estándar.', 'No aplica.', 'No aplica.', 'Conexión USB o integrada.'),
        @('Dispositivo móvil', 'Ejecuta la aplicación móvil.', 'Teléfono compatible.', 'CPU ARM compatible.', '2 GB o más.', 'Wi-Fi o red móvil.'),
        @('Servidor de datos', 'Almacena cuentas y movimientos.', $Profile.BaseDatos, 'Según motor instalado.', '4 GB o más.', 'Disco persistente y respaldo.'),
        @('Red', 'Transporta solicitudes y respuestas.', 'LAN o Internet.', 'No aplica.', 'No aplica.', 'HTTP(S) permitido por la política local.')
    )
    Add-GridTable $Document $hardware 'Tabla 5. Requisitos de interfaces de hardware' -BoldRows @(1) | Out-Null

    Add-Paragraph $Document '8.1.3 Interfaces de software' -4 | Out-Null
    $software = @(
        @('Componente', 'Tecnología / versión', 'Interfaz', 'Propósito'),
        @('Servidor', $Profile.Tecnologia, $Profile.Protocolo, 'Valida sesiones y ejecuta operaciones bancarias.'),
        @('Persistencia', $Profile.BaseDatos, 'Driver del servidor', 'Consulta cuentas, saldos y movimientos.'),
        @('Cliente de consola', $Profile.Lenguaje, $Profile.Protocolo, 'Ofrece menú de texto para las cinco operaciones.'),
        @('Cliente de escritorio', $(if ($Profile.Lenguaje -eq 'Java') { 'Java Swing' } else { '.NET WPF' }), $Profile.Protocolo, 'Presenta formularios de escritorio.'),
        @('Cliente web', $(if ($Profile.Lenguaje -eq 'Java') { 'JSP / Servlet MVC' } else { 'ASP.NET Core MVC' }), $Profile.Protocolo, 'Permite operar desde navegador.'),
        @('Cliente móvil', $(if ($Profile.Lenguaje -eq 'Java') { 'Android' } else { '.NET MAUI' }), $Profile.Protocolo, 'Permite operar desde dispositivo móvil.'),
        @('Configuración', 'Variables de entorno del servidor', 'Configuración externa', 'Evita incluir secretos en el código.'),
        @('Sesión', 'Token temporal', 'Cabecera o contexto de solicitud', 'Autoriza las funciones protegidas.'),
        @('Registro', 'Datos operativos sin secretos', 'Salida de diagnóstico', 'Permite investigar fallos sin revelar credenciales.'),
        @('Cliente común', 'Modelo-vista-controlador', 'Adaptador de transporte', 'Mantiene separadas interfaz y comunicación.')
    )
    Add-GridTable $Document $software 'Tabla 6. Interfaces de software' -BoldRows @(1) | Out-Null

    Add-Paragraph $Document '8.1.4 Interfaces de comunicación' -4 | Out-Null
    if ($Profile.Protocolo -eq 'SOAP') {
        Add-Paragraph $Document 'El cliente envía solicitudes SOAP con contenido XML al servicio y procesa la respuesta definida por el contrato. El token se transmite de acuerdo con la operación protegida y no se registra en texto claro.' | Out-Null
    } else {
        Add-Paragraph $Document 'El cliente envía solicitudes HTTP a recursos REST y procesa cuerpos JSON. Los códigos HTTP y el cuerpo de respuesta distinguen validación, autorización, éxito y error.' | Out-Null
    }
    Add-Paragraph $Document ('Endpoint predeterminado de referencia: ' + $Profile.Endpoint + '. Configuración del cliente: ' + $Profile.EndpointVar + '. El valor puede cambiar por entorno; la documentación no afirma que el servicio esté en ejecución.') | Out-Null

    Add-Paragraph $Document '8.2 Requisitos Funcionales' -3 | Out-Null
    Add-Paragraph $Document '8.2.1. Objetivo General' -4 | Out-Null
    Add-Paragraph $Document 'Proteger la información bancaria y permitir operaciones coherentes mediante un servicio que centraliza autenticación, reglas de negocio y persistencia.' | Out-Null
    Add-Paragraph $Document '8.2.2. Objetivos Específicos' -4 | Out-Null
    $objectives = @(
        @('OBJ-001', 'Autenticar al usuario', 'Validar credenciales y emitir un token temporal para la sesión.', 'Alta'),
        @('OBJ-002', 'Consultar movimientos', 'Presentar los movimientos de una cuenta sin alterar su saldo.', 'Alta'),
        @('OBJ-003', 'Registrar depósitos', 'Acreditar un importe positivo y registrar el movimiento correspondiente.', 'Alta'),
        @('OBJ-004', 'Transferir fondos', 'Debitar origen, acreditar destino y registrar ambos movimientos atómicamente.', 'Alta'),
        @('OBJ-005', 'Cerrar sesión', 'Revocar el token remoto y eliminar el estado autenticado local.', 'Alta')
    )
    for ($i = 0; $i -lt $objectives.Count; $i++) {
        $n = $i + 7
        $row = @(
            @($objectives[$i][0], $objectives[$i][1]),
            @('Descripción', $objectives[$i][2]),
            @('Importancia', $objectives[$i][3]),
            @('Comentarios', 'Aplicable a los cuatro canales de EurekaBank.')
        )
        Add-GridTable $Document $row $tableCaptions[$n - 1] | Out-Null
    }

    Add-Paragraph $Document '8.2.3. Actores' -4 | Out-Null
    $actors = @(
        @('ACT-001', 'Usuario bancario autenticado'),
        @('Descripción', 'Persona que utiliza cualquiera de los cuatro clientes para consultar o ejecutar operaciones sobre cuentas autorizadas.'),
        @('Comentarios', 'El servicio valida la identidad, la titularidad y los permisos para cada solicitud.')
    )
    Add-GridTable $Document $actors 'Tabla 12. Actor: Usuario bancario' | Out-Null

    Add-Paragraph $Document '8.2.4. Lista de requisitos funcionales' -4 | Out-Null
    Add-Paragraph $Document 'La lista consolida las funciones obligatorias y su relación con el actor principal.' | Out-Null
    Add-Paragraph $Document 'El diagrama siguiente presenta las cinco operaciones y el actor bancario.' | Out-Null
    Add-Picture $Document $DiagramPath 420 | Out-Null
    Add-Paragraph $Document 'Ilustración 2. Diagrama de casos de uso de EurekaBank' -35 | Out-Null
    $requirements = @(
        @('Servicio EurekaBank', '', '', ''),
        @('Lista de requisitos funcionales', '', '', ''),
        @('Código', 'Requisito', 'Caso de uso', 'Actor'),
        @('RF-01', 'Validar credenciales y emitir sesión temporal.', 'Iniciar sesión (ECUD1)', 'Usuario'),
        @('RF-02', 'Consultar movimientos de una cuenta autorizada.', 'Consultar movimientos (ECUD2)', 'Usuario'),
        @('RF-03', 'Acreditar depósito y registrar su movimiento.', 'Registrar depósito (ECUD3)', 'Usuario'),
        @('RF-04', 'Debitar origen y acreditar destino en una transacción.', 'Transferir fondos (ECUD5)', 'Usuario'),
        @('RF-05', 'Revocar sesión y eliminar estado local.', 'Cerrar sesión (ECUD4)', 'Usuario')
    )
    Add-GridTable $Document $requirements 'Tabla 13. Lista de requisitos funcionales' -MergeAllColumnsRows @(1, 2) -BoldRows @(1, 2, 3) | Out-Null

    Add-Paragraph $Document '8.2.5 Requisitos Funcionales' -4 | Out-Null
    $functional = @(
        @('RQF-001', 'Gestionar inicio de sesión', 'El servicio debe validar usuario y contraseña. Ante éxito debe emitir un token temporal. El cliente no guarda la contraseña después de autenticar.', 'OBJ-001'),
        @('RQF-002', 'Consultar movimientos', 'El servicio debe validar el token y el acceso a la cuenta, y devolver movimientos ordenados por fecha descendente. Una cuenta sin movimientos debe producir una respuesta vacía válida.', 'OBJ-002'),
        @('RQF-003', 'Registrar depósito', 'El servicio debe comprobar cuenta activa e importe mayor que cero. Debe actualizar saldo y registrar el movimiento dentro de una transacción.', 'OBJ-003'),
        @('RQF-004', 'Transferir fondos', 'El servicio debe validar token, titularidad de origen, cuentas distintas y activas, moneda común, saldo e idempotencia. Debe registrar débito 009 SALIDA y crédito 008 INGRESO junto con el cambio de saldos en una transacción.', 'OBJ-004'),
        @('RQF-005', 'Cerrar sesión', 'El servicio debe revocar el token. El cliente debe borrar el token y la identidad local aun cuando el servicio no responda.', 'OBJ-005')
    )
    for ($i = 0; $i -lt $functional.Count; $i++) {
        $n = $i + 14
        $row = @(
            @($functional[$i][0], $functional[$i][1]),
            @('Descripción', $functional[$i][2]),
            @('Objetivo', $functional[$i][3]),
            @('Importancia', 'Alta'),
            @('Estado', 'Aprobado para implementación.'),
            @('Estabilidad', 'Alta; cambio sujeto a versión del contrato.'),
            @('Comentarios', 'El cliente confirma la operación solo ante respuesta exitosa del servicio.')
        )
        Add-GridTable $Document $row $tableCaptions[$n - 1] | Out-Null
    }

    Add-Paragraph $Document '8.3 Requisitos No Funcionales' -3 | Out-Null
    Add-Paragraph $Document '8.3.1 Objetivo General' -4 | Out-Null
    Add-Paragraph $Document 'Proteger datos, conservar integridad financiera y ofrecer respuestas comprensibles en todos los canales.' | Out-Null
    $quality = @(
        @('RNF-001', 'Tiempo de respuesta', 'En condiciones normales de red y carga de prueba, las consultas deben responder en hasta 3 segundos y las operaciones de escritura en hasta 5 segundos. El cliente debe mostrar espera cuando se supera el primer segundo.', 'Alta'),
        @('RNF-002', 'Presentación y legibilidad', 'Las cuatro interfaces deben mostrar cuentas, importes, fechas y resultados sin recortes ni superposición. Los importes usan dos decimales y separadores consistentes.', 'Media'),
        @('RNF-003', 'Facilidad de uso', 'Los formularios deben identificar campos obligatorios, validar formatos antes del envío y pedir confirmación para depósitos y transferencias.', 'Media'),
        @('RNF-004', 'Seguridad', 'Las credenciales de base de datos solo se configuran en el servidor. Las contraseñas y tokens no se escriben en logs. El servidor valida sesión y titularidad en cada operación protegida.', 'Alta'),
        @('RNF-005', 'Plataforma y arquitectura', 'La implementación debe usar la plataforma definida para la variante. Los clientes aplican MVC; el servidor separa controlador de servicio, reglas de negocio y repositorios.', 'Alta'),
        @('RNF-006', 'Accesibilidad y compatibilidad', 'La interfaz debe permitir navegación por teclado, etiquetas comprensibles, contraste suficiente y visualización en tamaños de pantalla compatibles con cada canal.', 'Media'),
        @('RNF-007', 'Mantenimiento', 'Los contratos, reglas y repositorios deben mantener responsabilidades separadas. Las operaciones de base de datos deben cerrar recursos en todos los caminos de salida.', 'Alta')
    )
    for ($i = 0; $i -lt $quality.Count; $i++) {
        $n = $i + 19
        $row = @(
            @($quality[$i][0], $quality[$i][1]),
            @('Descripción', $quality[$i][2]),
            @('Importancia', $quality[$i][3]),
            @('Estado', 'Criterio de aceptación para verificación.')
        )
        Add-GridTable $Document $row $tableCaptions[$n - 1] | Out-Null
    }

    Add-Paragraph $Document '8.4 Otros Requerimientos' -3 | Out-Null
    Add-Paragraph $Document '8.4.1 Restricciones de Diseño' -4 | Out-Null
    Add-Paragraph $Document 'Las vistas no contienen reglas de persistencia. El acceso a datos se limita al servidor y se encapsula en repositorios. La creación y cierre de conexiones se controlan con recursos autocerrables o bloques finally.' | Out-Null
    Add-Paragraph $Document '8.4.2 Restricciones de Hardware' -4 | Out-Null
    Add-Paragraph $Document 'El cliente requiere un dispositivo compatible con su sistema operativo y conectividad IP. El servidor y la base de datos deben disponer de recursos para atender solicitudes concurrentes.' | Out-Null
    Add-Paragraph $Document '8.4.3 Atributos de calidad' -4 | Out-Null
    Add-Paragraph $Document 'La integridad requiere transacciones atómicas para depósitos y transferencias. La seguridad exige validación en el servidor, expiración o revocación del token y protección de credenciales. La interoperabilidad exige respuestas consistentes en los canales.' | Out-Null

    Add-Paragraph $Document '9. Requisitos de Rendimiento' -2 | Out-Null
    Add-Paragraph $Document 'El servicio debe aceptar solicitudes concurrentes sin confirmar saldos parciales. Las pruebas de rendimiento deben medir consultas y escrituras con datos representativos y registrar latencia sin incluir secretos ni tokens.' | Out-Null
    Add-Paragraph $Document '9.1. Requisitos de Interfaces Externas' -3 | Out-Null
    Add-Paragraph $Document '9.1.1. Interfaces de Usuario' -4 | Out-Null
    Add-Paragraph $Document 'Los clientes deben mantener disponibles las acciones de consulta y operación, comunicar resultados y permitir recuperación de errores de validación.' | Out-Null
    Add-Paragraph $Document '9.1.2. Interfaces de Hardware' -4 | Out-Null
    Add-Paragraph $Document 'El servidor debe disponer de almacenamiento persistente y conectividad con la base de datos. Los clientes requieren conexión al endpoint configurado.' | Out-Null
    Add-Paragraph $Document '9.1.3. Interfaces de Software' -4 | Out-Null
    Add-Paragraph $Document ('La variante integra ' + $Profile.Lenguaje + ', ' + $Profile.Protocolo + ' y ' + $Profile.BaseDatos + '. La versión exacta queda fijada en la configuración de construcción y despliegue.') | Out-Null
    Add-Paragraph $Document '9.1.4. Interfaces de Comunicación' -4 | Out-Null
    Add-Paragraph $Document 'Los errores de transporte no deben presentarse como confirmaciones de operación. Ante resultado incierto, el cliente debe consultar o reintentar con la misma clave idempotente cuando la operación lo permita.' | Out-Null

    Add-Paragraph $Document 'RECOMENDACIONES' -2 | Out-Null
    Add-Paragraph $Document 'Mantener los contratos de comunicación versionados, probar simultaneidad en transferencias, verificar restricciones de saldo y revisar que todas las rutas cierren sus conexiones y sentencias.' | Out-Null
    Add-Paragraph $Document 'CONCLUSIONES' -2 | Out-Null
    Add-Paragraph $Document 'La separación entre clientes y servicio centraliza las reglas bancarias. La atomicidad de débito, crédito y movimientos es necesaria para conservar saldos consistentes.' | Out-Null
    Add-Paragraph $Document 'BIBLIOGRAFÍA' -2 | Out-Null
    Add-Paragraph $Document 'IEEE. Recommended Practice for Software Requirements Specifications, IEEE 830-1998.' | Out-Null
    Add-Paragraph $Document 'ISO/IEC. Systems and software quality models, ISO/IEC 25010.' | Out-Null
    Add-Paragraph $Document 'W3C. SOAP Version 1.2 Part 1: Messaging Framework.' | Out-Null
    Add-Paragraph $Document 'IETF. HTTP Semantics, RFC 9110.' | Out-Null
}

function Get-OperationDescription {
    param($Profile, $Case)
    if ($Profile.Protocolo -eq 'SOAP') {
        switch ($Case.Codigo) {
            'Login' { return 'login(usuario, contrasena) por SOAP.' }
            'Movimientos' { return 'traerMovimientos(cuenta) por SOAP.' }
            'Deposito' { return 'regDeposito(cuenta, importe) por SOAP.' }
            'Transferencia' { return 'transferir(token, cuentaOrigen, cuentaDestino, importe, claveOperacion) por SOAP.' }
            'CerrarSesion' { return 'cerrarSesion(token) por SOAP.' }
        }
    }
    switch ($Case.Codigo) {
        'Login' { return 'POST /api/eureka/login con usuario y contrasena en JSON.' }
        'Movimientos' { return 'GET /api/eureka/movimientos?cuenta={cuenta}.' }
        'Deposito' { return 'POST /api/eureka/depositos con cuenta e importe en JSON.' }
        'Transferencia' { return 'POST /api/eureka/transferencias con token, cuentas, importe y claveOperacion en JSON.' }
        'CerrarSesion' { return 'POST /api/eureka/logout con token de sesión.' }
    }
}

function Set-EcudCell {
    param($Table, [int]$Row, [int]$Column, [string]$Text)
    $Table.Cell($Row, $Column).Range.Text = $Text
}

function Get-EcudFlowPairs {
    param($Case)
    $pairs = @()
    switch ($Case.Codigo) {
        'Login' {
            $pairs += ,@('Usuario abre la aplicación y elige Iniciar sesión.', 'El sistema presenta el formulario de acceso.')
            $pairs += ,@('Ingresa usuario y contraseña.', 'Valida que ambos campos tengan valor antes de enviar.')
            $pairs += ,@('Espera la respuesta del servicio.', 'El servicio valida las credenciales y emite un token temporal.')
            $pairs += ,@('Recibe acceso al menú.', 'El cliente conserva el token y descarta la contraseña.')
        }
        'Movimientos' {
            $pairs += ,@('Elige Consultar movimientos.', 'Solicita identificar la cuenta.')
            $pairs += ,@('Selecciona o ingresa la cuenta.', 'Valida sesión y formato de cuenta antes de consultar.')
            $pairs += ,@('Espera el resultado.', 'El servicio verifica autorización y consulta movimientos.')
            $pairs += ,@('Revisa la lista o mensaje sin resultados.', 'Presenta movimientos ordenados por fecha descendente.')
        }
        'Deposito' {
            $pairs += ,@('Elige Registrar depósito.', 'Solicita cuenta e importe.')
            $pairs += ,@('Ingresa cuenta e importe.', 'Valida formato e importe positivo y pide confirmación.')
            $pairs += ,@('Confirma la operación.', 'El servicio valida token, cuenta activa e importe.')
            $pairs += ,@('Espera el resultado.', 'El servicio actualiza saldo y movimiento en una transacción; luego confirma.')
        }
        'CerrarSesion' {
            $pairs += ,@('Elige Cerrar sesión.', 'Envía al servicio la solicitud de revocación del token.')
            $pairs += ,@('Espera la respuesta.', 'El servicio revoca el token activo.')
            $pairs += ,@('Regresa a la pantalla de acceso.', 'El cliente elimina token, identidad y datos privados locales.')
            $pairs += ,@('', 'Si no hay respuesta, limpia la sesión local y no informa que la revocación remota fue confirmada.')
        }
        'Transferencia' {
            $pairs += ,@('Elige Transferir e ingresa origen, destino e importe.', 'Valida campos, formato de cuenta, cuentas distintas e importe positivo.')
            $pairs += ,@('Confirma la transferencia.', 'Envía token, cuentas, importe y clave idempotente.')
            $pairs += ,@('Espera la validación.', 'El servicio comprueba token, titularidad de origen, estado, moneda e idempotencia.')
            $pairs += ,@('Espera el resultado financiero.', 'El servicio bloquea cuentas en orden determinista y verifica saldo disponible.')
            $pairs += ,@('Recibe comprobante solo tras éxito.', 'En una transacción debita origen, acredita destino y registra 009 SALIDA y 008 INGRESO.')
        }
    }
    return ,$pairs
}

function Add-EcudDiagramsToCell {
    param($Document, $Cell, [string]$CasePath, [string]$ActivityPath, [string]$SequencePath, [int]$FirstCaptionNumber, [string]$CaseName)
    $Cell.Range.Text = ''
    $Cell.Range.ParagraphFormat.Alignment = 1
    $Cell.Range.Font.Name = 'Arial'
    $Cell.Range.Font.Size = 10
    $Cell.Range.Font.Bold = 0
    $Cell.Range.Font.Italic = 0
    $paths = @($CasePath, $ActivityPath, $SequencePath)
    $labels = @('Caso de uso', 'Actividad', 'Secuencia')
    for ($i = 0; $i -lt 3; $i++) {
        if (-not (Test-Path -LiteralPath $paths[$i] -PathType Leaf)) { throw "Falta diagrama ECUD: $($paths[$i])" }
        $position = [int]$Cell.Range.End - 1
        $insert = $Document.Range($position, $position)
        $picture = $insert.InlineShapes.AddPicture($paths[$i], $false, $true)
        $picture.LockAspectRatio = -1
        if ($picture.Width -gt 390) { $picture.Width = 390 }
        if ($picture.Height -gt 600) { $picture.Height = 600 }
        $picture.Range.ParagraphFormat.Alignment = 1
        $captionStart = [int]$picture.Range.End
        $captionRange = $Document.Range($captionStart, $captionStart)
        $caption = 'Ilustración ' + ($FirstCaptionNumber + $i) + '. ' + $labels[$i] + ' ' + $CaseName
        $captionRange.InsertAfter("`r" + $caption + "`r")
        $captionText = $Document.Range($captionStart + 1, $captionStart + 1 + $caption.Length)
        $captionText.Font.Name = 'Arial'
        $captionText.Font.Size = 14
        $captionText.Font.Bold = 0
        $captionText.Font.Italic = 0
        $captionText.ParagraphFormat.Alignment = 1
        $captionPrefix = $Document.Range($captionStart + 1, $captionStart + 1 + ('Ilustración ' + ($FirstCaptionNumber + $i) + '.').Length)
        $captionPrefix.Font.Bold = 1
        $Cell.Range.ParagraphFormat.Alignment = 1
    }
}

function Add-EcudCaseTable {
    param($Document, $Profile, $Case, [string]$DiagramDirectory, [int]$CaseNumber, [string]$TableCaption)
    $flowPairs = Get-EcudFlowPairs $Case
    $flowCount = $flowPairs.Count
    $rowCount = 17 + $flowCount
    Add-Paragraph $Document $TableCaption -5 | Out-Null
    $position = [int]$Document.Content.End - 1
    $table = $Document.Tables.Add($Document.Range($position, $position), $rowCount, 4)
    $table.Borders.Enable = 1
    $table.AllowAutoFit = $false
    $table.AutoFitBehavior(0)
    $table.Range.Font.Name = 'Arial'
    $table.Range.Font.Size = 12
    $table.Range.Font.Bold = 0
    $table.Range.Font.Italic = 0
    $table.Range.ParagraphFormat.SpaceAfter = 2
    foreach ($row in @(1, 2, 3, 4, 6, 8, 9, 10)) { $table.Cell($row, 2).Merge($table.Cell($row, 4)) }
    foreach ($row in @(11, 12, (14 + $flowCount), (15 + $flowCount), (16 + $flowCount), (17 + $flowCount))) {
        $table.Cell($row, 1).Merge($table.Cell($row, 4))
    }
    $table.Cell(13, 3).Merge($table.Cell(13, 4))
    $table.Cell(13, 1).Merge($table.Cell(13, 2))
    for ($row = 14; $row -lt (14 + $flowCount); $row++) {
        $table.Cell($row, 3).Merge($table.Cell($row, 4))
        $table.Cell($row, 1).Merge($table.Cell($row, 2))
    }
    $table.AllowAutoFit = $false
    $table.AutoFitBehavior(0)
    try {
        $table.Cell(1, 1).Width = 127
        $table.Cell(1, 2).Width = 324
        $table.Cell(5, 1).Width = 127
        $table.Cell(5, 2).Width = 112
        $table.Cell(5, 3).Width = 114
        $table.Cell(5, 4).Width = 97
    } catch { }

    Set-EcudCell $table 1 1 'Nombre Caso de Uso:'
    Set-EcudCell $table 1 2 $Case.Nombre
    Set-EcudCell $table 2 1 'Identificador:'
    Set-EcudCell $table 2 2 $Case.Id
    Set-EcudCell $table 3 1 'Descripción:'
    Set-EcudCell $table 3 2 $Case.Descripcion
    Set-EcudCell $table 4 1 'Meta:'
    Set-EcudCell $table 4 2 $Case.Meta
    Set-EcudCell $table 5 1 'Estado:'
    Set-EcudCell $table 5 2 'Aprobado'
    Set-EcudCell $table 5 3 'Versión:'
    Set-EcudCell $table 5 4 '1.0'
    Set-EcudCell $table 6 1 'Autores:'
    Set-EcudCell $table 6 2 'Grupo 09 · NRC 36884'
    Set-EcudCell $table 7 1 'Fecha creación:'
    Set-EcudCell $table 7 2 '22/10/2026'
    Set-EcudCell $table 7 3 'Fecha modificación:'
    Set-EcudCell $table 7 4 '22/10/2026'
    Set-EcudCell $table 8 1 'Precondiciones:'
    Set-EcudCell $table 8 2 $Case.Pre
    Set-EcudCell $table 9 1 'Post condiciones:'
    Set-EcudCell $table 9 2 $Case.Post
    Set-EcudCell $table 10 1 'Gráfico:'
    Set-EcudCell $table 11 1 'Flujo Básico'
    Set-EcudCell $table 12 1 $Case.Nombre
    Set-EcudCell $table 13 1 'Actor'
    Set-EcudCell $table 13 2 'Sistema'
    for ($i = 0; $i -lt $flowCount; $i++) {
        Set-EcudCell $table (14 + $i) 1 (($i * 2 + 1).ToString() + '. ' + $flowPairs[$i][0])
        Set-EcudCell $table (14 + $i) 2 (($i * 2 + 2).ToString() + '. ' + $flowPairs[$i][1])
    }
    $alternativeHeader = 14 + $flowCount
    Set-EcudCell $table $alternativeHeader 1 'Flujo Alternativo'
    $alternativeText = $Case.Alternos
    $extraRules = ''
    switch ($Case.Codigo) {
        'Login' { $alternativeText = 'A1. Campos vacíos: no enviar y conservar el formulario. A2. Credenciales incorrectas: rechazar sin crear sesión. A3. Servicio no disponible: informar y permitir reintento.' }
        'Movimientos' { $alternativeText = 'A1. Sesión ausente: volver al acceso. A2. Sin movimientos: mostrar una lista vacía. A3. Cuenta inválida o error de servicio: informar sin alterar datos.' }
        'Deposito' { $alternativeText = 'A1. Cuenta o importe inválido: no enviar. A2. Cuenta inexistente o inactiva: rechazar sin confirmar. A3. Resultado incierto: verificar el movimiento antes de reintentar.' }
    }
    if ($Case.Codigo -eq 'Transferencia') {
        $alternativeText = 'A1. Campos inválidos o cuentas iguales: no enviar. A2. Token inválido o falta de titularidad de origen: rechazar. A3. Cuenta inactiva, moneda distinta o saldo insuficiente: rechazar sin cambios. A4. Clave repetida con los mismos datos: devolver resultado sin duplicar. A5. Clave usada con otros datos: rechazar. A6. Error: revertir todo y reintentar con la misma clave.'
        $extraRules = 'Reglas: cuenta de origen y destino distintas, activas y en la misma moneda; el usuario debe ser titular del origen. La clave idempotente se reutiliza solo con los mismos datos. El débito 009 SALIDA, el crédito 008 INGRESO, los saldos y la transferencia se confirman en una transacción; ante error se revierten todos.'
    }
    if ($Case.Codigo -eq 'CerrarSesion') {
        $alternativeText = 'A1. Sin sesión local: limpiar el estado y mostrar el acceso. A2. El servicio no responde: borrar el token local y no afirmar que la revocación remota se confirmó.'
        $extraRules = 'Seguridad: se revoca el token remoto y se elimina el token local. Si la comunicación falla, el cliente limpia su sesión, pero no afirma que la revocación del servicio fue confirmada.'
    }
    $alternativeContent = [regex]::Replace($alternativeText, '\s+(?=A\d+\.)', "`r")
    if ($extraRules) { $alternativeContent += "`r" + $extraRules }
    Set-EcudCell $table ($alternativeHeader + 1) 1 $alternativeContent
    try { $table.Rows.Item($alternativeHeader + 1).AllowBreakAcrossPages = 0 } catch { }
    Set-EcudCell $table ($alternativeHeader + 2) 1 ('Casos de uso incluidos: ' + $(if ($Case.Codigo -in @('Movimientos', 'Deposito', 'CerrarSesion', 'Transferencia')) { 'Iniciar sesión (ECUD1).' } else { 'Ninguno.' }))
    Set-EcudCell $table ($alternativeHeader + 3) 1 'Casos de uso extendidos: Ninguno.'
    foreach ($row in @(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
        try {
            $label = $table.Cell($row, 1)
            $label.Range.Bold = 1
            $label.Range.ParagraphFormat.Alignment = 2
            $label.VerticalAlignment = 1
        } catch { }
    }
    foreach ($row in @(1, 5, 11, 12, 13, $alternativeHeader)) {
        try {
            foreach ($cell in $table.Rows.Item($row).Cells) { $cell.Range.ParagraphFormat.Alignment = 1 }
        } catch { }
    }
    foreach ($row in @(11, $alternativeHeader)) {
        try { $table.Cell($row, 1).Range.Bold = 1 } catch { }
    }

    $caseDiagram = Join-Path $DiagramDirectory ('GR09_CasosUso_CU_' + $Case.Codigo + '.png')
    $activityDiagram = Join-Path $Profile.ActivityDiagramDirectory ('GR09_Actividad_DA_' + $Case.Codigo + '.png')
    $sequenceDiagram = Join-Path $Profile.SequenceDiagramDirectory ('GR09_Secuencia_DS_' + $Case.Codigo + '.png')
    $graphCell = $table.Cell(10, 2)
    Add-EcudDiagramsToCell -Document $Document -Cell $graphCell -CasePath $caseDiagram -ActivityPath $activityDiagram -SequencePath $sequenceDiagram -FirstCaptionNumber (($CaseNumber - 1) * 3 + 1) -CaseName $Case.Nombre
    foreach ($cell in $table.Range.Cells) { $cell.Shading.BackgroundPatternColor = 14408667 }
    Add-Paragraph $Document '' -1 | Out-Null
    return $table
}

function Add-EcudBody {
    param($Document, $Profile, [string]$DiagramDirectory)
    $figureNames = @()
    $tableNames = @()
    foreach ($case in $casos) {
        $figureNames += 'Ilustración ' + ($figureNames.Count + 1) + '. Caso de uso ' + $case.Nombre
        $figureNames += 'Ilustración ' + ($figureNames.Count + 1) + '. Actividad ' + $case.Nombre
        $figureNames += 'Ilustración ' + ($figureNames.Count + 1) + '. Secuencia ' + $case.Nombre
        $tableNames += 'Tabla ' + ($tableNames.Count + 1) + '. ECUD ' + $case.Nombre
    }
    $reportTitle = 'ESPECIFICACIÓN DE CASOS DE USO DETALLADO PARA EUREKABANK CON ' + $Profile.Protocolo + ' EN ' + $Profile.Lenguaje.ToUpperInvariant()
    Add-TocFrontMatter $Document $reportTitle $figureNames $tableNames
    $script:BodyContentStart = [int]$Document.Content.End - 1
    $sectionHeading = Add-Paragraph $Document 'ESPECIFICACIÓN DE CASOS DE USO DETALLADO' -2
    $sectionHeading.Range.Font.Name = 'Arial'
    $sectionHeading.Range.Font.Size = 12
    $sectionHeading.Range.Font.Bold = 1
    Add-Paragraph $Document ('Las especificaciones describen EurekaBank con ' + $Profile.Protocolo + ' en ' + $Profile.Lenguaje + '. Tecnología del servidor: ' + $Profile.Tecnologia + ' Persistencia: ' + $Profile.BaseDatos + '. Cada caso incluye diagramas de caso de uso, actividad y secuencia.') | Out-Null
    foreach ($caseIndex in 0..($casos.Count - 1)) {
        $case = $casos[$caseIndex]
        $number = ($caseIndex * 3) + 1
        $tableCaption = 'Tabla ' + ($caseIndex + 1) + '. ECUD ' + $case.Nombre + '.'
        $caseHeading = Add-Paragraph $Document ($case.Id + ' — ' + $case.Nombre.ToUpperInvariant()) -2 ($caseIndex -gt 0)
        $caseHeading.Range.Font.Name = 'Arial'
        $caseHeading.Range.Font.Size = 12
        $caseHeading.Range.Font.Bold = 1
        Add-Paragraph $Document ('A continuación, se presenta ' + $case.Id + ' para EurekaBank mediante ' + $Profile.Protocolo + ' en ' + $Profile.Lenguaje + '.') | Out-Null
        Add-EcudCaseTable -Document $Document -Profile $Profile -Case $case -DiagramDirectory $DiagramDirectory -CaseNumber ($caseIndex + 1) -TableCaption $tableCaption | Out-Null
    }
}

$referenceRoot = Join-Path (Split-Path $Raiz -Parent) 'TI.1.1\WS_ConUni_Java_SOAP_GR5-master\01. UML'
$ersTemplate = Join-Path $referenceRoot '01. ERS\ERS_SOAP_JAVA_SINBDD_GR05.docx'
$ecudTemplate = Join-Path $referenceRoot '02. ECUD\ECUD_SOAP_JAVA_SINBDD_GR5.docx'
if (-not (Test-Path -LiteralPath $ersTemplate -PathType Leaf) -or -not (Test-Path -LiteralPath $ecudTemplate -PathType Leaf)) {
    throw 'No se encontraron las plantillas ERS y ECUD de referencia T1.1.'
}

$word = $null
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = 0
    foreach ($profile in $perfiles) {
        $projectPath = Join-Path $Raiz $profile.Carpeta
        $umlPath = Join-Path $projectPath '01. UML'
        $ersPath = Join-Path (Join-Path $umlPath '01. ERS') ('ERS_EUREKABANK_' + $profile.Clave + '_CONBDD_GR09.docx')
        $ecudPath = Join-Path (Join-Path $umlPath '02. ECUD') ('ECUD_EUREKABANK_' + $profile.Clave + '_CONBDD_GR09.docx')
        $diagramRoot = Join-Path $umlPath '03. DIAGRAMAS UML'
        $caseDiagramDir = Join-Path $diagramRoot '01_CASOS_DE_USO'
        $profile.ActivityDiagramDirectory = Join-Path $diagramRoot '02_ACTIVIDAD'
        $profile.SequenceDiagramDirectory = Join-Path $diagramRoot '03_SECUENCIA'
        $profile.ArchitectureDiagram = Join-Path (Join-Path $diagramRoot '05_ARQUITECTURA') 'GR09_Arquitectura_Arquitectura EurekaBank.png'
        $coverImage = Join-Path (Join-Path $projectPath '05. DOCUMENTACION\01. MANUAL TECNICO\Capturas') 'InterfazInicioSesion.png'
        Copy-Item -LiteralPath $ersTemplate -Destination $ersPath -Force
        Copy-Item -LiteralPath $ecudTemplate -Destination $ecudPath -Force
        if (-not (Test-Path -LiteralPath $coverImage -PathType Leaf)) { throw "Falta la captura de portada para $($profile.Clave): $coverImage" }
        Replace-CoverImage $ersPath $coverImage
        Replace-CoverImage $ecudPath $coverImage
        $coverTitleErs = ('ESPECIFICACIÓN DE REQUERIMIENTOS DE SOFTWARE PARA EUREKABANK CON ' + $profile.Protocolo + ' EN ' + $profile.Lenguaje.ToUpperInvariant())
        $coverTitleEcud = ('ESPECIFICACIÓN DE CASOS DE USO DETALLADA PARA EUREKABANK CON ' + $profile.Protocolo + ' EN ' + $profile.Lenguaje.ToUpperInvariant())

        $document = $word.Documents.Open($ersPath, $false, $false, $false)
        try {
            Update-Cover $document $coverTitleErs $false
            Reset-BodyAfterCover $document
            Add-ErsBody $document $profile (Join-Path $caseDiagramDir 'GR09_CasosUso_CU_Resumen.png')
            $document.Repaginate()
            $document.TablesOfContents.Item(1).Update() | Out-Null
            $document.Fields.Update() | Out-Null
            $document.Repaginate()
            Update-FrontMatterPages $document
            $document.Repaginate()
            $document.TablesOfContents.Item(1).Update() | Out-Null
            $document.Fields.Update() | Out-Null
            $document.Repaginate()
            Update-FrontMatterPages $document
            $document.Save()
        } finally { $document.Close(0) }

        $document = $word.Documents.Open($ecudPath, $false, $false, $false)
        try {
            Update-Cover $document $coverTitleEcud $true
            Reset-BodyAfterCover $document
            Add-EcudBody $document $profile $caseDiagramDir
            $document.Repaginate()
            $document.TablesOfContents.Item(1).Update() | Out-Null
            $document.Fields.Update() | Out-Null
            $document.Repaginate()
            Update-FrontMatterPages $document
            $document.Repaginate()
            $document.TablesOfContents.Item(1).Update() | Out-Null
            $document.Fields.Update() | Out-Null
            $document.Repaginate()
            Update-FrontMatterPages $document
            $document.Save()
        } finally { $document.Close(0) }
        Write-Output "Documentación EurekaBank generada: $($profile.Clave)"
    }
} finally {
    if ($word -ne $null) { $word.Quit() }
}
