# 03. BDD

Base de datos.

## Nota importante

Este proyecto es **SIN base de datos (sin BDD)**. No se crea ningún esquema, script ni conexión
JDBC. El estado se mantiene en memoria:

- Credenciales en `application.properties` (`Monster` / `Monster9`).
- Tokens activos en un `AlmacenTokens` en memoria.
- Factores de conversión definidos en el código (`CatalogoUnidades`).

Por lo tanto, esta carpeta se conserva únicamente por consistencia de la estructura del proyecto.
