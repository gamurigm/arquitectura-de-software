# Cliente móvil SOAP GR09

Aplicación Android .NET MAUI 10. Presenta vistas MAUI, controlador de conversión, modelos de sesión y unidades, y servicio SOAP 1.1.

## Ejecutar

1. Inicia manualmente el servidor desde `04. SERVIDOR\IniciarServidor.ps1`.
2. Instala las dependencias Android con .NET MAUI 10 y OpenJDK 21. Define `DOTNET_ROOT`, `DOTNET_CLI_HOME`, `NUGET_PACKAGES`, `TEMP`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `ANDROID_USER_HOME` y `JAVA_HOME` bajo `D:\tools`.
3. Ejecuta `dotnet build ClienteMovil\ClienteMovilGR09\ClienteMovilGR09.csproj -t:InstallAndroidDependencies -f net10.0-android -p:AndroidSdkDirectory=D:\tools\android-sdk -p:JavaSdkDirectory=D:\tools\jdk\jdk-21.0.9+10 -p:AcceptAndroidSDKLicenses=True --artifacts-path D:\tools\GR09-SOAP-DotNet\artifacts`.
4. Abre el proyecto en Visual Studio, selecciona un emulador Android y ejecuta.

El emulador Android usa `http://10.0.2.2:19081` para alcanzar el equipo anfitrión. Para un teléfono físico, cambia `Urls` en `04. SERVIDOR\WSConversionUnidades_DotNet_SOAP_GR09\appsettings.json` a `http://0.0.0.0:19081` y cambia `ObtenerEndpoint` en `ClienteMovilGR09\Controladores\ControladorConversor.cs` por la IP LAN del equipo. Permite el puerto 19081 en el firewall.

El SDK .NET, JDK, SDK Android, NuGet, temporales y artefactos quedan en `D:\tools`. La instalación de dependencias necesita conexión a los feeds oficiales de Microsoft y Android.
