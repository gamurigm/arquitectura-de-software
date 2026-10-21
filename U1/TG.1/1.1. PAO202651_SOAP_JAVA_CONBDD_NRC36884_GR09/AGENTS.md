# EurekaBank SOAP Java GR09

- Este proyecto implementa operaciones bancarias EurekaBank con SOAP y Java. No es el conversor de unidades.
- Usa MySQL mediante JDBC. Incluye el controlador con Maven: `com.mysql:mysql-connector-j` en el `pom.xml` del servidor.
- Despliega el servidor WAR exclusivamente en Payara Server Community 7. Usa su configuración y sus herramientas oficiales.
- Mantén el servidor en `04. SERVIDOR` y los clientes en `04. CLICON`, `04. CLIESC`, `04. CLIMOV` y `04. CLIWEB`.
- Conserva `01. UML`, `02. MER`, `03. BDD` y `05. DOCUMENTACION`; los scripts MySQL viven en `03. BDD`.
- No guardes credenciales de base de datos en el código ni en Git.
