# Cliente Android MAUI

Consume la API de la comercializadora y permite catálogo, ventas y consultas. Para requisitos y ejecución, consulte `../05. DOCUMENTACION/ManualTecnico.md`.

La URL se configura internamente en `Celulares.Movil/MauiProgram.cs`, constante `DireccionServidor`. El emulador usa `http://10.0.2.2:19102/` y conecta automáticamente; no existe un campo de servidor en la interfaz.

Para instalar el APK directamente con `adb install -r`, compile con `-p:EmbedAssembliesIntoApk=true`. Sin esta opción, un APK Debug puede requerir el despliegue rápido de ensamblados desde el IDE.
