# Configurar Visual Studio para No Detenerse en Excepciones SOAP

Si Visual Studio sigue mostrando el mensaje morado (error) cuando el servidor SOAP está desconectado, aunque la aplicación funcione correctamente, puedes configurar Visual Studio para que NO se detenga en estas excepciones.

## Pasos para Configurar Visual Studio

### Opción 1: Desactivar Excepciones de System.ServiceModel (Recomendado)

1. En Visual Studio, ve a **Depurar (Debug)** → **Windows** → **Configuración de excepciones** (o presiona `Ctrl+Alt+E`)

2. En la ventana "Configuración de excepciones", busca y expande:
   - **System.ServiceModel**

3. Desmarca las siguientes excepciones:
   - `System.ServiceModel.CommunicationException`
   - `System.ServiceModel.EndpointNotFoundException`
   - `System.ServiceModel.FaultException`

4. También busca y desmarca:
   - **System.Net.Sockets** → `SocketException`
   - **System.Net.Http** → `HttpRequestException`

5. Cierra la ventana

### Opción 2: Usar Solo "Continuar" cuando Aparezca el Error

Cuando Visual Studio muestre el mensaje morado:
1. En lugar de hacer clic en "Detener", haz clic en **"Continuar"** (F5)
2. La aplicación seguirá funcionando y mostrará el mensaje de error amigable en el navegador

### Opción 3: Ejecutar sin Depuración

1. En lugar de presionar **F5** (Iniciar con depuración)
2. Presiona **Ctrl+F5** (Iniciar sin depuración)
3. Esto evitará que Visual Studio se detenga en cualquier excepción

## Nota Importante

✅ **El código ya está protegido**: La aplicación NO se caerá, incluso si Visual Studio muestra el error.

✅ **El error se muestra en la web**: El usuario verá el mensaje amigable "Servidor desconectado..." en el navegador.

**Visual Studio es solo una notificación**: El mensaje morado es solo Visual Studio notificando sobre la excepción, pero la aplicación maneja el error correctamente.

## Verificación

1. Apaga el servidor SOAP
2. Ejecuta el cliente web
3. Intenta hacer una conversión
4. **Deberías ver**: El mensaje "Servidor desconectado..." en el navegador
5. **NO debería pasar**: La aplicación cerrándose o rompiéndose


