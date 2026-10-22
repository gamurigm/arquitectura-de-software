# Cliente móvil EurekaBank SOAP Java GR09

Aplicación Android Java con MVC. Permite iniciar y cerrar una sesión local, consultar movimientos y registrar depósitos mediante SOAP. No guarda credenciales ni se conecta a MySQL.

Requiere JDK 21, Android SDK 35 y Gradle 8.11.1. En el emulador, el servicio local usa `http://10.0.2.2:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka`.

Para cambiar el servicio durante la compilación, use `-PeurekabankSoapUrl=https://servidor/WSEureka`.

```powershell
$env:JAVA_HOME = 'D:\tools\jdk\jdk-21.0.9+10'
$env:ANDROID_HOME = 'D:\tools\android-sdk'
$env:GRADLE_USER_HOME = 'D:\tools\gradle-home'
.\gradlew.bat assembleDebug
```

Los depósitos inciertos no se reenvían automáticamente; consulte los movimientos antes de intentar de nuevo.
