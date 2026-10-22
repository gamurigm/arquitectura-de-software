# Mi música — reproductor de Spotify

Aplicación local con Fastify para escuchar una playlist o un álbum mediante el reproductor oficial embebido de Spotify.

## Requisitos

- Node.js 24 o superior.
- npm.

## Iniciar

En PowerShell, desde la raíz del repositorio:

```powershell
Set-Location .\fastify-app
npm install
npm start
```

Abre <http://127.0.0.1:3000> en el navegador y pega un enlace web de una playlist o un álbum de Spotify.

Para usar otro puerto en PowerShell:

```powershell
$env:PORT = "3001"
npm start
```

## Enlaces compatibles

La página acepta enlaces HTTPS completos de `open.spotify.com` con una ruta `/playlist/{id}` o `/album/{id}`. Los parámetros de consulta y fragmentos del enlace se ignoran. No acepta enlaces HTTP, enlaces cortos, URI `spotify:`, canciones sueltas ni artistas.

La página se limita a cargar el reproductor oficial de Spotify. Spotify controla la reproducción, los requisitos de inicio de sesión y la disponibilidad regional. Esta aplicación no se conecta a tu cuenta ni puede mostrar tu biblioteca privada.

El servidor escucha en `127.0.0.1` y requiere conexión a internet para cargar el reproductor de Spotify.
