# Cliente web EurekaBank SOAP Java GR09

Cliente web Java con MVC, Jakarta Servlet/JSP y comunicación SOAP. Incluye inicio y cierre de sesión, consulta de movimientos y depósitos. Las vistas no abren conexiones al servidor ni a MySQL.

Requiere JDK 21 para el WAR y Payara Community 7. El JDK 25 del equipo puede compilarlo con `--release 21`.

Configure `EUREKABANK_SOAP_URL` en el archivo compartido `U1/TG.1/.env` antes de iniciar Payara. El valor inicial es `http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka`. No guarde credenciales de base de datos en este cliente.

Abra los proyectos GR09 con `Abrir-Proyectos-Java-NetBeans.ps1` desde la raíz del repositorio y ejecute **Run Project** en este módulo para desplegarlo en Payara. También se puede compilar el WAR desde esta carpeta:

```powershell
mvn package -DskipTests
```

El WAR y sus artefactos temporales quedan en `tools/build-cache/eurekabank-gr09/cliente-web`, junto a la carpeta del repositorio.
