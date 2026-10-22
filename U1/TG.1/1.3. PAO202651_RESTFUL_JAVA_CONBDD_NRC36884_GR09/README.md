# EurekaBank REST Java GR09

Servidor Jakarta EE 11 para Payara Community 7 y clientes Java consola, Swing, Android y Servlet/JSP. Los clientes solo consumen REST.

## Inicio local

1. Usa la base Java existente y compartida con el servicio SOAP.
2. Configura en el archivo `.env` común las variables de conexión a MySQL y Payara. El servidor las carga al iniciarse.
3. Desde PowerShell, ejecuta `./run-payara.ps1` en el servidor. API base: `http://127.0.0.1:8080/eurekabank-rest-java-gr09/api/eureka`.
4. Para cada cliente Maven, ejecuta `mvn -DskipTests package`. Si la API usa otra dirección, configura `EUREKABANK_REST_URL`; Android en emulador usa `10.0.2.2` y permite cambiarla con `-PEUREKABANK_REST_URL=...`.

El servidor usa las tablas y credenciales ya existentes. No crea ni cambia objetos o datos de la base.

## API

- `POST /api/eureka/login`: JSON `{ "usuario": "...", "contrasena": "..." }`; responde `{ "autenticado": true|false }`.
- `GET /api/eureka/movimientos?cuenta=...`: lista `{ numero, cuenta, fecha, tipo, accion, importe }`.
- `POST /api/eureka/depositos`: JSON `{ "cuenta": "...", "importe": 10.25 }`; responde `{ "registrado": true|false }`.

El depósito requiere un importe positivo de hasta dos decimales. El servidor no emite tokens ni conserva sesiones.
