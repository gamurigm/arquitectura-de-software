# Cliente Android

Cliente .NET MAUI 10 con capas Modelos, Vistas, Controladores y Servicios. La pantalla está en Vistas/MainPage.xaml. En emulador Android, la API del equipo anfitrión usa http://10.0.2.2:19082.

La compilación requiere JDK 21 y SDK Android. Configura el entorno desde la raíz y genera artefactos en D:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
$env:JAVA_HOME = 'D:\tools\jdk\jdk-21.0.9+10'
$env:ANDROID_HOME = 'D:\tools\android-sdk'
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
& $env:DOTNET_ROOT\dotnet.exe build '.\04. CLIMOV\ClienteMovil\ClienteMovilGR09\ClienteMovilGR09.csproj' -f net10.0-android -p:AndroidSdkDirectory=$env:ANDROID_HOME --artifacts-path 'D:\tools\GR09-REST-DotNet\artifacts'
~~~

Para un teléfono físico, configura la URL de la API con la IP local del equipo y permite conexiones al puerto 19082.
