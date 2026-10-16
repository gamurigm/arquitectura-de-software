@echo off
REM Script para verificar si WCF HTTP Activation está instalado
REM Ejecutar como Administrador

echo ========================================
echo Verificando WCF HTTP Activation
echo ========================================
echo.

echo [1/3] Verificando caracteristicas de Windows...
powershell -Command "Get-WindowsOptionalFeature -Online | Where-Object {$_.FeatureName -like '*WCF*' -or $_.FeatureName -like '*HTTP*' -or $_.FeatureName -like '*ASPNET*'} | Select-Object FeatureName, State | Format-Table -AutoSize"
echo.

echo [2/3] Verificando handlers de .svc en IIS...
powershell -Command "Get-WebHandler -PSPath 'IIS:\' | Where-Object {$_.Name -like '*svc*'} | Select-Object Name, Path, State | Format-Table -AutoSize"
echo.

echo [3/3] Verificando Application Pools...
powershell -Command "Get-WebApplicationPool | Select-Object Name, ManagedRuntimeVersion, ManagedPipelineMode | Format-Table -AutoSize"
echo.

echo ========================================
echo Verificacion completada
echo ========================================
echo.
echo Busca en la lista anterior:
echo - IIS-HTTPActivation (debe estar Enabled)
echo - IIS-ASPNET45 (debe estar Enabled)
echo - Handler svc-Integrated-4.0 (debe existir)
echo.
pause


