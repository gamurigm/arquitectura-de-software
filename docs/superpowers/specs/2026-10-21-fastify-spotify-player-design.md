# Diseño: reproductor local de Spotify con Fastify

**Estado:** aprobado para revisión de especificación
**Fecha:** 2026-10-21

## Objetivo

Crear un proyecto Node.js pequeño y ejecutable que permita pegar el enlace web de una playlist o álbum de Spotify y escuchar su contenido mediante el reproductor oficial de Spotify embebido.

## Contexto

- El repositorio actual reúne proyectos académicos Java y .NET y no tiene un proyecto Node.js en su raíz.
- El proyecto nuevo se aislará en `fastify-app/`.
- El usuario quiere escuchar música desde Spotify y eligió pegar un enlace de playlist o álbum en el reproductor oficial.
- Node.js 24.22.0 y npm 11.19.1 están disponibles en Windows.
- npm publica Fastify 5.12.7 y `@fastify/static` 10.1.7 al redactar esta especificación. La tabla del plugin indica que la serie 10.x es compatible con Fastify 5.

## Alcance

El proyecto permitirá:

1. Iniciar un servidor local con `npm start`.
2. Abrir una página con un campo para pegar un enlace de Spotify.
3. Aceptar enlaces HTTPS estándar de `open.spotify.com` cuyo recurso sea una playlist o un álbum.
4. Validar host, tipo de recurso e identificador antes de construir la URL del reproductor.
5. Mostrar el reproductor oficial en un iframe adaptable al ancho de la página.
6. Mostrar un mensaje accesible cuando el enlace no sea compatible.

El código nuevo vivirá en `fastify-app/`: `package.json`, `package-lock.json`, `.gitignore`, `server.js`, `public/index.html`, `public/app.js`, `public/styles.css` y `README.md`.

## Diseño

### Servidor

- Usar Fastify como servidor HTTP y `@fastify/static` para servir `public/`.
- Mantener el paquete en modo ES modules y definir el script `npm start`.
- Escuchar en `127.0.0.1`, con puerto `3000` predeterminado y opción de cambiarlo con `PORT`.
- No crear dependencias ni manifiestos npm en la raíz del repositorio.

### Página y flujo de reproducción

1. El usuario abre `http://127.0.0.1:3000` y pega un enlace de playlist o álbum.
2. La página comprueba primero la estructura del enlace original para impedir que la normalización de URL acepte segmentos de ruta extra; después analiza la URL con el constructor `URL` y comprueba que el protocolo sea HTTPS y el host exactamente `open.spotify.com`.
3. La página acepta solo rutas `/playlist/{id}` y `/album/{id}` con identificador alfanumérico. Los parámetros de consulta y fragmentos no se reutilizan.
4. La página construye una URL fija bajo `https://open.spotify.com/embed/` y la asigna al iframe. Nunca inserta el enlace proporcionado como HTML.
5. Spotify presenta sus propios controles y gestiona la sesión, disponibilidad y reproducción.

### Seguridad y privacidad

- El servidor escucha solo en loopback por defecto.
- El navegador no acepta protocolos distintos de HTTPS, hosts arbitrarios ni rutas que no sean playlist o álbum.
- No se usan OAuth, claves de Spotify, tokens, almacenamiento persistente ni una base de datos.
- El proyecto no consulta la biblioteca privada ni modifica la cuenta de Spotify.
- La conexión al reproductor se realiza directamente desde el navegador a Spotify; la página requiere internet para mostrarlo.

### Errores y límites

- Un enlace mal formado, de otro dominio o de un tipo no permitido deja el reproductor actual intacto y muestra un mensaje comprensible.
- Spotify decide si solicita inicio de sesión y qué reproducción está disponible según la cuenta, el contenido y la región. El proyecto no descarga ni retransmite audio.
- Solo se aceptan enlaces HTTPS completos `open.spotify.com`; no se resolverán enlaces HTTP, enlaces cortos `spotify.link`, URIs `spotify:`, canciones sueltas ni enlaces a artistas.

## Fuera de alcance

- Autenticarse con Spotify o enumerar canciones guardadas, playlists privadas o listas “Me gusta”.
- Buscar música mediante Spotify Web API.
- Implementar un reproductor de audio propio, descargar audio o transmitir desde el servidor.
- Controlar el reproductor mediante el Web Playback SDK.
- Publicar el servicio para otros usuarios o desplegarlo en internet.

## Criterios de aceptación

- `fastify-app/` contiene un proyecto npm aislado con Fastify y una página estática.
- `npm start` inicia el servidor local en el puerto predeterminado o en el valor de `PORT`.
- La raíz del servidor entrega la página y sus recursos estáticos.
- Un enlace válido de playlist o álbum genera un iframe del dominio oficial de Spotify.
- Enlaces no HTTPS, malformados, de hosts externos, con segmentos extra o de tipos no permitidos muestran un error accesible y no se insertan en el DOM como HTML.
- La documentación explica cómo instalar dependencias, iniciar el servidor, pegar el enlace y las limitaciones del reproductor.
- No se modifican los proyectos Java/.NET ni los cambios preexistentes del usuario.

## Verificación prevista

- Instalar dependencias en `fastify-app/` y confirmar que npm resuelve Fastify y `@fastify/static`.
- Comprobar sintaxis de los archivos JavaScript y consultar que el manifiesto no introduzca dependencias incompatibles.
- No se agregará una suite de pruebas en este alcance.

## Referencias

- [Fastify: instalación](https://github.com/fastify/fastify#install)
- [@fastify/static: instalación y compatibilidad](https://github.com/fastify/fastify-static#compatibility)
- [Spotify Embeds](https://developer.spotify.com/documentation/embeds)
- [Spotify: creación de un Embed](https://developer.spotify.com/documentation/embeds/tutorials/creating-an-embed)
- [Spotify Web Playback SDK: requisitos de autenticación](https://developer.spotify.com/documentation/web-playback-sdk/tutorials/getting-started)
