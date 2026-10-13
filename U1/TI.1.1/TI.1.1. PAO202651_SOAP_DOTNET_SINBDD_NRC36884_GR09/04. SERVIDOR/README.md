# Servidor SOAP .NET GR09

Servidor SOAP 1.1 con CoreWCF 1.9.1 y .NET 10. Sigue MVC: `Controladores` publican el contrato, `Servicios` contienen conversiones, `Modelos` representan unidades y `Seguridad` administra credenciales y tokens.

## Iniciar manualmente en Windows

Ejecute `IniciarServidor.ps1` desde PowerShell. El script usa el SDK instalado en `D:\tools\dotnet10`, guarda paquetes y artefactos en `D:\tools` y mantiene el servidor en primer plano.

El servicio escucha en `http://127.0.0.1:19081/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09`. El WSDL está en la misma dirección con `?wsdl`.

Credenciales iniciales: `Monster` / `Monster9`. Las operaciones protegidas requieren el encabezado SOAP `token`, válido durante 15 minutos.
