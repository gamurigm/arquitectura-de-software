# WS_ConUni_Java_REST

Servidor REST (Spring Boot 3 + Java 21, **sin base de datos**) para conversión de unidades.
Primero se obtiene un **token** con el login y luego se usa en cada conversión mediante el header
`Authorization: Bearer <token>`.

## Requisitos

- JDK 21
- Maven 3.9+

## Compilar y ejecutar

```bash
mvn clean package
java -jar target/WS_ConUni_Java_REST.jar
```

- URL base: `http://localhost:8080`
- Pagina del servidor: `http://localhost:8080/`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Autenticacion

- Usuario: `monster`
- Clave: `monster9`
- El token (UUID) expira a los **30 minutos** (configurable en `application.properties`).

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
  -d '{"usuario":"monster","clave":"monster9"}'
```

Respuesta:

```json
{
  "exito": true,
  "token": "4777dca7a8b94c6e9a97f6c0a5e780da",
  "tipo": "Bearer",
  "usuario": "monster",
  "expiraEnSegundos": 1800,
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
