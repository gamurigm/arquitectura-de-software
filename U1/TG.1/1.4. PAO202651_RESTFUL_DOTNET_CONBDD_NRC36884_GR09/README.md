# EurekaBank REST .NET

API ASP.NET Core 10 conectada a la base de datos SQL Server existente, con clientes de consola, escritorio WPF, Android MAUI y web ASP.NET MVC. Las aplicaciones cliente consumen la API; solo el servidor accede a SQL Server.

Configure localmente EUREKABANK_DB_CONNECTION_STRING para el servidor y EUREKABANK_REST_DOTNET_URL para los clientes. La API usa http://localhost:5081/; MAUI usa http://10.0.2.2:5081/ en el emulador Android. No guarde cadenas de conexión ni contraseñas en el repositorio.

`run-api.ps1` carga la configuración local y arranca la API. La base de datos ya debe existir; el servidor no crea ni inicializa tablas o datos.

Las credenciales existentes usan hashes pbkdf2-sha256 con 600 000 iteraciones, 16 bytes de sal y 32 bytes de salida. El inicio de sesión responde autenticado; los depósitos responden registrado; los movimientos contienen numero, cuenta, fecha ISO 8601, tipo, accion e importe. No se emiten tokens.

El contrato expone `POST /api/eureka/login` con `usuario` y `contrasena`, `GET /api/eureka/movimientos?cuenta=...` y `POST /api/eureka/depositos` con `cuenta` e `importe`. El importe debe ser positivo y tener como máximo dos decimales. Las aplicaciones mantienen el inicio de sesión local solo después de una respuesta autenticada y limpian ese estado al cerrar sesión.

Antes de abrir un cliente desde PowerShell, ejecute `CargarVariables.ps1` en esa misma terminal para cargar la configuración local común.
