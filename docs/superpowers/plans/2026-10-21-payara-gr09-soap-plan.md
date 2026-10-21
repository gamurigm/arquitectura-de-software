# Payara Runtime and SOAP GR09 Implementation Plan

**Estado:** aprobado para implementación por el usuario.

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Servir el servicio SOAP y el cliente web SOAP de GR09 en Payara, primero sobre un puerto de preparación.

**Architecture:** Instalar Azul Payara Community Server 7.2026.10 en `..\tools\payara7`, fuera del repositorio, y crear un dominio local `gr09-payara`. Un script PowerShell versionado configura y opera ese dominio; el servicio conserva el contexto `/04.SERVIDOR` y el cliente conserva una URL SOAP configurable.

**Tech Stack:** Windows PowerShell 5.1, Azul Payara Community Server 7.2026.10, Temurin JDK 25.0.4+101 en NetBeans/compilación/runtime con bytecode Java 21, Maven 3.10.0 vía `mise`, Jakarta EE 11.

**Spec:** `docs/superpowers/specs/2026-10-21-payara-gr09-migration-design.md`

## Global Constraints

- Alcance: solo los módulos activos Java web SOAP y REST del grupo GR09.
- Usar JDK 25 en NetBeans, compilación, pruebas y runtime; conservar `release`/bytecode Java 21 en los módulos.
- Mantener SOAP en `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09` al terminar el corte.
- La etapa SOAP usa temporalmente el puerto HTTP `18080`, sujeto a comprobación de disponibilidad.
- Conservar el soporte de URL por `ws.base.url` y `WS_BASE_URL`.
- No cambiar contratos, operaciones, nombres de carpeta numerada ni lógica de conversión.
- No tocar copias GR03, GR05 o GR69 ni aplicaciones .NET.
- No instalar runtimes en el PATH global; guardar Payara, JDK, Maven y repositorio de dependencias bajo `..\tools`.

## Review Focus

- Conflicto del puerto de preparación `18080` o de administración `14848`: comprobar ambos antes de crear o iniciar el dominio.
- Contexto SOAP distinto de `/04.SERVIDOR`: consultar el WSDL exacto en preparación y después en `19080`.
- El cliente SOAP apunta por defecto a `19080` durante la prueba escalonada: fijar `WS_BASE_URL` a la URL de preparación y verificar login y conversión.
- La vista web SOAP responde 404 por el contexto o página de inicio: comprobar `/soap-web/inicio.jsp` y `/soap-web/conversor.jsp`.
- URL de preparación filtrada al corte: `Start` compara explícitamente el modo y asigna `WS_BASE_URL` a `18080` en preparación y a `19080` en modo final; verificar login y conversión tras reiniciar Payara.

---

## Archivos y responsabilidades

- `scripts/payara-gr09.ps1` — configura y opera el dominio ya instalado, sus listeners y despliegues, inicio, estado y detención.
- `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WSConversionUnidades_Java_SOAP_GR09/src/main/webapp/WEB-INF/payara-web.xml` — define el contexto Payara del servicio SOAP.
- `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/README.txt` y `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/04. CLIWEB/README.txt` — explican el runtime activo y el endpoint del cliente web.
- La descarga `..\tools\payara-7.2026.10.zip`, el runtime y el dominio local quedan fuera del repositorio.

## Task 1: Preparar el runtime Payara local y su operador

**Files:**
- Create: `scripts/payara-gr09.ps1`
- Runtime local: `..\tools\payara-7.2026.10.zip` y `..\tools\payara7` (no se versionan)

**Interfaces:**
- `-Action ConfigureStage` crea el dominio `gr09-payara` con HTTP en `18080` y administración en `14848`.
- `-Action Start` inicia el dominio.
- `-Action Deploy -Target Soap` despliega el WAR SOAP y el WAR del cliente web SOAP en `/04.SERVIDOR` y `/soap-web`.
- `-Action Status` informa versión Payara, estado del dominio, puertos y aplicaciones.
- `-Action Stop` detiene solo `gr09-payara`.
- `-Action ConfigureFinal` reserva `8080` para el listener principal y crea el listener SOAP `19080`; se ejecuta solo en el corte final.

- [x] **Step 1: Verificar los prerrequisitos locales.** Comprobar Temurin JDK 25.0.4+101 y Maven 3.10.0; iniciar NetBeans con el acceso directo personal `Apache NetBeans JDK 25` (`--jdkhome`); confirmar que `18080` y `14848` estén libres. Si falta JDK o Maven, instalarlos con `mise` bajo `..\tools\mise`; no tocar PATH persistente.
- [x] **Step 2: Obtener Azul Payara Community Server 7.2026.10.** Descargar el ZIP oficial a `..\tools\payara-7.2026.10.zip`, extraerlo como `..\tools\payara7` y comprobar `asadmin version`.
- [x] **Step 3: Crear `scripts/payara-gr09.ps1`.** Implementar las acciones e interfaces exactas de arriba con rutas absolutizadas desde `$PSScriptRoot`, validación de puertos y errores no-cero ante CLI faltante, dominio ausente o comando fallido.
- [x] **Step 4: Ejecutar `ConfigureStage`, `Start` y `Status`.** Esperado: el proceso informa Payara Server 7.2026.10, dominio `gr09-payara` activo en `18080`, sin puertos ocupados por el dominio anterior.
- [x] **Step 5: Revisar configuración generada.** Esperado: el listener HTTP se enlaza a `127.0.0.1`/localhost y el puerto administrativo no se expone en interfaces externas.

## Task 2: Migrar el servicio SOAP y el cliente web SOAP

**Files:**
- Delete: `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WSConversionUnidades_Java_SOAP_GR09/src/main/webapp/WEB-INF/glassfish-web.xml`
- Create: `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/04. SERVIDOR/WSConversionUnidades_Java_SOAP_GR09/src/main/webapp/WEB-INF/payara-web.xml`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/README.txt`
- Modify: `U1/TI.1.1/TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09/04. CLIWEB/README.txt`

**Interfaces:**
- WAR SOAP: `WSConversionUnidades_Java_SOAP_GR09.war`, contexto `/04.SERVIDOR`.
- WAR de presentación: `Cliente_Web.war`, contexto `/soap-web`.
- El endpoint configurable sigue usando `ws.base.url` y `WS_BASE_URL`.

- [x] **Step 1: Escribir `payara-web.xml`.** Declarar `<payara-web-app><context-root>/04.SERVIDOR</context-root></payara-web-app>` y retirar el descriptor de GlassFish.
- [x] **Step 2: Actualizar la documentación SOAP activa.** Sustituir instrucciones de GlassFish por el script Payara, los módulos que se construyen y las URL locales definitivas.
- [x] **Step 3: Construir los módulos SOAP.** Desde cada carpeta con `pom.xml`, ejecutar `mvn test package` con JDK 25/Maven local y release Java 21. Esperado: se generan los WAR de servicio y cliente web; las pruebas existentes pasan.
- [x] **Step 4: Desplegar el par SOAP en preparación.** Ejecutar `scripts/payara-gr09.ps1 -Action Deploy -Target Soap`; confirmar las aplicaciones con `Status`.
- [x] **Step 5: Comprobar SOAP y sus páginas.** Consultar WSDL y páginas bajo `http://127.0.0.1:18080`; con `WS_BASE_URL` a ese endpoint, completar login y una conversión SOAP. Esperado: respuestas correctas sin errores de despliegue en el log Payara.
- [x] **Step 6: Revisar el alcance del cambio.** `git status --short` y búsqueda de `glassfish|GlassFish` en las carpetas activas SOAP. Esperado: solo el nombre histórico del archivo borrado en el diff; ninguna instantánea antigua modificada.
