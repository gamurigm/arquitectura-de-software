# Cliente móvil EurekaBank SOAP Java GR09

Aplicación Android Java con MVC. Permite iniciar y cerrar una sesión local, consultar movimientos y registrar depósitos mediante SOAP. No guarda credenciales ni se conecta a MySQL.

Requiere JDK 21, Android SDK 35 y Gradle 8.11.1. La ruta local del SDK se configura al abrir los proyectos con `Abrir-Proyectos-Java-NetBeans.ps1`. En el emulador, el servicio local usa `http://10.0.2.2:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka`.

Para cambiar el servicio durante la compilación, use `-PeurekabankSoapUrl=https://servidor/WSEureka`.

Desde NetBeans, use **Run Project** con un emulador o teléfono Android conectado; la acción instala y abre la versión debug. **Build Project** genera el APK sin ejecutar pruebas.

Los depósitos inciertos no se reenvían automáticamente; consulte los movimientos antes de intentar de nuevo.
