@echo off
REM Script para instalar y configurar ASP.NET en IIS
REM Ejecutar como Administrador

echo ========================================
echo Instalando y configurando ASP.NET para WCF
echo ========================================
echo.

echo [1/4] Instalando ASP.NET 4.7.2 en IIS...
dism /online /enable-feature /featurename:IIS-ASPNET45 /all
if %errorlevel% neq 0 (
    echo ERROR: No se pudo instalar ASP.NET 4.7.2
    pause
    exit /b 1
)
echo.

echo [2/4] Registrando ASP.NET en IIS...
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\aspnet_regiis.exe -iru
if %errorlevel% neq 0 (
    echo ERROR: No se pudo registrar ASP.NET
    pause
    exit /b 1
)
echo.

echo [3/4] Verificando que ASP.NET esta instalado...
powershell -Command "Get-WindowsOptionalFeature -Online | Where-Object {$_.FeatureName -like '*ASPNET*'} | Select-Object FeatureName, State"
echo.

echo [4/4] Reiniciando IIS...
iisreset
echo.

echo ========================================
echo Proceso completado!
echo ========================================
echo.
echo Verifica que:
echo 1. El Application Pool use .NET CLR v4.0
echo 2. El handler svc-Integrated-4.0 este habilitado
echo 3. WCF HTTP Activation este instalado
echo.
pause


