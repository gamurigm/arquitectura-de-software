# Payara REST GR09 Implementation Plan

**Estado:** aprobado para implementación por el usuario.

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Desplegar el backend REST y el cliente web REST GR09 en el dominio Payara preparado, y completar el corte desde GlassFish/Spring Boot.

**Architecture:** Convertir el backend Spring Boot 3.5.5 de JAR a WAR tradicional, iniciable por Payara y con contexto raíz `/` para conservar `/api/...`. Desplegar también el cliente REST WAR en `/rest-web`; después de las verificaciones de preparación, Payara toma los puertos actuales `8080` y `19080`.

**Tech Stack:** Windows PowerShell 5.1, Azul Payara Community Server 7.2026.10, Temurin JDK 25.0.4+101 en NetBeans/compilación/runtime con bytecode Java 21, Spring Boot 3.5.5, Maven 3.10.0 vía `mise`, Jakarta Servlet.

**Spec:** `docs/superpowers/specs/2026-10-21-payara-gr09-migration-design.md`

## Global Constraints

- Este plan se ejecuta después de `2026-10-21-payara-gr09-soap-plan.md`; reutiliza dominio `gr09-payara`.
- NetBeans, compilación, pruebas y Payara usan JDK 25, conservando `release`/bytecode Java 21. Maven y el repositorio local de dependencias se mantienen bajo `..\tools`.
- Alcance: solo los módulos activos Java web REST GR09.
- El backend REST conserva `/api/...` en el contexto raíz y el puerto público final `8080`.
- El cliente web REST conserva el contexto `/rest-web`; la interfaz permite configurar el host API durante el ensayo.
- La autenticación REST y las sesiones siguen en memoria; volver a iniciar el backend invalida los tokens anteriores.
- No cambiar contratos, operaciones, credenciales iniciales, JSON, autenticación ni lógica de conversión.
- No tocar copias GR03, GR05 o GR69, aplicaciones .NET ni clientes nativos.
- El corte final solo se realiza después de smoke checks exitosos en el listener Payara temporal `18080`.

## Review Focus

- El backend WAR adquiere contexto `/WS_ConUni_Java_REST` por su nombre: desplegar con contexto raíz y comprobar `/api/salud`.
- Tomcat embebido interfiere con Payara: declarar su dependencia como `provided` y desplegar el WAR en el contenedor.
- La inicialización Servlet informa que CDI no está disponible en Payara: declarar `WEB-INF/beans.xml` CDI 4.1 con `bean-discovery-mode="annotated"` y volver a desplegar.
- La UI REST llama al backend antiguo durante la prueba: configurar base API `http://localhost:18080` en el formulario de acceso y comprobar autenticación/conversión en Payara.
- La UI REST deja de apuntar al host API esperado tras el corte: probar la URL predeterminada `http://localhost:8080` desde la aplicación desplegada.
- Tokens volátiles se pierden durante el corte: realizar nuevo login y una conversión protegida después de reiniciar.

---

## Archivos y responsabilidades

- `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/pom.xml` — empaquetado WAR y Tomcat embebido provisto por el contenedor.
- `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/src/main/java/ec/edu/monster/AplicacionRestConversiones.java` — inicialización Spring Boot tanto para Payara como para `java -jar`.
- `scripts/payara-gr09.ps1` — ampliar despliegue para backend REST en `/` y cliente web REST en `/rest-web`; cerrar el dominio con los puertos públicos.
- `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/README.md`, `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/README.md`, `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/README.md` y `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. CLIWEB/README.md` — instrucciones activas de compilación y ejecución en Payara.

## Task 1: Empaquetar Spring Boot como WAR

**Files:**
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/pom.xml`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/src/main/java/ec/edu/monster/AplicacionRestConversiones.java`

- Create: U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/src/main/webapp/WEB-INF/beans.xml

**Interfaces:**
- El artefacto sigue llamándose `WS_ConUni_Java_REST.war`.
- El método `configure(SpringApplicationBuilder)` registra `AplicacionRestConversiones.class` para carga por Servlet.
- Los controladores conservan las rutas `/api/salud`, `/api/auth/login` y `/api/conversiones/...`.

- [x] **Step 1: Ejecutar las pruebas existentes del backend antes de cambiar el empaquetado.** Usar Maven local y JDK 25 con release Java 21 desde el módulo. Esperado: se registran los resultados de `PruebaServicioConversion` y `PruebaApiConversion`.
- [x] **Step 2: Implementar el inicializador Servlet en `AplicacionRestConversiones.java`.** Extender `SpringBootServletInitializer` y devolver `builder.sources(AplicacionRestConversiones.class)` desde `configure`.
- [x] **Step 3: Modificar `pom.xml`.** Cambiar a `war`, agregar `spring-boot-starter-tomcat` con alcance `provided` y mantener `spring-boot-maven-plugin`, dependencias y `finalName`.
- [x] **Step 4: Construir y probar el WAR.** Ejecutar `mvn clean verify`; esperado: pruebas existentes verdes y `target/WS_ConUni_Java_REST.war` presente.
- [x] **Step 5: Comprobar contexto raíz en Payara temporal.** Desplegar el WAR con contexto `/` en listener `18080`; `GET http://localhost:18080/api/salud` debe responder 200 y el WAR no debe exponer `/WS_ConUni_Java_REST/api/salud` como ruta requerida.

- [x] **Step 6: Declarar CDI para Payara.** Tras observar `CDI is not available` en el primer despliegue, añadir `WEB-INF/beans.xml` CDI 4.1 con `bean-discovery-mode="annotated"`; reconstruir y confirmar que la API y el cliente web cargan.

## Task 2: Desplegar el cliente web REST y completar la configuración Payara

**Files:**
- Modify: `scripts/payara-gr09.ps1`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/README.md`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/README.md`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WS_ConUni_Java_REST/README.md`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09/04. CLIWEB/README.md`

**Interfaces:**
- `-Action Deploy -Target Rest` instala el WAR de API en `/` y el WAR cliente en `/rest-web`.
- `-Action ConfigureFinal` conserva listener `8080` y añade listener `19080` al mismo virtual server.
- `-Action Status` lista las cuatro aplicaciones y sus context roots.

- [x] **Step 1: Ampliar el operador Payara para REST.** Agregar el artefacto REST root y cliente REST con sus contextos exactos, validando que ambos WAR existen antes del despliegue.
- [x] **Step 2: Actualizar la documentación REST activa.** Reemplazar instrucciones de ejecutar GlassFish/Tomcat/JAR separado por pasos Payara; documentar `/`, `/rest-web`, `/api`, login, configuración de host y operaciones de estado/parada.
- [x] **Step 3: Construir y probar el cliente REST web.** Ejecutar `mvn test package` desde `04. CLIWEB/Cliente_Web` con JDK 25 y release Java 21. Esperado: `target/Cliente_Web.war` generado.
- [x] **Step 4: Desplegar y probar REST en puerto temporal.** Ejecutar `Deploy -Target Rest`; consultar `/api/salud` y `/rest-web/inicio.jsp`; en la vista de acceso indicar `http://localhost:18080`, completar login y una conversión protegida. Esperado: todas las peticiones llegan al backend Payara temporal.

## Task 3: Cortar a Payara y verificar los endpoints públicos

**Files:**
- Modify: `scripts/payara-gr09.ps1`
- Modify: documentación activa de SOAP/REST de los módulos anteriores

**Interfaces:**
- Puerto `8080`: backend REST en contexto raíz.
- Puerto `19080`: SOAP `/04.SERVIDOR`, UI SOAP `/soap-web` y UI REST `/rest-web`; el mismo dominio Payara sirve las cuatro aplicaciones.

- [x] **Step 1: Confirmar las comprobaciones de preparación.** Esperado: WSDL, salud REST, login/conversión SOAP, login/conversión REST y ambas páginas web responden en `18080`.
- [x] **Step 2: Detener únicamente los procesos actuales de GlassFish GR09 y Spring Boot REST.** Identificar por PID y comando antes de detener; preservar intacta la distribución/dominio GlassFish para rollback.
- [x] **Step 3: Aplicar configuración final e iniciar Payara.** Ejecutar `ConfigureFinal`, `Start`, `Deploy -Target All` y `Status`; esperado: producto Payara 7.2026.10 con cuatro aplicaciones activas.
- [x] **Step 4: Verificar los URL estables.** Consultar `http://localhost:8080/api/salud`, WSDL `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09?wsdl`, `/soap-web/inicio.jsp`, `/soap-web/conversor.jsp` y `/rest-web/inicio.jsp`; repetir login/conversión REST y SOAP.
- [x] **Step 5: Verificar procesos y capacidad de recuperación.** Esperado: puertos `8080` y `19080` pertenecen a Payara y ningún proceso antiguo atiende esas URL; se conserva el dominio GlassFish anterior y el WAR ejecutable REST como recursos de recuperación.
- [x] **Step 6: Revisar alcance y documentación.** `git status --short` y búsquedas acotadas a las carpetas activas GR09; esperado: ninguna modificación a las instantáneas históricas y ninguna instrucción activa que pida GlassFish.
