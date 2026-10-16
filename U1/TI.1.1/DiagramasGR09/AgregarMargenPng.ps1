param([string]$Raiz)

Add-Type -AssemblyName System.Drawing
$margen = 32
$proyectos = Get-ChildItem -LiteralPath $Raiz -Directory -Filter 'TI.1.1. PAO202651_*_SINBDD_NRC36884_GR09'
foreach ($proyecto in $proyectos) {
    $diagramas = Join-Path $proyecto.FullName '01. UML\03. DIAGRAMAS UML'
    if (-not (Test-Path -LiteralPath $diagramas)) { continue }
    foreach ($imagen in Get-ChildItem -LiteralPath $diagramas -Filter 'GR09_*.png' -File -Recurse) {
        $original = [System.Drawing.Bitmap]::new($imagen.FullName)
        $lienzo = [System.Drawing.Bitmap]::new($original.Width + 2 * $margen, $original.Height + 2 * $margen)
        $grafico = [System.Drawing.Graphics]::FromImage($lienzo)
        $grafico.Clear([System.Drawing.Color]::White)
        $grafico.DrawImageUnscaled($original, $margen, $margen)
        $temporal = "$($imagen.FullName).tmp.png"
        $lienzo.Save($temporal, [System.Drawing.Imaging.ImageFormat]::Png)
        $grafico.Dispose()
        $lienzo.Dispose()
        $original.Dispose()
        Move-Item -LiteralPath $temporal -Destination $imagen.FullName -Force
    }
}
