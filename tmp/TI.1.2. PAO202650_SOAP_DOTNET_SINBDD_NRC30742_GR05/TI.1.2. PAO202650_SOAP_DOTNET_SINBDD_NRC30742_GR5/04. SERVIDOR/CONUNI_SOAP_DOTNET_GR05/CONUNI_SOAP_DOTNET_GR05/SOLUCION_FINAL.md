# SOLUCIÓN FINAL - Configuración WCF en IIS sin modificar Web.config

## Paso 1: Usar Web.config MÍNIMO

Copia el contenido de `Web.config.MINIMO` al archivo `C:\servidores\soap_dotnet\web.config` en tu servidor.

Este Web.config NO incluye ninguna sección de `system.webServer` para evitar conflictos con IIS.

---

## Paso 2: Verificar que WCF HTTP Activation está instalado

1. Abre **"Activar o desactivar características de Windows"** (Windows Features)
2. Navega a: **Internet Information Services** → **Servicios de World Wide Web** → **Características de desarrollo de aplicaciones**
3. Asegúrate de que **"Activación HTTP de WCF"** esté marcado
4. Si no está instalado, márcalo, haz clic en Aceptar y espera a que se instale
5. Reinicia IIS después de instalar

---

## Paso 3: Verificar Application Pool

1. Abre **IIS Manager**
2. Selecciona tu sitio (puerto 8085)
3. En el panel de acciones, haz clic en **"Configuración básica..."**
4. Verifica que el Application Pool tenga:
   - **.NET CLR Version:** v4.0 (o v4.0 sin código administrado)
   - **Managed Pipeline Mode:** Integrated
5. Si no está así, haz clic en "Seleccionar..." y crea/modifica un Application Pool con estas características

---

## Paso 4: Verificar Handler para .svc

1. En IIS Manager, selecciona el **SERVIDOR** (raíz, no el sitio)
2. Haz doble clic en **"Asignación de controladores"** (Handler Mappings)
3. Busca **"svc-Integrated-4.0"** en la lista
4. Si existe y está **Habilitado**, perfecto. Si está **Deshabilitado**, haz clic derecho → **"Editar permisos de función..."** → **"Habilitar"**
5. Si NO existe el handler, haz clic en **"Restaurar al elemento primario"** en el panel derecho
6. Esto restaurará los handlers predeterminados de IIS, incluyendo el handler para .svc

---

## Paso 5: Configurar Documento Predeterminado (Opcional)

1. Selecciona tu **sitio** (puerto 8085)
2. Haz doble clic en **"Documentos predeterminados"**
3. Verifica si **"Service1.svc"** está en la lista
4. Si no está, haz clic en **"Agregar..."** y escribe: `Service1.svc`
5. Mueve `Service1.svc` al **inicio de la lista** usando "Subir"

---

## Paso 6: Reiniciar IIS

Ejecuta en PowerShell como Administrador:

```powershell
iisreset
```

O desde IIS Manager: clic derecho en el servidor → "Reiniciar"

---

## Paso 7: Probar el servicio

Abre en el navegador:
- `http://localhost:8085/Service1.svc` - Debería mostrar información del servicio
- `http://localhost:8085/Service1.svc?wsdl` - Debería mostrar el WSDL

---

## Si aún no funciona - Verificaciones adicionales

### Verificar que Service1.svc existe
- Asegúrate de que el archivo `Service1.svc` esté en `C:\servidores\soap_dotnet\Service1.svc`

### Verificar permisos
- El Application Pool debe tener permisos de lectura en la carpeta `C:\servidores\soap_dotnet`
- Verifica en IIS Manager → Sitio → "Editar permisos..." → Seguridad

### Verificar que .NET Framework 4.7.2 esté instalado
- Ejecuta: `dir "C:\Windows\Microsoft.NET\Framework64\v4.0*"`
- Deberías ver una carpeta con la versión 4.7.2 o superior

### Ver logs de IIS
- Revisa los logs en: `C:\inetpub\logs\LogFiles\`
- O usa Event Viewer → Windows Logs → Application

---

## Comando rápido para verificar todo

Ejecuta en PowerShell como Administrador:

```powershell
# Verificar handlers
Get-WebHandler -PSPath "IIS:\Sites\Default Web Site" | Where-Object {$_.Name -like "*svc*"}

# Verificar Application Pool
Get-WebApplicationPool | Select-Object Name, ManagedRuntimeVersion, ManagedPipelineMode

# Verificar que el sitio existe
Get-Website | Where-Object {$_.State -eq "Started"}
```


