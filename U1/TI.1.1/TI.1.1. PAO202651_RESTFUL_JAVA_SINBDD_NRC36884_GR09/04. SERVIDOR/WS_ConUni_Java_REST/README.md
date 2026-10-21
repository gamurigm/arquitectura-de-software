# WS_ConUni_Java_REST

Servidor REST (Spring Boot 3.5.x, compilado con JDK 25 y bytecode Java 21, desplegado en Payara, **sin base de datos**) para conversión de unidades.
Primero se obtiene un **token** con el login y luego se usa en cada conversión mediante el header
`Authorization: Bearer <token>`.

## Requisitos

- JDK 25 para IDE, compilación y runtime
- Maven 3.10.0
- Payara Server Community 7

## Compilar y desplegar en Payara

```bash
$repoRoot = (& git rev-parse --show-toplevel).Trim()
$toolsDir = Join-Path (Split-Path $repoRoot -Parent) 'tools'
$env:JAVA_HOME = Join-Path $toolsDir 'mise\installs\java\temurin-25.0.4+101.0.LTS'
$mvn = Join-Path $toolsDir 'mise\installs\maven\3.10.0\apache-maven-3.10.0\bin\mvn.cmd'
& $mvn "-Dmaven.repo.local=$(Join-Path $toolsDir 'm2')" test package
```

El artefacto es `target/WS_ConUni_Java_REST.war`; se despliega en el contexto raíz `/` para conservar las rutas `/api/...`. Desde la raíz del repositorio, el operador local se consulta con `scripts/payara-gr09.ps1 -Action Status` y despliega con `scripts/payara-gr09.ps1 -Action Deploy -Target Rest`.

Para recuperación local, el WAR mantiene el lanzador ejecutable de Spring Boot (`java -jar target/WS_ConUni_Java_REST.war`). El despliegue web activo usa Payara.

La dependencia `spring-boot-starter-tomcat` tiene alcance `provided`: queda disponible para el lanzador y las pruebas del WAR, mientras que Payara atiende el despliegue web.

- URL base: `http://localhost:8080`
- Pagina del servidor: `http://localhost:8080/`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Autenticacion

- Usuario: `Monster`
- Clave: `Monster9`
- El token (UUID) expira a los **10 minutos** (configurable en `application.properties`).

## Endpoints

| Metodo | Ruta | Auth | Descripcion |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | No | Devuelve el token |
| `POST` | `/api/auth/logout` | Bearer | Invalida el token |
| `GET` | `/api/catalogo` | No | Unidades por magnitud |
| `POST` | `/api/conversiones/longitud` | Bearer | Conversion de longitud |
| `POST` | `/api/conversiones/masa` | Bearer | Conversion de masa |
| `POST` | `/api/conversiones/temperatura` | Bearer | Conversion de temperatura |
| `GET` | `/api/salud` | No | Estado del servicio |

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"Monster","clave":"Monster9"}'
```

Respuesta:

```json
{
  "exito": true,
  "token": "4777dca7a8b94c6e9a97f6c0a5e780da",
  "tipo": "Bearer",
  "usuario": "Monster",
  "expiraEnSegundos": 600,
  "mensaje": "Autenticacion exitosa."
}
```

### Conversion

```bash
curl -X POST http://localhost:8080/api/conversiones/longitud \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"valor":10,"unidadOrigen":"metros","unidadDestino":"pies"}'
```

Respuesta:

```json
{
  "exito": true,
  "magnitud": "longitud",
  "valor": 10.0,
  "unidadOrigen": "metros",
  "unidadDestino": "pies",
  "resultado": 32.808399,
  "mensaje": "10.0 metros equivalen a 32.808399 pies"
}
```

## Unidades

- **Longitud:** `centimetros`, `metros`, `pies`, `yardas`, `millas`
- **Masa:** `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`
- **Temperatura:** `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`

## Codigos de error

| HTTP | Descripcion |
| --- | --- |
| `400` | Unidad invalida o datos de entrada incorrectos |
| `401` | Token ausente, invalido o expirado; credenciales incorrectas |
| `404` | Ruta no encontrada |
| `500` | Error interno del servidor |

Formato de error:

```json
{
  "exito": false,
  "codigo": 401,
  "error": "No autorizado",
  "mensaje": "Token invalido o expirado.",
  "ruta": "/api/conversiones/longitud",
  "timestamp": "2026-10-13T08:30:00-05:00"
}
```

## Pruebas

```bash
mvn test
```
