$raizComun = Split-Path -Parent $PSScriptRoot
$archivoEntorno = Join-Path $raizComun '.env'
if (Test-Path -LiteralPath $archivoEntorno) {
    foreach ($linea in Get-Content -LiteralPath $archivoEntorno) {
        if ($linea -match '^\s*([^#=\s]+)\s*=\s*(.*)\s*$') {
            $nombre = $matches[1]
            $valor = $matches[2]
            if ($valor.Length -ge 2 -and $valor.StartsWith('"') -and $valor.EndsWith('"')) {
                $valor = $valor.Substring(1, $valor.Length - 2)
            }
            [Environment]::SetEnvironmentVariable($nombre, $valor, 'Process')
        }
    }
}
