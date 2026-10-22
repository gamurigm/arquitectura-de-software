[CmdletBinding()]
param([string]$Raiz = $PSScriptRoot)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Split-SqlItems([string]$Text) {
    $items = [System.Collections.Generic.List[string]]::new()
    $depth = 0
    $quoted = $false
    $start = 0
    for ($i = 0; $i -lt $Text.Length; $i++) {
        $c = $Text[$i]
        if ($c -eq "'") {
            if ($quoted -and $i + 1 -lt $Text.Length -and $Text[$i + 1] -eq "'") { $i++; continue }
            $quoted = -not $quoted
        } elseif (-not $quoted) {
            if ($c -eq '(') { $depth++ }
            elseif ($c -eq ')') { $depth-- }
            elseif ($c -eq ',' -and $depth -eq 0) {
                $items.Add($Text.Substring($start, $i - $start).Trim())
                $start = $i + 1
            }
        }
    }
    if ($start -lt $Text.Length) { $items.Add($Text.Substring($start).Trim()) }
    return ,$items
}

function Read-Schema([string]$Path) {
    $sql = [System.IO.File]::ReadAllText($Path)
    $tables = [ordered]@{}
    foreach ($match in [regex]::Matches($sql, '(?im)CREATE\s+TABLE\s+[`]?([A-Za-z][A-Za-z0-9_]*)[`]?\s*\(')) {
        $name = $match.Groups[1].Value
        $open = $sql.IndexOf('(', $match.Index)
        $depth = 0
        $quoted = $false
        $close = -1
        for ($i = $open; $i -lt $sql.Length; $i++) {
            $c = $sql[$i]
            if ($c -eq "'") {
                if ($quoted -and $i + 1 -lt $sql.Length -and $sql[$i + 1] -eq "'") { $i++; continue }
                $quoted = -not $quoted
            } elseif (-not $quoted) {
                if ($c -eq '(') { $depth++ }
                elseif ($c -eq ')') { $depth--; if ($depth -eq 0) { $close = $i; break } }
            }
        }
        if ($close -lt 0) { throw "No se pudo leer la definición SQL de $name." }
        $table = [pscustomobject]@{
            Code = $name
            Name = $name
            Columns = [System.Collections.Generic.List[object]]::new()
            PrimaryKey = [System.Collections.Generic.List[string]]::new()
            UniqueKeys = [System.Collections.Generic.List[object]]::new()
            ForeignKeys = [System.Collections.Generic.List[object]]::new()
        }
        foreach ($item in (Split-SqlItems $sql.Substring($open + 1, $close - $open - 1))) {
            if ($item -match '^\s*(?:CONSTRAINT\s+[`]?([A-Za-z0-9_]+)[`]?\s+)?PRIMARY\s+KEY\s*\(([^)]*)\)') {
                foreach ($column in ($Matches[2] -split ',')) { $table.PrimaryKey.Add($column.Trim(' ', '`')) }
                continue
            }
            if ($item -match '^\s*(?:CONSTRAINT\s+[`]?([A-Za-z0-9_]+)[`]?\s+)?(?:UNIQUE(?:\s+KEY)?|UNIQUE\s+INDEX)\s*(?:[`]?[A-Za-z0-9_]+[`]?\s*)?\(([^)]*)\)') {
                $table.UniqueKeys.Add([pscustomobject]@{ Name = $Matches[1]; Columns = @($Matches[2] -split ',' | ForEach-Object { $_.Trim(' ', '`') }) })
                continue
            }
            if ($item -match '^\s*(?:CONSTRAINT\s+[`]?([A-Za-z0-9_]+)[`]?\s+)?FOREIGN\s+KEY(?:\s+[`]?([A-Za-z0-9_]+)[`]?)?\s*\(([^)]*)\)\s*REFERENCES\s+[`]?([A-Za-z0-9_]+)[`]?\s*\(([^)]*)\)') {
                $fkName = $Matches[1]
                if (-not $fkName) { $fkName = $Matches[2] }
                $table.ForeignKeys.Add([pscustomobject]@{
                    Name = $fkName
                    Columns = @($Matches[3] -split ',' | ForEach-Object { $_.Trim(' ', '`') })
                    Parent = $Matches[4]
                    ParentColumns = @($Matches[5] -split ',' | ForEach-Object { $_.Trim(' ', '`') })
                })
                continue
            }
            if ($item -match '^\s*([A-Za-z][A-Za-z0-9_]*)\s+([A-Za-z]+(?:\s*\([^)]*\))?)(.*)$') {
                $code = $Matches[1]
                if ($code -in @('KEY', 'INDEX', 'UNIQUE', 'CONSTRAINT', 'CHECK', 'PRIMARY', 'FOREIGN')) { continue }
                $columnType = ($Matches[2] -replace '\s+', '').ToUpperInvariant()
                $columnTail = $Matches[3]
                $table.Columns.Add([pscustomobject]@{
                    Code = $code
                    Type = $columnType
                    Nullable = ($columnTail -notmatch '\bNOT\s+NULL\b')
                    Identity = ($columnTail -match '\b(?:AUTO_INCREMENT|IDENTITY)\b')
                })
            }
        }
        $tables[$name] = $table
    }

    foreach ($match in [regex]::Matches($sql, '(?im)ALTER\s+TABLE\s+[`]?([A-Za-z][A-Za-z0-9_]*)[`]?\s+ADD\s+COLUMN\s+[`]?([A-Za-z][A-Za-z0-9_]*)[`]?\s+([A-Za-z]+(?:\s*\([^)]*\))?)([^;]*);')) {
        $table = $tables[$match.Groups[1].Value]
        if ($null -eq $table) { throw "ALTER TABLE apunta a entidad desconocida: $($match.Groups[1].Value)." }
        $code = $match.Groups[2].Value
        if (-not ($table.Columns | Where-Object Code -eq $code)) {
            $columnType = ($match.Groups[3].Value -replace '\s+', '').ToUpperInvariant()
            $columnTail = $match.Groups[4].Value
            $table.Columns.Add([pscustomobject]@{
                Code = $code
                Type = $columnType
                Nullable = ($columnTail -notmatch '\bNOT\s+NULL\b')
                Identity = ($columnTail -match '\b(?:AUTO_INCREMENT|IDENTITY)\b')
            })
        }
    }
    foreach ($match in [regex]::Matches($sql, '(?is)ALTER\s+TABLE\s+[`]?([A-Za-z][A-Za-z0-9_]*)[`]?\s+ADD\s+CONSTRAINT\s+[`]?([A-Za-z][A-Za-z0-9_]*)[`]?\s+FOREIGN\s+KEY\s*\(([^)]*)\)\s*REFERENCES\s+[`]?([A-Za-z][A-Za-z0-9_]*)[`]?\s*\(([^)]*)\)')) {
        $table = $tables[$match.Groups[1].Value]
        if ($null -eq $table) { throw "ALTER TABLE apunta a entidad desconocida: $($match.Groups[1].Value)." }
        $table.ForeignKeys.Add([pscustomobject]@{
            Name = $match.Groups[2].Value
            Columns = @($match.Groups[3].Value -split ',' | ForEach-Object { $_.Trim(' ', '`') })
            Parent = $match.Groups[4].Value
            ParentColumns = @($match.Groups[5].Value -split ',' | ForEach-Object { $_.Trim(' ', '`') })
        })
    }
    if ($tables.Count -lt 19) { throw "Se esperaban al menos 19 entidades y se encontraron $($tables.Count)." }
    $foreignKeyCount = @($tables.Values | ForEach-Object { $_.ForeignKeys }).Count
    if ($foreignKeyCount -lt 20) { throw "Faltan relaciones de integridad referencial: $foreignKeyCount." }
    foreach ($table in $tables.Values) {
        if ($table.PrimaryKey.Count -eq 0) { throw "La entidad $($table.Code) no tiene clave primaria." }
        foreach ($fk in $table.ForeignKeys) {
            if (-not $tables.Contains($fk.Parent)) { throw "La relación $($fk.Name) apunta a $($fk.Parent), que no existe." }
        }
    }
    return ,$tables
}

function Label([string]$Code) {
    $value = $Code -replace '^(chr|vch|int|dec|dtt|fec)_', ''
    $value = $value -replace '_', ' '
    return [Globalization.CultureInfo]::GetCultureInfo('es-EC').TextInfo.ToTitleCase($value.ToLowerInvariant())
}

function Model-Type([string]$Type, [bool]$Logical, [bool]$SqlServer, [string]$Column) {
    $value = $Type
    if ($SqlServer) {
        if ($value -eq 'INTEGER') { $value = 'INT' }
        if ($value -eq 'DATETIME') { $value = 'DATETIME2' }
        if ($Column -in @('id_transferencia', 'clave_operacion')) { $value = 'UNIQUEIDENTIFIER' }
    }
    if ($Logical) { return $value }
    if ($value -match '^(CHAR|VARCHAR)') { return 'Texto' }
    if ($value -match '^(DECIMAL|NUMERIC)') { return 'Importe' }
    if ($value -match '^(INTEGER|INT)') { return 'Número' }
    if ($value -match '^DATE') { return 'Fecha' }
    if ($value -match '^(DATETIME|TIMESTAMP)') { return 'Fecha y hora' }
    if ($value -match 'UNIQUEIDENTIFIER') { return 'Identificador' }
    return 'Texto'
}

function Get-MerViews {
    @(
        [pscustomobject]@{
            Code = '01_Clientes_Cuentas'; Name = 'Clientes y cuentas'
            Entities = @('Cliente', 'Credenciales', 'Cuenta', 'Moneda')
            Relationships = @('fk_cuenta_cliente', 'FK_Credenciales_Cliente', 'fk_cuenta_moneda')
            Positions = @{ Cliente = @(-20000, 15000); Credenciales = @(-50000, 15000); Cuenta = @(15000, 15000); Moneda = @(15000, -15000) }
        }
        [pscustomobject]@{
            Code = '02_Transferencia_Origen'; Name = 'Cuenta de origen y transferencia'
            Entities = @('Cuenta', 'Cliente', 'Transferencias')
            Relationships = @('FK_Transferencias_Cliente', 'FK_Transferencias_Cuenta_Origen')
            Positions = @{ Cuenta = @(-35000, -15000); Cliente = @(0, 25000); Transferencias = @(35000, -15000) }
        }
        [pscustomobject]@{
            Code = '03_Transferencia_Destino'; Name = 'Cuenta de destino y transferencia'
            Entities = @('Cuenta', 'Transferencias')
            Relationships = @('FK_Transferencias_Cuenta_Destino')
            Positions = @{ Cuenta = @(-35000, 0); Transferencias = @(35000, 0) }
        }
        [pscustomobject]@{
            Code = '04_Movimientos'; Name = 'Movimientos y asientos'
            Entities = @('Movimiento', 'Cuenta', 'Transferencias', 'Empleado', 'TipoMovimiento')
            Relationships = @('fk_movimiento_tipomovimiento', 'fk_movimiento_empleado', 'fk_movimiento_cuenta', 'FK_Movimiento_Transferencias')
            Positions = @{ Movimiento = @(0, 0); Cuenta = @(-35000, 0); Transferencias = @(0, 30000); Empleado = @(35000, 0); TipoMovimiento = @(0, -26000) }
        }
        [pscustomobject]@{
            Code = '05_Usuarios_Permisos'; Name = 'Usuarios y permisos'
            Entities = @('Empleado', 'Usuario', 'Permiso', 'Modulo', 'LOGSESSION')
            Relationships = @('FK_Usuario_Empleado', 'FK_Permiso_Modulo', 'FK_Permiso_Usuario', 'fk_log_session_empleado')
            Positions = @{ Empleado = @(-27000, 0); Usuario = @(0, 0); Permiso = @(27000, 0); Modulo = @(54000, 0); LOGSESSION = @(-27000, -23000) }
        }
        [pscustomobject]@{
            Code = '06_Sucursales'; Name = 'Sucursales y personal'
            Entities = @('Sucursal', 'Asignado', 'Empleado', 'Cuenta')
            Relationships = @('fk_asignado_empleado', 'fk_asignado_sucursal', 'fk_cuenta_sucursal', 'fk_cuente_empleado')
            Positions = @{ Sucursal = @(-26000, 26000); Asignado = @(26000, 26000); Empleado = @(26000, -26000); Cuenta = @(-26000, -26000) }
        }
        [pscustomobject]@{
            Code = '07_Configuracion'; Name = 'Parámetros y cargos'
            Entities = @('Moneda', 'CargoMantenimiento', 'CostoMovimiento', 'InteresMensual', 'Parametro', 'Contador')
            Relationships = @('fk_interesmensual_moneda', 'fk_costomovimiento_moneda', 'fk_cargomantenimiento_moneda')
            Positions = @{ Moneda = @(0, 25000); CargoMantenimiento = @(-30000, 0); CostoMovimiento = @(0, 0); InteresMensual = @(30000, 0); Parametro = @(-20000, -26000); Contador = @(20000, -26000) }
        }
    )
}

function Set-MerSymbolPosition($Symbol, [int[]]$Position) {
    $point = $Symbol.GetAttribute('Position')
    $point.X = $Position[0]
    $point.Y = $Position[1]
    $Symbol.SetAttribute('Position', $point) | Out-Null
}

function Export-MerViews($Model, [string]$DiagramCollectionName, $ObjectsByCode, $Links,
                         [string]$ImagePath, [string]$Name, [string]$Code, [bool]$Physical) {
    $views = Get-MerViews
    $diagramCollection = $Model.$DiagramCollectionName
    $directory = [System.IO.Path]::GetDirectoryName($ImagePath)
    $baseName = [System.IO.Path]::GetFileNameWithoutExtension($ImagePath)
    foreach ($legacySuffix in @('_02_Transferencias', '_03_Movimientos', '_04_Usuarios_Permisos', '_05_Sucursales', '_06_Configuracion')) {
        $legacyPath = Join-Path $directory ($baseName + $legacySuffix + '.png')
        if (Test-Path -LiteralPath $legacyPath) { Remove-Item -LiteralPath $legacyPath -Force }
    }
    $coveredEntities = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $coveredLinks = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)

    for ($viewIndex = 0; $viewIndex -lt $views.Count; $viewIndex++) {
        $view = $views[$viewIndex]
        $diagram = if ($viewIndex -eq 0) { $diagramCollection.Item(0) } else { $diagramCollection.CreateNew() }
        $diagram.Name = $Name + ' - ' + $view.Name
        $diagram.Code = $Code + '_' + $view.Code
        $symbolsByCode = @{}

        foreach ($entityCode in $view.Entities) {
            if (-not $ObjectsByCode.ContainsKey($entityCode)) { throw "Falta $entityCode en la vista $($view.Name)." }
            $symbol = $diagram.AttachObject($ObjectsByCode[$entityCode])
            Set-MerSymbolPosition $symbol $view.Positions[$entityCode]
            $symbolsByCode[$entityCode] = $symbol
            $coveredEntities.Add($entityCode) | Out-Null
        }

        for ($i = 0; $i -lt $view.Entities.Count; $i++) {
            $firstCode = $view.Entities[$i]
            $firstPosition = $view.Positions[$firstCode]
            $firstWidth = [double]$symbolsByCode[$firstCode].GetAttribute('Width')
            $firstHeight = [double]$symbolsByCode[$firstCode].GetAttribute('Height')
            for ($j = $i + 1; $j -lt $view.Entities.Count; $j++) {
                $secondCode = $view.Entities[$j]
                $secondPosition = $view.Positions[$secondCode]
                $secondWidth = [double]$symbolsByCode[$secondCode].GetAttribute('Width')
                $secondHeight = [double]$symbolsByCode[$secondCode].GetAttribute('Height')
                $overlapX = [Math]::Abs($firstPosition[0] - $secondPosition[0]) -lt (($firstWidth + $secondWidth) / 2 + 2000)
                $overlapY = [Math]::Abs($firstPosition[1] - $secondPosition[1]) -lt (($firstHeight + $secondHeight) / 2 + 2000)
                if ($overlapX -and $overlapY) { throw "Se superponen $firstCode y $secondCode en la vista $($view.Name)." }
            }
        }

        $attachedLinks = 0
        foreach ($link in $Links) {
            if ($Physical) {
                $parentCode = $link.ParentTable.Code
                $childCode = $link.ChildTable.Code
            } else {
                $parentCode = $link.Entity1.Code
                $childCode = $link.Entity2.Code
            }
            if ($view.Relationships -contains $link.Code) {
                if (-not $symbolsByCode.ContainsKey($parentCode) -or -not $symbolsByCode.ContainsKey($childCode)) {
                    throw "La relación $($link.Code) no tiene ambos extremos en la vista $($view.Name)."
                }
                $diagram.AttachLinkObject($link) | Out-Null
                $attachedLinks++
                $coveredLinks.Add($link.Code) | Out-Null
            }
        }

        $expectedLinks = $view.Relationships.Count
        if ($attachedLinks -ne $expectedLinks) { throw "La vista $($view.Name) conserva $attachedLinks de $expectedLinks relaciones." }

        $outputPath = $ImagePath
        if ($viewIndex -gt 0) {
            $outputPath = Join-Path $directory ($baseName + '_' + $view.Code + '.png')
        }
        $diagram.ExportImage($outputPath)
        if (-not (Test-Path -LiteralPath $outputPath) -or (Get-Item -LiteralPath $outputPath).Length -lt 1000) {
            throw "No se exportó la vista $($view.Name)."
        }
        Write-Output "Vista MER: $($view.Name) ($($view.Entities.Count) entidades, $attachedLinks relaciones)"
    }

    if ($coveredEntities.Count -ne $ObjectsByCode.Count -or $coveredLinks.Count -ne $Links.Count) {
        throw "Las vistas no cubren el MER completo ($($coveredEntities.Count)/$($ObjectsByCode.Count) entidades, $($coveredLinks.Count)/$($Links.Count) relaciones)."
    }
}

function New-PowerDesignerModel($Application, $Tables, [string]$Kind, [string]$DiagramCollection,
                                [string]$DiagramKind, [string]$Name, [string]$Code,
                                [string]$Path, [string]$ImagePath, [bool]$Logical, [bool]$SqlServer) {
    $metaClass = $Application.MetaModel.GetMetaClassByPublicName($Kind)
    if ($null -eq $metaClass) { throw "PowerDesigner no ofrece $Kind." }
    $model = $Application.CreateModel($metaClass.Kind, "|Diagram=$DiagramKind", 9)
    if ($null -eq $model) { throw "PowerDesigner no creó $Name." }
    $saved = $false
    try {
        $model.Name = $Name
        $model.Code = $Code
        $model.Comment = 'Modelo de EurekaBank GR09. Incluye autenticación, cuentas, movimientos y transferencias de doble asiento.'
        $diagram = $model.$DiagramCollection.Item(0)
        $diagram.Name = $Name
        $diagram.Code = $Code + '_ERD'
        $entityObjects = @{}
        foreach ($table in $Tables.Values) {
            $entity = $model.Entities.CreateNew()
            $entity.Name = $table.Name
            $entity.Code = $table.Code
            $entity.Comment = "Entidad correspondiente a $($table.Code) en el esquema bancario."
            $entityObjects[$table.Code] = $entity
            $attributeObjects = @{}
            foreach ($column in $table.Columns) {
                $attribute = $entity.Attributes.CreateNew()
                $attribute.Name = Label $column.Code
                $attribute.Code = if ($Logical) { $column.Code } else { $table.Code + '_' + $column.Code }
                $attribute.DataType = Model-Type $column.Type $Logical $SqlServer $column.Code
                $attribute.Mandatory = -not $column.Nullable
                $attributeObjects[$column.Code] = $attribute
            }
            $identifier = $entity.Identifiers.CreateNew()
            $identifier.Name = 'Identificador principal'
            $identifier.Code = 'PK_' + $table.Code
            $identifier.PrimaryIdentifier = $true
            foreach ($columnCode in $table.PrimaryKey) { $identifier.Attributes.Add($attributeObjects[$columnCode]) }
            foreach ($unique in $table.UniqueKeys) {
                $sameAsPrimary = ($unique.Columns.Count -eq $table.PrimaryKey.Count) -and
                    (@(Compare-Object $unique.Columns $table.PrimaryKey).Count -eq 0)
                if ($sameAsPrimary) { continue }
                $alternate = $entity.Identifiers.CreateNew()
                $alternate.Name = 'Identificador alternativo ' + $unique.Name
                $alternate.Code = if ($unique.Name) { $unique.Name } else { 'UQ_' + $table.Code + '_' + $entity.Identifiers.Count }
                $alternate.PrimaryIdentifier = $false
                foreach ($columnCode in $unique.Columns) { $alternate.Attributes.Add($attributeObjects[$columnCode]) }
            }
        }
        $relationshipObjects = [System.Collections.Generic.List[object]]::new()
        foreach ($table in $Tables.Values) {
            foreach ($fk in $table.ForeignKeys) {
                $parent = $entityObjects[$fk.Parent]
                $child = $entityObjects[$table.Code]
                $relationship = $model.Relationships.CreateNew()
                $columnSuffix = $fk.Columns -join '_'
                $relationship.Code = if ($fk.Name) { $fk.Name } else { 'FK_' + $table.Code + '_' + $fk.Parent + '_' + $columnSuffix }
                $relationship.Name = $parent.Name + ' - ' + $child.Name
                $relationship.Comment = 'Relación referencial desde ' + ($fk.Columns -join ', ') + ' hacia ' + ($fk.ParentColumns -join ', ') + '.'
                $relationship.Entity1 = $parent
                $relationship.Entity2 = $child
                $relationship.Entity1ToEntity2Role = ' '
                $relationship.Entity2ToEntity1Role = ' '
                if ($table.Code -eq 'Transferencias' -and $fk.Parent -eq 'Cuenta') {
                    if (($fk.Columns -join ' ') -match 'origen') {
                        $relationship.Code = 'FK_Transferencias_Cuenta_Origen'
                        $relationship.Name = 'Cuenta origen de transferencia'
                    } elseif (($fk.Columns -join ' ') -match 'destino') {
                        $relationship.Code = 'FK_Transferencias_Cuenta_Destino'
                        $relationship.Name = 'Cuenta destino de transferencia'
                    }
                }
                $relationship.Entity1ToEntity2RoleMinimumCardinality = '0'
                $uniqueForeignKey = ($fk.Columns.Count -eq $table.PrimaryKey.Count) -and
                    (@(Compare-Object $fk.Columns $table.PrimaryKey).Count -eq 0)
                if (-not $uniqueForeignKey) {
                    foreach ($unique in $table.UniqueKeys) {
                        if (($unique.Columns.Count -eq $fk.Columns.Count) -and (@(Compare-Object $unique.Columns $fk.Columns).Count -eq 0)) { $uniqueForeignKey = $true }
                    }
                }
                $relationship.Entity1ToEntity2RoleMaximumCardinality = if ($uniqueForeignKey) { '1' } else { 'n' }
                $relationship.Entity2ToEntity1RoleMinimumCardinality = if (@($fk.Columns | Where-Object { ($table.Columns | Where-Object Code -eq $_).Nullable }).Count -gt 0) { '0' } else { '1' }
                $relationship.Entity2ToEntity1RoleMaximumCardinality = '1'
                $relationshipObjects.Add($relationship)
            }
        }
        Export-MerViews $model $DiagramCollection $entityObjects $relationshipObjects $ImagePath $Name $Code $false
        $model.Save($Path)
        $saved = $true
        Write-Output "PowerDesigner: $Name"
    } finally {
        if ($saved) { $model.Close() }
    }
}

function New-PowerDesignerPhysicalModel($Application, $Tables, [string]$Name, [string]$Code,
                                         [string]$Path, [string]$ImagePath, [string]$Dbms,
                                         [bool]$SqlServer) {
    $metaClass = $Application.MetaModel.GetMetaClassByPublicName('PdPDM.Model')
    if ($null -eq $metaClass) { throw 'PowerDesigner no ofrece PdPDM.Model.' }
    $model = $Application.CreateModel($metaClass.Kind, "|DBMS=$Dbms|Diagram=PhysicalDiagram", 9)
    if ($null -eq $model) { throw "PowerDesigner no creó $Name." }
    $saved = $false
    try {
        $model.Name = $Name
        $model.Code = $Code
        $model.Comment = 'Modelo físico de EurekaBank GR09. Define tablas, columnas, claves e integridad referencial.'
        if ($model.DBMS.Name -ne $Dbms) { throw "El modelo $Name no usa el DBMS esperado: $Dbms." }
        $diagram = $model.PhysicalDiagrams.Item(0)
        $diagram.Name = $Name
        $diagram.Code = $Code + '_PDM'
        $tableObjects = @{}
        $columnObjects = @{}
        $columnCount = 0
        foreach ($table in $Tables.Values) {
            $physicalTable = $model.Tables.CreateNew()
            $physicalTable.Name = $table.Name
            $physicalTable.Code = $table.Code
            $physicalTable.Comment = "Tabla física $($table.Code) del esquema EurekaBank."
            $tableObjects[$table.Code] = $physicalTable
            foreach ($column in $table.Columns) {
                $physicalColumn = $physicalTable.Columns.CreateNew()
                $physicalColumn.Name = $column.Code
                $physicalColumn.Code = $column.Code
                $physicalColumn.DataType = Model-Type $column.Type $true $SqlServer $column.Code
                $physicalColumn.Mandatory = -not $column.Nullable
                if ($column.Identity) { $physicalColumn.Identity = $true }
                if ($column.Code -in $table.PrimaryKey) { $physicalColumn.Primary = $true }
                $columnObjects[($table.Code + '.' + $column.Code)] = $physicalColumn
                $columnCount++
            }
            foreach ($unique in $table.UniqueKeys) {
                $sameAsPrimary = ($unique.Columns.Count -eq $table.PrimaryKey.Count) -and
                    (@(Compare-Object $unique.Columns $table.PrimaryKey).Count -eq 0)
                if ($sameAsPrimary) { continue }
                $index = $physicalTable.Indexes.CreateNew()
                $index.Name = if ($unique.Name) { $unique.Name } else { 'UQ_' + $table.Code + '_' + $physicalTable.Indexes.Count }
                $index.Code = if ($unique.Name) { $unique.Name } else { 'UQ_' + $table.Code + '_' + $physicalTable.Indexes.Count }
                $index.Unique = $true
                foreach ($columnCode in $unique.Columns) {
                    $indexColumn = $index.IndexColumns.CreateNew()
                    $indexColumn.Column = $columnObjects[($table.Code + '.' + $columnCode)]
                }
            }
        }
        $referenceCount = 0
        $referenceObjects = [System.Collections.Generic.List[object]]::new()
        foreach ($table in $Tables.Values) {
            foreach ($foreignKey in $table.ForeignKeys) {
                $reference = $model.References.CreateNew()
                $columnSuffix = $foreignKey.Columns -join '_'
                $reference.Code = if ($foreignKey.Name) { $foreignKey.Name } else { 'FK_' + $table.Code + '_' + $foreignKey.Parent + '_' + $columnSuffix }
                $reference.Name = if ($foreignKey.Name) { $foreignKey.Name } else { $reference.Code }
                if ($table.Code -eq 'Transferencias' -and $foreignKey.Parent -eq 'Cuenta') {
                    if (($foreignKey.Columns -join ' ') -match 'origen') {
                        $reference.Code = 'FK_Transferencias_Cuenta_Origen'
                        $reference.Name = 'Cuenta origen'
                    } elseif (($foreignKey.Columns -join ' ') -match 'destino') {
                        $reference.Code = 'FK_Transferencias_Cuenta_Destino'
                        $reference.Name = 'Cuenta destino'
                    }
                }
                $reference.ParentTable = $tableObjects[$foreignKey.Parent]
                $reference.ChildTable = $tableObjects[$table.Code]
                for ($index = 0; $index -lt $foreignKey.Columns.Count; $index++) {
                    $join = if ($index -lt $reference.Joins.Count) { $reference.Joins.Item($index) } else { $reference.Joins.CreateNew() }
                    $join.ParentTableColumn = $columnObjects[($foreignKey.Parent + '.' + $foreignKey.ParentColumns[$index])]
                    $join.ChildTableColumn = $columnObjects[($table.Code + '.' + $foreignKey.Columns[$index])]
                }
                for ($index = $reference.Joins.Count - 1; $index -ge $foreignKey.Columns.Count; $index--) {
                    $reference.Joins.Item($index).delete()
                }
                $referenceObjects.Add($reference)
                $referenceCount++
            }
        }
        $expectedReferences = @($Tables.Values | ForEach-Object { $_.ForeignKeys }).Count
        if ($model.Tables.Count -ne $Tables.Count -or $columnCount -lt 1 -or
            $model.References.Count -ne $expectedReferences -or $referenceCount -ne $expectedReferences) {
            throw "El PDM $Name no conserva tablas, columnas y relaciones del esquema."
        }
        Export-MerViews $model 'PhysicalDiagrams' $tableObjects $referenceObjects $ImagePath $Name $Code $true
        $model.Save($Path)
        $saved = $true
        Write-Output "PowerDesigner: $Name ($($model.Tables.Count) tablas, $columnCount columnas, $referenceCount referencias)"
    } finally {
        if ($saved) { $model.Close() }
    }
}

$proyectos = @(
    @{ Carpeta = '1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR09'; Stack = 'Java' },
    @{ Carpeta = '1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR09'; Stack = '.NET' },
    @{ Carpeta = '1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR09'; Stack = 'Java' },
    @{ Carpeta = '1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09'; Stack = '.NET' }
)
$schemaPath = Join-Path (Join-Path (Join-Path $Raiz $proyectos[0].Carpeta) '03. BDD') 'Crea_BD.sql'
$tables = Read-Schema $schemaPath
$application = $null
try {
    $application = New-Object -ComObject PowerDesigner.Application
    $application.InteractiveMode = 2
    foreach ($stack in @('Java', '.NET')) {
        $baseProject = $proyectos | Where-Object Stack -eq $stack | Select-Object -First 1
        $merRoot = Join-Path (Join-Path $Raiz $baseProject.Carpeta) '02. MER'
        $conceptualDir = Join-Path $merRoot '01. CONCEPTUAL'
        $logicalDir = Join-Path $merRoot '02. LOGICO'
        $physicalDir = Join-Path $merRoot '03. FISICO'
        New-Item -ItemType Directory -Force -Path $conceptualDir, $logicalDir, $physicalDir | Out-Null
        $suffix = if ($stack -eq 'Java') { 'Java' } else { 'DotNet' }
        $conceptualName = 'GR09_EurekaBank_Conceptual_' + $suffix
        $logicalName = 'GR09_EurekaBank_Logical_' + $suffix
        $physicalSuffix = if ($stack -eq 'Java') { 'Java_MySQL' } else { 'DotNet_SQLServer' }
        $physicalName = 'GR09_EurekaBank_Physical_' + $physicalSuffix
        $dbms = if ($stack -eq 'Java') { 'MySQL 5.0' } else { 'Microsoft SQL Server 2008' }
        New-PowerDesignerModel $application $tables 'PdCDM.Model' 'ConceptualDiagrams' 'ConceptualDiagram' `
            ('EurekaBank Conceptual ' + $stack + ' GR09') ('GR09_EurekaBank_CDM_' + $suffix) `
            (Join-Path $conceptualDir ($conceptualName + '.cdm')) (Join-Path $conceptualDir ($conceptualName + '.png')) $false ($stack -eq '.NET')
        New-PowerDesignerModel $application $tables 'PdLDM.Model' 'LogicalDiagrams' 'LogicalDiagram' `
            ('EurekaBank Logical ' + $stack + ' GR09') ('GR09_EurekaBank_LDM_' + $suffix) `
            (Join-Path $logicalDir ($logicalName + '.ldm')) (Join-Path $logicalDir ($logicalName + '.png')) $true ($stack -eq '.NET')
        New-PowerDesignerPhysicalModel $application $tables ('EurekaBank Physical ' + $stack + ' GR09') `
            ('GR09_EurekaBank_PDM_' + $physicalSuffix) (Join-Path $physicalDir ($physicalName + '.pdm')) `
            (Join-Path $physicalDir ($physicalName + '.png')) $dbms ($stack -eq '.NET')
    }
} finally {
    if ($application) {
        [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($application)
        [GC]::Collect()
        [GC]::WaitForPendingFinalizers()
    }
}

foreach ($project in $proyectos) {
    $sourceStack = $proyectos | Where-Object Stack -eq $project.Stack | Select-Object -First 1
    if ($sourceStack.Carpeta -eq $project.Carpeta) { continue }
    $stackSuffix = if ($project.Stack -eq 'Java') { 'Java' } else { 'DotNet' }
    $physicalSuffix = if ($project.Stack -eq 'Java') { 'Java_MySQL' } else { 'DotNet_SQLServer' }
    $imageBaseNames = @(
        'GR09_EurekaBank_Conceptual_' + $stackSuffix,
        'GR09_EurekaBank_Logical_' + $stackSuffix,
        'GR09_EurekaBank_Physical_' + $physicalSuffix
    )
    $source = Join-Path (Join-Path (Join-Path (Join-Path $Raiz $sourceStack.Carpeta) '02. MER') '01. CONCEPTUAL') ''
    $destination = Join-Path (Join-Path (Join-Path (Join-Path $Raiz $project.Carpeta) '02. MER') '01. CONCEPTUAL') ''
    New-Item -ItemType Directory -Force -Path $destination, (Join-Path (Join-Path (Join-Path $Raiz $project.Carpeta) '02. MER') '02. LOGICO'), (Join-Path (Join-Path (Join-Path $Raiz $project.Carpeta) '02. MER') '03. FISICO') | Out-Null
    foreach ($category in @('01. CONCEPTUAL', '02. LOGICO', '03. FISICO')) {
        $sourceDir = Join-Path (Join-Path (Join-Path $Raiz $sourceStack.Carpeta) '02. MER') $category
        $destinationDir = Join-Path (Join-Path (Join-Path $Raiz $project.Carpeta) '02. MER') $category
        Get-ChildItem -LiteralPath $sourceDir -File | Copy-Item -Destination $destinationDir -Force
        foreach ($baseName in $imageBaseNames) {
            foreach ($legacySuffix in @('_02_Transferencias', '_03_Movimientos', '_04_Usuarios_Permisos', '_05_Sucursales', '_06_Configuracion')) {
                $legacyPath = Join-Path $destinationDir ($baseName + $legacySuffix + '.png')
                if (Test-Path -LiteralPath $legacyPath) { Remove-Item -LiteralPath $legacyPath -Force }
            }
        }
    }
}
