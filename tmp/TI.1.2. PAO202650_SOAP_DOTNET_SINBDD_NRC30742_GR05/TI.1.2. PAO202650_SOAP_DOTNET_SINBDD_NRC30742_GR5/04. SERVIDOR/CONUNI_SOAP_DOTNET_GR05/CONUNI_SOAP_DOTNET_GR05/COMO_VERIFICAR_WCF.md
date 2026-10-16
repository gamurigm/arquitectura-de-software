# Cómo Verificar si WCF HTTP Activation está Instalado

## Método 1: Usar el Script (Más Rápido)

Ejecuta el archivo `verificar_wcf.bat` como **Administrador**. Te mostrará el estado de todas las características relacionadas.

---

## Método 2: Desde Windows Features (Interfaz Gráfica)

1. Presiona **`Win + R`** y escribe: `optionalfeatures`
2. O busca **"Activar o desactivar características de Windows"** en el menú inicio
3. Navega a:
   ```
   Internet Information Services
   └── Servicios de World Wide Web
       └── Características de desarrollo de aplicaciones
           └── Activación HTTP de WCF
   ```
4. Si está **marcado** ✅ → Está instalado
5. Si **NO está marcado** ❌ → Necesitas instalarlo

---

## Método 3: Desde PowerShell (Línea de Comandos)

Ejecuta en PowerShell como **Administrador**:

```powershell
# Verificar WCF HTTP Activation
Get-WindowsOptionalFeature -Online | Where-Object {$_.FeatureName -like "*WCF*" -or $_.FeatureName -like "*HTTPActivation*"}

# Verificar todas las características relacionadas
Get-WindowsOptionalFeature -Online | Where-Object {
    $_.FeatureName -like "*WCF*" -or 
    $_.FeatureName -like "*HTTP*" -or 
    $_.FeatureName -like "*ASPNET*"
} | Select-Object FeatureName, State | Format-Table
```

**Interpretación:**
- `State = Enabled` → ✅ Está instalado
- `State = Disabled` → ❌ No está instalado

---

## Método 4: Verificar Handlers en IIS Manager

1. Abre **IIS Manager**
2. Selecciona el **SERVIDOR** (raíz, no el sitio)
3. Haz doble clic en **"Asignación de controladores"** (Handler Mappings)
4. Busca estos handlers:
   - `svc-Integrated-4.0` ✅
   - `svc-ISAPI-4.0_32bit` ✅
   - `svc-ISAPI-4.0_64bit` ✅

Si estos handlers existen y están habilitados, WCF HTTP Activation está instalado.

---

## Método 5: Verificar desde IIS usando appcmd

Ejecuta en PowerShell como **Administrador**:

```powershell
# Verificar handlers de .svc
%windir%\system32\inetsrv\appcmd list config -section:system.webServer/handlers | findstr "svc"
```

Si ves handlers con "svc" en la lista, están instalados.

---

## Si NO está Instalado - Instalarlo

### Opción A: Desde Windows Features
1. Abre **"Activar o desactivar características de Windows"**
2. Navega a: **IIS** → **Servicios de World Wide Web** → **Características de desarrollo de aplicaciones**
3. Marca **"Activación HTTP de WCF"**
4. Haz clic en **"Aceptar"**
5. Espera a que se instale
6. Reinicia IIS: `iisreset`

### Opción B: Desde PowerShell
```powershell
# Instalar WCF HTTP Activation
Enable-WindowsOptionalFeature -Online -FeatureName IIS-HTTPActivation

# Reiniciar IIS
iisreset
```

### Opción C: Usar dism
```powershell
dism /online /enable-feature /featurename:IIS-HTTPActivation /all
iisreset
```

---

## Verificación Completa Recomendada

Para un servicio WCF funcionando correctamente, necesitas:

1. ✅ **IIS-ASPNET45** (ASP.NET 4.7.2)
2. ✅ **IIS-HTTPActivation** (WCF HTTP Activation)
3. ✅ **Handler svc-Integrated-4.0** en IIS
4. ✅ **Application Pool** con .NET CLR v4.0

Usa el script `verificar_wcf.bat` para verificar todo de una vez.


