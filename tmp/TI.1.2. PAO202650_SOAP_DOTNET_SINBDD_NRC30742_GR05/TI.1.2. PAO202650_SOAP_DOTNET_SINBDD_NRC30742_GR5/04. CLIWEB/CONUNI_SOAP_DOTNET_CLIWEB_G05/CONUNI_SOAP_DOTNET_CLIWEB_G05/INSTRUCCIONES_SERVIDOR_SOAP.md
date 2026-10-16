# Instrucciones para Ejecutar el Servidor SOAP

## IMPORTANTE

**El cliente web NO funcionará hasta que el servidor SOAP esté ejecutándose.**

## Pasos para Ejecutar el Servidor SOAP

### 1. Abrir el Proyecto del Servidor

1. Abre Visual Studio
2. Abre el archivo de solución: `CONUNI_SOAP_DOTNET_GR09.sln`
   - Ubicación: `TI1.2 SOAP_DOTNET_SINBDD_GR09\01.SERVIDOR\CONUNI_SOAP_DOTNET_GR09\`

### 2. Configurar el Proyecto

El proyecto está configurado para ejecutarse en:
- **URL**: `http://localhost:62533/`
- **Endpoint del servicio**: `http://localhost:62533/Service1.svc`

### 3. Ejecutar el Servidor

1. En Visual Studio, presiona **F5** o haz clic en **Ejecutar**
2. El servidor se iniciará y deberías ver una página en el navegador mostrando información del servicio
3. Verifica que el servicio esté disponible en: `http://localhost:62533/Service1.svc`
4. Verifica el WSDL en: `http://localhost:62533/Service1.svc?wsdl`

### 4. Verificar que el Servidor Está Funcionando

Abre tu navegador y visita:
- `http://localhost:62533/Service1.svc` - Debería mostrar información del servicio
- `http://localhost:62533/Service1.svc?wsdl` - Debería mostrar el WSDL (XML)

Si ves estos archivos XML, el servidor está funcionando correctamente.

### 5. Ejecutar el Cliente Web

**Solo después** de que el servidor SOAP esté ejecutándose:

1. Abre el proyecto cliente web: `CONUNI_SOAP_DOTNET_CLIWEB_G09`
2. Ejecuta el proyecto cliente (F5)
3. Ingresa con usuario: `MONSTER` y contraseña: `MONSTER9`
4. Ahora deberías poder hacer conversiones sin errores

## Solución de Problemas

### Error: "No se puede establecer una conexión"

**Causa**: El servidor SOAP no está ejecutándose.

**Solución**:
1. Asegúrate de que el servidor SOAP esté ejecutándose en el puerto 62533
2. Verifica que no haya otro proceso usando ese puerto
3. Revisa que el proyecto del servidor esté configurado correctamente

### Error: "Puerto ya en uso"

**Causa**: Otro proceso está usando el puerto 62533.

**Solución**:
1. Cierra otros procesos que puedan estar usando el puerto
2. O cambia el puerto en el `.csproj` del servidor y actualiza el endpoint en `appsettings.json` del cliente

### Cambiar el Puerto del Servidor

Si necesitas cambiar el puerto:

1. **En el servidor** (`CONUNI_SOAP_DOTNET_GR09.csproj`):
   - Busca `<DevelopmentServerPort>62533</DevelopmentServerPort>`
   - Cambia el puerto

2. **En el cliente** (`appsettings.json`):
   - Actualiza: `"Endpoint": "http://localhost:NUEVO_PUERTO/Service1.svc"`

## Configuración del Endpoint

El endpoint del servicio SOAP se puede configurar en:
- `appsettings.json` → Sección `SoapService:Endpoint`
- Por defecto: `http://localhost:62533/Service1.svc`

## Notas

- El servidor SOAP debe ejecutarse **antes** que el cliente web
- El servidor SOAP debe permanecer ejecutándose mientras uses el cliente web
- Si detienes el servidor SOAP, el cliente web mostrará errores de conexión


