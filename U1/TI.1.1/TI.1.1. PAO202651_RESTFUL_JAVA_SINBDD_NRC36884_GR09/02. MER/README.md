# 02. MER

Modelo Entidad-Relación del proyecto.

## Nota importante

El proyecto es **SIN base de datos**. Los datos (credenciales, tokens y factores de conversión)
se mantienen **en memoria** dentro del servidor. Por ello los diagramas del MER son
**conceptuales / de referencia** y no se despliegan en un gestor de base de datos.

## Contenido

- `01. CONCEPTUAL` - Diagrama entidad-relación conceptual.
- `02. LOGICO` - Modelo lógico (entidades, atributos y relaciones).
- `03. FISICO` - Modelo físico (referencial; no se implementa).

## Entidades de referencia

- **Usuario** (`usuario`, `clave`)
- **Token** (`valor`, `usuario`, `fechaExpiracion`)
- **Unidad** (`nombre`, `magnitud`)
- **ConversionLog** (`valor`, `unidadOrigen`, `unidadDestino`, `resultado`) - solo referencia
