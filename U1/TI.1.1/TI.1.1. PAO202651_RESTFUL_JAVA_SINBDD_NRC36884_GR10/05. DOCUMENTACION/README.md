# 05. DOCUMENTACION

Documentación del proyecto.

## Contenido

- `01. DICCIONARIO DE DATOS` - Descripción de los campos JSON (DTO) del servidor REST.
- `02. MANUAL TECNICO` - Manual técnico (arquitectura, despliegue, endpoints, errores).

## Diccionario de datos (resumen de DTO JSON)

### LoginRequest

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `usuario` | string | Usuario (`monster`) |
| `clave` | string | Clave (`monster9`) |

### TokenResponse

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `exito` | boolean | Resultado de la operación |
| `token` | string | Token de sesión (UUID) |
| `tipo` | string | Tipo de token (Bearer) |
| `usuario` | string | Usuario autenticado |
| `expiraEnSegundos` | number | Vigencia del token en segundos |
| `mensaje` | string | Mensaje descriptivo |

### ConversionRequest

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `valor` | number | Valor a convertir |
| `unidadOrigen` | string | Unidad de origen |
| `unidadDestino` | string | Unidad de destino |

### ConversionResponse

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `exito` | boolean | Resultado de la operación |
| `magnitud` | string | `longitud`, `masa` o `temperatura` |
| `valor` | number | Valor de entrada |
| `unidadOrigen` | string | Unidad de origen |
| `unidadDestino` | string | Unidad de destino |
| `resultado` | number | Resultado de la conversión |
| `mensaje` | string | Mensaje descriptivo |

### ErrorResponse

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `exito` | boolean | Siempre `false` |
| `codigo` | number | Código HTTP |
| `error` | string | Tipo de error |
| `mensaje` | string | Detalle del error |
| `ruta` | string | Ruta solicitada |
| `timestamp` | string | Fecha/hora del error |
