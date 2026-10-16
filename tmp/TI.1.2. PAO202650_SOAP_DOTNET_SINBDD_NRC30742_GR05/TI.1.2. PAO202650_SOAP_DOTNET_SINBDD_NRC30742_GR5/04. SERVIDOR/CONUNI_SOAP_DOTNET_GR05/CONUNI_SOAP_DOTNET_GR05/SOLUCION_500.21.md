# Solución Error HTTP 500.21 - Handler svc-Integrated-4.0

Este error indica que ASP.NET no está completamente instalado o configurado.

## Solución Paso a Paso

### Paso 1: Instalar ASP.NET 4.7.2 en IIS

Ejecuta estos comandos en PowerShell como **Administrador**:

```powershell
# Instalar ASP.NET 4.7.2
Enable-WindowsOptionalFeature -Online -FeatureName IIS-ASPNET45

# O usando dism
dism /online /enable-feature /featurename:IIS-ASPNET45 /all
```

### Paso 2: Registrar ASP.NET en IIS

Ejecuta este comando (ajusta la ruta según tu versión de .NET Framework):

```powershell
# Para .NET Framework 4.x (64-bit)
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\aspnet_regiis.exe -i

# Si el comando anterior no funciona, intenta:
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\aspnet_regiis.exe -iru
```

### Paso 3: Verificar que ASP.NET esté habilitado en IIS

1. Abre **IIS Manager**
2. Selecciona el **SERVIDOR** (raíz)
3. Haz doble clic en **"Módulos"** (Modules)
4. Busca estos módulos (deben estar presentes):
   - `ManagedEngine`
   - `UrlRoutingModule-4.0`
   - `DefaultDocumentModule`
   
Si no están, ASP.NET no está instalado correctamente.

### Paso 4: Verificar Application Pool

1. En IIS Manager, selecciona tu **sitio** (puerto 8085)
2. Haz clic en **"Configuración básica..."**
3. Verifica el Application Pool:
   - **.NET CLR Version:** Debe ser **v4.0** (NO "Sin código administrado")
   - **Managed Pipeline Mode:** **Integrated**

4. Si el Application Pool no tiene estas características:
   - Haz clic en **"Seleccionar..."** → **"Administrar grupos de aplicaciones..."**
   - Crea un nuevo Application Pool:
     - Nombre: `WCFAppPool` (o el que prefieras)
     - .NET CLR Version: **v4.0**
     - Managed Pipeline Mode: **Integrated**
   - Asigna este nuevo Application Pool a tu sitio

### Paso 5: Verificar Handler svc-Integrated-4.0

1. En IIS Manager, selecciona el **SERVIDOR** (raíz)
2. Haz doble clic en **"Asignación de controladores"** (Handler Mappings)
3. Busca **"svc-Integrated-4.0"**
4. Si existe pero está deshabilitado:
   - Haz clic derecho → **"Editar permisos de función..."**
   - Marca **"Ejecutar"** y **"Lectura"**
   - Haz clic en **"Aceptar"**
5. Si no existe o está mal configurado:
   - Haz clic en **"Restaurar al elemento primario"** en el panel derecho
   - Esto restaurará todos los handlers predeterminados

### Paso 6: Reiniciar IIS

```powershell
iisreset
```

### Paso 7: Probar el servicio

Abre en el navegador:
- `http://localhost:8085/Service1.svc`
- `http://localhost:8085/Service1.svc?wsdl`

---

## Comandos de Verificación

Ejecuta estos comandos para verificar la instalación:

```powershell
# Verificar que ASP.NET está instalado
Get-WindowsOptionalFeature -Online | Where-Object {$_.FeatureName -like "*ASPNET*"}

# Verificar Application Pools
Get-WebApplicationPool | Select-Object Name, ManagedRuntimeVersion, ManagedPipelineMode

# Verificar handlers
Get-WebHandler -PSPath "IIS:\Sites" | Where-Object {$_.Name -like "*svc*"}
```

---

## Si el problema persiste

### Opción A: Reinstalar WCF HTTP Activation

1. Abre **"Activar o desactivar características de Windows"**
2. Desmarca **"Activación HTTP de WCF"**
3. Acepta y reinicia
4. Vuelve a marcar **"Activación HTTP de WCF"**
5. Acepta y reinicia IIS

### Opción B: Verificar que .NET Framework 4.7.2 esté instalado

```powershell
# Verificar versión de .NET instalada
Get-ChildItem "C:\Windows\Microsoft.NET\Framework64\v4.0*" | Select-Object Name
```

Si no ves una carpeta con 4.7.2, necesitas instalar .NET Framework 4.7.2 o superior.

### Opción C: Verificar permisos del Application Pool

1. En IIS Manager, selecciona tu **Application Pool**
2. Haz clic en **"Configuración avanzada..."**
3. Verifica:
   - **Identity:** Debe ser `ApplicationPoolIdentity` o un usuario con permisos adecuados
4. Asegúrate de que el usuario del Application Pool tenga permisos de lectura en `C:\servidores\soap_dotnet`

