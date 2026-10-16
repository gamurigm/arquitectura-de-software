@echo off
REM Script para desbloquear la sección handlers en IIS
REM Ejecutar como Administrador

echo Desbloqueando secciones de IIS para permitir configuracion de handlers...
echo.

REM Desbloquear handlers
%windir%\system32\inetsrv\appcmd unlock config -section:system.webServer/handlers

REM Desbloquear modules (por si acaso)
%windir%\system32\inetsrv\appcmd unlock config -section:system.webServer/modules

REM Desbloquear defaultDocument
%windir%\system32\inetsrv\appcmd unlock config -section:system.webServer/defaultDocument

echo.
echo Secciones desbloqueadas correctamente!
echo.
echo Presiona cualquier tecla para continuar...
pause >nul


