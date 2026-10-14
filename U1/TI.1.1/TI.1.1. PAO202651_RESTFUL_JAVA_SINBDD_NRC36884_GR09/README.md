# TI.1.1 PAO2026-51 - Conversión de Unidades (RESTful Java, sin BD)

Sistema distribuido **Monster** para conversión de unidades (Longitud, Masa y Temperatura).
Arquitectura **cliente-servidor REST** con autenticación por **token** y **sin base de datos**
(datos en memoria). Incluye un servidor y cuatro clientes.

## Estructura del repositorio

| Carpeta | Contenido |
| --- | --- |
| `01. UML` | ERS, casos de uso y diagramas UML |
| `02. MER` | Modelo Entidad-Relación (conceptual, lógico, físico) |
| `03. BDD` | Notas de base de datos (el proyecto es **sin BD**) |
| `04. SERVIDOR` | Servidor REST (**Spring Boot 3** + Java 21) |
| `04. CLICON` | Cliente de consola (Java SE + HttpClient) |
| `04. CLIESC` | Cliente de escritorio (Java Swing) |
| `04. CLIWEB` | Cliente web (JSP/Servlet - WAR) |
| `04. CLIMOV` | Cliente móvil (Android + Retrofit) |
| `05. DOCUMENTACION` | Diccionario de datos y manual técnico |

## Servidor REST

- **Stack:** Java 21, Spring Boot 3.5.x, Spring Web, Maven. Sin base de datos.
- **Puerto:** `8080` (contexto raíz `/`).
- **Credenciales:** `Monster` / `Monster9`. El token (UUID) vive en memoria 10 minutos.

### Endpoints

| Método | Ruta | Auth | Descripción |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | No | Devuelve el token |
| `POST` | `/api/auth/logout` | Bearer | Invalida el token |
| `GET` | `/api/catalogo` | No | Unidades por magnitud |
| `POST` | `/api/conversiones/longitud` | Bearer | Conversión de longitud |
| `POST` | `/api/conversiones/masa` | Bearer | Conversión de masa |
| `POST` | `/api/conversiones/temperatura` | Bearer | Conversión de temperatura |
| `GET` | `/api/salud` | No | Estado del servicio |

Documentación interactiva: `http://localhost:8080/swagger-ui.html`
Contrato OpenAPI: `http://localhost:8080/v3/api-docs`
Página del servidor: `http://localhost:8080/`

### Ejecutar el servidor

```bash
cd "04. SERVIDOR/WS_ConUni_Java_REST"
mvn clean package
java -jar target/WS_ConUni_Java_REST.jar
```

### Ejemplo de uso

```bash
# 1) Login
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"usuario":"Monster","clave":"Monster9"}'

# 2) Conversión (usar el token devuelto)
curl -X POST http://localhost:8080/api/conversiones/longitud \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>" \
     -d '{"valor":10,"unidadOrigen":"metros","unidadDestino":"pies"}'
```

## Unidades soportadas

- **Longitud:** `centimetros`, `metros`, `pies`, `yardas`, `millas`
- **Masa:** `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`
- **Temperatura:** `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`

## Clientes

Cada cliente consume el mismo servidor REST. Ver el `README.md` de cada carpeta (`04. CLICON`,
`04. CLIESC`, `04. CLIWEB`, `04. CLIMOV`) para instrucciones de compilación y ejecución.

## Requisitos generales

- JDK 21
- Maven 3.9+
- Para el cliente web: contenedor Jakarta EE 10 (Tomcat 10+, Payara, GlassFish)
- Para el cliente móvil: Android Studio (SDK 24+)
