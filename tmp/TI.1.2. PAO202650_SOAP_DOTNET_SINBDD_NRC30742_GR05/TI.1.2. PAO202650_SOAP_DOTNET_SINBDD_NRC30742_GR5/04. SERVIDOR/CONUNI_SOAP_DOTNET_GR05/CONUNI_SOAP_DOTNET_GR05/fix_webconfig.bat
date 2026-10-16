@echo off
REM Script para corregir el Web.config en el servidor IIS
REM Ejecutar como Administrador

echo Corrigiendo Web.config en el servidor IIS...
echo.

REM Verificar que el archivo existe
if not exist "C:\servidores\soap_dotnet\web.config" (
    echo ERROR: No se encontro el archivo en C:\servidores\soap_dotnet\web.config
    echo Verifica que la ruta sea correcta.
    pause
    exit /b 1
)

REM Llamar al script de PowerShell
powershell -ExecutionPolicy Bypass -File "%~dp0fix_webconfig.ps1"

echo.
echo Presiona cualquier tecla para continuar...
pause >nul


