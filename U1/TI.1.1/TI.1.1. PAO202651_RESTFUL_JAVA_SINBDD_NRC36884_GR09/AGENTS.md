# Instrucciones del proyecto GR09

## Alcance y estructura

- Este proyecto implementa conversión de unidades con REST y Java. El contrato del servidor REST define las operaciones y los modelos de datos.
- Conserva las carpetas numeradas y sus nombres actuales. No reordenes ni renombres esa estructura.
- Mantén código, documentación, mensajes y explicaciones en español. Conserva literalmente nombres exigidos por Java, Spring, HTTP y JSON.
- Trabaja un módulo a la vez. Ejecuta Maven desde cada módulo que contiene su `pom.xml`; ejecuta Android desde Android Studio o desde su configuración Gradle local.
- El proyecto no usa base de datos ni Docker. No inicies el servidor cuando el usuario indique que lo ejecutará manualmente.

## Patrón arquitectónico

- Todos los clientes Java siguen MVC explícito: modelo, vista, controlador y servicio HTTP. Mantén cada responsabilidad en su paquete y documenta esa separación.
- El servidor organiza controladores REST, servicios, modelos/DTO y seguridad. Los controladores reciben HTTP; los servicios autentican, validan y ejecutan conversiones.
- No describas como implementado un paquete, endpoint o validación que no exista en el código.

## Contrato REST

- El servidor escucha en `http://localhost:8080` por defecto. Consulta `application.properties` antes de cambiar configuración.
- `GET /api/salud`, `POST /api/auth/login` y `GET /api/catalogo` son públicos. Las conversiones y `POST /api/auth/logout` requieren `Authorization: Bearer <token>`.
- Credenciales iniciales: `monster` / `monster9`. El token vive 10 minutos en memoria y desaparece al reiniciar el servidor.
- Unidades, límites y respuestas del servidor son autoridad. Antes de cambiar una conversión, revisa catálogo, enums y validadores.

## Cambios y verificación

- Antes de editar, revisa estado Git y conserva cambios locales ajenos a la tarea.
- Limita cada cambio al módulo y documentos necesarios. No edites artefactos generados.
- Verifica compilación o ejecución solo cuando la tarea lo pida o cuando sea necesaria para confirmar el cambio; comunica límites si el servidor no está disponible.
