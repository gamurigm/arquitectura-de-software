# Instrucciones para Configurar WCF en IIS

## Opción 1: Desbloquear secciones en IIS (Recomendado)

Ejecuta el archivo `desbloquear_handlers.bat` **como Administrador**. Esto desbloqueará las secciones necesarias para que el Web.config funcione.

Luego usa el `Web.config` normal que incluye los handlers.

---

## Opción 2: Configurar Handler en IIS Manager (Sin modificar Web.config)

Si no puedes desbloquear las secciones, sigue estos pasos:

### Paso 1: Usar Web.config simplificado

Usa el archivo `Web.config.sin_handlers` renombrado como `Web.config` en tu servidor (`C:\servidores\soap_dotnet\web.config`).

### Paso 2: Configurar Handler en IIS Manager

1. Abre **IIS Manager**
2. Selecciona tu **servidor** (no el sitio, sino el servidor en la raíz)
3. Haz doble clic en **"Asignación de controladores"** (Handler Mappings)
4. Busca el handler **"svc-Integrated-4.0"**
5. Si no existe, haz clic en **"Agregar asignación de módulo..."** en el panel derecho
6. Configura:
   - **Ruta de solicitud:** `*.svc`
   - **Módulo:** `System.ServiceModel.Activation.ServiceHttpHandlerFactory`
   - **Nombre:** `svc-Integrated-4.0`
   - **Precondición:** `integratedMode,runtimeVersionv4.0`
7. Haz clic en **"Aceptar"**

### Paso 3: Configurar Documento Predeterminado

1. Selecciona tu **sitio** (puerto 8085)
2. Haz doble clic en **"Documentos predeterminados"**
3. En el panel de acciones, haz clic en **"Agregar..."**
4. Escribe: `Service1.svc`
5. Mueve `Service1.svc` al **inicio de la lista** (usa "Subir")

### Paso 4: Verificar Application Pool

1. Selecciona tu sitio
2. En el panel de acciones, haz clic en **"Configuración básica..."**
3. Verifica que el Application Pool tenga:
   - **.NET CLR Version:** v4.0
   - **Managed Pipeline Mode:** Integrated

### Paso 5: Reiniciar IIS

```powershell
iisreset
```

---

## Opción 3: Comando PowerShell (Desbloquear)

Ejecuta como Administrador:

```powershell
%windir%\system32\inetsrv\appcmd unlock config -section:system.webServer/handlers
%windir%\system32\inetsrv\appcmd unlock config -section:system.webServer/modules
%windir%\system32\inetsrv\appcmd unlock config -section:system.webServer/defaultDocument
```

---

## Verificar que WCF HTTP Activation esté instalado

1. Abre **"Activar o desactivar características de Windows"**
2. Navega a: **Servicios de IIS** → **Servicios de World Wide Web** → **Características de desarrollo de aplicaciones**
3. Asegúrate de que **"Activación HTTP de WCF"** esté marcado

Si no está instalado, instálalo y reinicia IIS.

---

## Probar el servicio

Después de configurar, prueba:
- `http://localhost:8085/Service1.svc`
- `http://localhost:8085/Service1.svc?wsdl`

