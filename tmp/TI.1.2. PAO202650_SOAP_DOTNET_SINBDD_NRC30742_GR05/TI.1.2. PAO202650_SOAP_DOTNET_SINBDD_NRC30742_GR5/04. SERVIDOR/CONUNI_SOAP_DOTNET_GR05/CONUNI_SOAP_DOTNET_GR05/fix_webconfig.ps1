# Script para corregir el Web.config en el servidor IIS
# Ejecutar como Administrador

$webConfigPath = "C:\servidores\soap_dotnet\web.config"

if (Test-Path $webConfigPath) {
    Write-Host "Leyendo archivo Web.config..." -ForegroundColor Yellow
    
    # Leer el contenido del archivo
    $content = Get-Content $webConfigPath -Raw
    
    # Reemplazar la sección system.webServer activa por comentada
    $content = $content -replace '(?s)<system\.webServer>.*?</system\.webServer>', '<!--
  <system.webServer>
    <modules runAllManagedModulesForAllRequests="true" />
    <directoryBrowse enabled="true" />
  </system.webServer>
  -->
  <!-- Comentado para evitar conflictos con IIS -->'
    
    # Guardar el archivo
    $content | Set-Content $webConfigPath -Encoding UTF8
    
    Write-Host "Web.config actualizado correctamente!" -ForegroundColor Green
    Write-Host "Ubicación: $webConfigPath" -ForegroundColor Cyan
} else {
    Write-Host "ERROR: No se encontró el archivo en $webConfigPath" -ForegroundColor Red
    Write-Host "Verifica que la ruta sea correcta." -ForegroundColor Yellow
}


