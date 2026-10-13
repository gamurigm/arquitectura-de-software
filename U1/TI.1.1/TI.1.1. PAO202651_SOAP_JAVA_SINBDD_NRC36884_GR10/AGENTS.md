# Instrucciones del proyecto GR10

## Alcance y estructura

- Este proyecto implementa conversión de unidades con **SOAP y Java**. Usa ese contrato y sus operaciones como fuente de verdad para los clientes.
- Conserva los nombres y la estructura numerada existentes (`01. UML`, `04. CLICON`, `04. CLIESC`, `04. CLIMOV`, `04. CLIWEB`, `04. SERVIDOR`, etc.). No renombres ni reordenes carpetas.
- Trabaja un cliente a la vez, en este orden acordado: **web, móvil, escritorio**. El servidor vive en `04. SERVIDOR` y la consola en `04. CLICON`.
- Mantén código, documentación, mensajes y explicaciones en español. Conserva literalmente los nombres SOAP y los identificadores exigidos por bibliotecas o protocolos.
- Ejecuta los comandos desde el directorio del módulo que contiene el `pom.xml` o `build.gradle.kts` correspondiente. El proyecto móvil usa Gradle y los módulos Java usan Maven.
- Ejecuta y prueba el proyecto localmente. No uses Docker; cuando el usuario vaya a iniciar el servidor manualmente, déjalo detenido.

## Contrato SOAP de GR10

- El endpoint predeterminado local es `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR10`; permite cambiar la dirección con la propiedad `ws.base.url` o la variable `WS_BASE_URL`.
- El emulador Android debe usar `http://10.0.2.2:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR10`; su dirección está en `04. CLIMOV/app/src/main/res/values/strings.xml` y debe permitirse en la configuración de red Android.
- El servicio publica `login`, `cambiarContrasenia`, `convertirLongitud`, `convertirMasa` y `convertirTemperatura` bajo el espacio de nombres `http://ws.controladores.monster.edu.ec/`.
- `login` entrega un token. Las operaciones protegidas lo reciben en el encabezado SOAP `token`; el servidor lo vence a los 15 minutos. Los clientes deben manejar la sesión vencida y pedir autenticación otra vez.
- Los nombres, unidades y límites del servidor son autoridad. Antes de cambiar una conversión, consulta sus enums y mapeadores.
- No conectes los clientes GR10 a los WSDL antiguos de otros grupos ni a servicios REST.

## Cambios y verificación

- Antes de editar, revisa el estado de Git y conserva los cambios locales que no correspondan al cliente activo.
- Limita cada paso al cliente acordado y sus archivos necesarios. No edites artefactos generados.
- Reutiliza el contrato SOAP de GR10 y conserva el patrón de presentación existente salvo que la tarea pida cambiarlo.
- Verifica la compilación del módulo modificado y comunica si la prueba real requiere que el servidor esté desplegado y accesible.
