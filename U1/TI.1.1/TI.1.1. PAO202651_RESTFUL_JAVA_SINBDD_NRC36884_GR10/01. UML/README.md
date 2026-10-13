# 01. UML

Artefactos de análisis y diseño del proyecto **Conversión de Unidades Monster (RESTful Java, sin BD)**.

## Contenido

- `01. ERS` - Especificación de Requisitos de Software (`ERS_RESTFUL_JAVA_SINBDD_NRC36884_GR10_PLANTILLA.docx`).
- `02. ECUD` - Especificación de Casos de Uso Detallados.
- `03. DIAGRAMAS UML` - Diagramas del sistema:
  - Diagramas de casos de uso
  - Diagrama de clases
  - Diagrama de secuencia
  - Diagrama de componentes
  - Diagrama de despliegue

## Actores

- **Usuario** - Persona que usa cualquiera de los clientes (consola, escritorio, web, móvil).
- **Servidor REST** - Sistema que expone la API de conversión y valida el token.

## Flujo general

1. El usuario se autentica en `/api/auth/login` y obtiene un token.
2. El usuario solicita una conversión (`/api/conversiones/{longitud|masa|temperatura}`) enviando el token.
3. El servidor responde con el resultado en JSON o con un error con su código HTTP.
