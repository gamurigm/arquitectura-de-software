# Diseño: migración de los proyectos Java web GR09 a Payara

**Estado:** aprobado para implementación
**Fecha:** 2026-10-21

## Objetivo

Ejecutar en Payara todos los servidores y clientes Java web activos del grupo GR09, manteniendo las URL públicas que usan hoy los clientes web, móviles, de escritorio y consola. Las copias históricas de otros grupos quedan fuera del cambio.

## Alcance

Migrar estos cuatro artefactos activos:

1. Servicio SOAP: `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WSConversionUnidades_Java_SOAP_GR09`.
2. Cliente web SOAP: la carpeta `04. CLIWEB/Cliente_Web` del mismo proyecto SOAP.
3. Servicio REST: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST`.
4. Cliente web REST: la carpeta `04. CLIWEB/Cliente_Web` del proyecto REST.

No se migran ni modifican copias históricas GR03, GR05 y GR69, aplicaciones .NET, ni los clientes nativos Android, escritorio y consola de GR09. Estos clientes conservan sus contratos y deben seguir pudiendo conectarse a los mismos endpoints.

## Estado observado

- El servicio SOAP GR09 y las dos aplicaciones web GR09 están desplegados en Eclipse GlassFish 8.0.5, dominio `gr09`, puerto HTTP `19080`.
- El backend REST GR09 corre aparte como proceso Spring Boot ejecutable en el puerto `8080`; ahora produce un JAR.
- Las rutas comprobadas incluyen `/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09?wsdl`, `/soap-web/inicio.jsp`, `/soap-web/conversor.jsp` y `/rest-web/inicio.jsp`.
- El backend REST publica rutas bajo `/api`, incluyendo `GET /api/salud`, con el puerto `8080` como valor predeterminado.
- El módulo SOAP del servidor ya usa Jakarta EE 11 y Java 21. El backend REST usa Spring Boot 3.5.5 y Java 21. Ambos clientes web producen WAR.
- Payara no está instalado en la carpeta local de herramientas; allí está la distribución de GlassFish actual.

## Diseño propuesto

### Runtime y puertos

- Usar JDK 25 para NetBeans, compilación, pruebas, Payara y las aplicaciones. Conservar el nivel de bytecode Java 21 definido por los proyectos. JDK 25 está soportado por Payara Community 7 y NetBeans 31.
- Instalar JDK 25, Maven y **Payara Server Community 7.2026.10** bajo `..\tools` en el único disco disponible, `C:`; usar la distribución oficial de Payara. No instalar un runtime global ni modificar el PATH persistente.
- Iniciar NetBeans con JDK 25 mediante el acceso directo personal `Apache NetBeans JDK 25` (opción `--jdkhome`); conservar una copia de `netbeans.conf`. La ACL de Windows impide editar la configuración compartida en `Program Files` desde la sesión actual.
- Crear un dominio local dedicado a GR09 y configurar dos listeners HTTP: `8080` y `19080`. Ambos deben servir los despliegues del dominio.
- Mantener el backend REST en el contexto raíz (`/`) para que conserve `/api/...`; desplegar las interfaces web en `/rest-web` y `/soap-web`, y el servicio SOAP en `/04.SERVIDOR`.
- Mantener las URL por defecto de los clientes REST en `localhost:8080` y las URL SOAP en `127.0.0.1:19080`. Para Android se conserva el host emulador `10.0.2.2` y el puerto correspondiente.
- El puerto de administración de Payara será local al equipo y se elegirá libre durante la configuración; no forma parte del contrato usado por las aplicaciones.

### Empaquetado y configuración de aplicaciones

- Convertir el backend REST Spring Boot de JAR a WAR desplegable: añadir soporte `SpringBootServletInitializer`, usar empaquetado WAR y declarar el contenedor embebido Tomcat como `provided`, siguiendo la guía oficial de Spring Boot.
- Conservar la posibilidad de ejecutar el WAR con `java -jar` si el empaquetado final y las dependencias lo permiten, para facilitar recuperación local.
- Mantener ambos clientes web como WAR y ajustar solo la configuración necesaria para su ejecución en Payara.
- Sustituir el descriptor específico de GlassFish del servicio SOAP por `payara-web.xml` cuando corresponda, preservando el contexto `/04.SERVIDOR`.
- Revisar referencias de servidor en los scripts y la documentación activa de GR09; actualizarlas a Payara y documentar cómo compilar, desplegar, iniciar, consultar estado y detener el dominio.
- No modificar contratos SOAP/REST, operaciones, autenticación, modelos, lógica de conversión ni el MVC de los clientes.

### Migración con corte controlado

1. Construir los cuatro WAR sin detener los procesos actuales.
2. Iniciar Payara temporalmente en puertos libres y desplegar las cuatro aplicaciones allí.
3. Comprobar el WSDL SOAP, salud REST, autenticación y una conversión protegida, además de las páginas de ambos clientes web.
4. Si pasan las comprobaciones, detener únicamente el dominio GlassFish activo y el proceso Spring Boot del backend REST; configurar Payara con los puertos estables `19080` y `8080`, e iniciar los despliegues nuevos.
5. Volver a comprobar las URL originales y confirmar que los puertos están atendidos por Payara.

Se conserva intacta la distribución y el dominio GlassFish existentes durante la migración como respaldo local. Si la puesta en marcha de Payara falla, se detiene Payara y se puede volver a iniciar el runtime actual con sus mismos despliegues. Los tokens REST existentes son volátiles y se pierden al reiniciar el backend, tanto en el corte como en un rollback.

## Criterios de aceptación

- El proceso que atiende `8080` y `19080` pertenece a Payara; no queda GlassFish ni un proceso Spring Boot independiente atendiendo esas aplicaciones.
- Payara muestra desplegados el servicio SOAP, el backend REST y las dos interfaces web.
- `GET http://localhost:8080/api/salud` responde correctamente.
- El WSDL responde en `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09?wsdl`.
- Las páginas `/soap-web/inicio.jsp`, `/soap-web/conversor.jsp` y `/rest-web/inicio.jsp` responden desde el puerto `19080`.
- La autenticación y conversión de ambos contratos funcionan desde un smoke test local.
- La documentación activa GR09 identifica Payara como runtime y deja instrucciones para operar el dominio.
- No hay cambios en las instantáneas GR03, GR05 ni GR69.

## Referencias técnicas

- [Spring Boot: Traditional Deployment](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)
- [Payara Server: comando `deploy`](https://docs.payara.fish/community/docs/6.2025.7/Technical%20Documentation/Payara%20Server%20Documentation/Command%20Reference/deploy.html)
- [Payara Server 7: resultados de certificación Jakarta EE](https://docs.payara.fish/community/docs/7.2026.1/Jakarta%20EE%20Certification/7.2026.1/7.2026.1%20Platform%20TCK%20Results.html)
