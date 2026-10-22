# Cliente de escritorio EurekaBank

Cliente SOAP con Java Swing y arquitectura MVC. Su interfaz usa una paleta azul noche, paneles translúcidos y controles de alto contraste. Consume el servicio desplegado en Payara 7; no se conecta directamente a MySQL.

## Requisitos

- JDK 25 y Maven.
- Servicio EurekaBank disponible en Payara 7.

## Ejecutar

Configure `EUREKABANK_SOAP_URL` en el archivo compartido `U1/TG.1/.env` y ejecute:

```powershell
.\run.ps1
```

La URL por defecto es `http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka?wsdl`. No ponga credenciales de base de datos en este cliente.

## Estructura

- `src/main/java/ec/edu/monster/controlador`: lógica y flujo de la interfaz.
- `src/main/java/ec/edu/monster/modelo`: datos de movimientos.
- `src/main/java/ec/edu/monster/service`: consumo SOAP.
- `src/main/java/ec/edu/monster/vista`: login, menú, movimientos y depósitos Swing.
- `src/META-INF/wsdl`: WSDL y esquema locales para generar el proxy SOAP.
- `tests`: pruebas de controlador y flujo visual con un servicio SOAP local simulado.

## Verificar

```powershell
mvn test
mvn '-Deurekabank.ui.tests=true' test
```

La segunda orden incluye pruebas de interfaz y requiere un escritorio Windows disponible.
