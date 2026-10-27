# EurekaBank SOAP .NET

Sistema bancario SOAP con servidor CoreWCF, SQL Server 2025 y clientes de consola, escritorio WPF, móvil .NET MAUI y web ASP.NET MVC. Los clientes solo consumen SOAP; solo el servidor accede a la base de datos.

## Configuración

1. Use la base SQL Server existente y compartida por los servicios bancarios .NET.
2. Defina `EUREKABANK_DB_CONNECTION_STRING` en el `.env` común de `U1/TG.1`, apuntando a esa base. Mantenga credenciales reales solo en ese archivo local, ignorado por Git.
3. El formato recomendado de cadena es `Server=localhost,1433;Database=<base-compartida>;User ID=<usuario>;Password=<contraseña>;Encrypt=True;TrustServerCertificate=True;`. Use una cuenta SQL limitada a la base y los permisos de lectura, actualización e inserción requeridos.
4. Para la prueba local, ejecute una vez `03. BDD/Cargar_Datos_SQLServer.sql` sobre la base vacía. Carga los datos de ejemplo y vincula `Monster` al cliente `00005`, con cuentas `00100001` y `00100002`. Para otros usuarios, genere el hash con `04. SERVIDOR/crear-hash.ps1` y asocie el cliente correcto.

El servicio queda en `http://localhost:5080/WSEureka`; su WSDL se publica en `http://localhost:5080/WSEureka?wsdl`. Para clientes locales, `EUREKABANK_SOAP_DOTNET_URL` cambia el endpoint. El cliente Android usa `http://10.0.2.2:5080/WSEureka` por defecto; su pantalla permite editarlo.

## Ejecución

Ejecute el servidor con `04. SERVIDOR/run.ps1`. Luego inicie cualquiera de estos clientes desde PowerShell:

- `04. CLICON/run.ps1`
- `04. CLIESC/run.ps1`
- `04. CLIMOV/run.ps1`
- `04. CLIWEB/run.ps1`

Cada cliente inicia una sesión local solo cuando `login` devuelve `1`. Después permite consultar movimientos, calcular saldo como ingresos menos salidas, depositar y cerrar sesión local. Los importes usan `decimal` y escala de dos decimales.

## Contrato y persistencia

SOAP conserva `login(usuario, contrasena)`, `traerMovimientos(cuenta)` y `regDeposito(cuenta, importe)`. `traerMovimientos` devuelve cuenta, número, fecha, tipo, acción e importe. Los depósitos actualizan cuenta y movimiento dentro de una transacción serializable con `commit` o `rollback`.

Solo `EurekaBank.SoapServer` accede a la base de datos con `Microsoft.Data.SqlClient`; su singleton lee `EUREKABANK_DB_CONNECTION_STRING` y crea una conexión por operación. Conexiones, comandos, lectores y transacciones se disponen siempre.

## Compilación

Use .NET 10. Para compilar MAUI Android, instale la carga de trabajo `maui-android` y la plataforma Android SDK 36.

```powershell
dotnet build "04. SERVIDOR/EurekaBank.SoapServer/EurekaBank.SoapServer.csproj"
dotnet build "04. CLICON/EurekaBank.Consola/EurekaBank.Consola.csproj"
dotnet build "04. CLIESC/EurekaBank.Wpf/EurekaBank.Wpf.csproj"
dotnet build "04. CLIMOV/EurekaBank.Maui/EurekaBank.Maui.csproj"
dotnet build "04. CLIWEB/EurekaBank.Web/EurekaBank.Web.csproj"
```
