# Cliente web EurekaBank SOAP Java GR09

Cliente web Java con MVC, Jakarta Servlet/JSP y comunicación SOAP. Incluye inicio y cierre de sesión, consulta de movimientos y depósitos. Las vistas no abren conexiones al servidor ni a MySQL.

Requiere JDK 25, Maven y Payara Community 7.

Configure `EUREKABANK_SOAP_URL` en el archivo compartido `U1/TG.1/.env` antes de iniciar Payara. El valor inicial es `http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka`. No guarde credenciales de base de datos en este cliente.

Compile el WAR desde esta carpeta:

```powershell
mvn package -DskipTests
```

Despliegue `D:\tools\build-cache\eurekabank-gr09\cliente-web\ClienteWebEurekaBank.war` en Payara 7.
