# Conversión de unidades por SOAP — GR09

El servicio SOAP y la interfaz web se ejecutan en **Payara Server Community 7**. El contrato, las operaciones y los clientes existentes se mantienen.

## URLs

- WSDL: `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09?wsdl`
- Servicio SOAP: `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09`
- Interfaz web: `http://localhost:19080/soap-web/inicio.jsp`
- Conversor web: `http://localhost:19080/soap-web/conversor.jsp`
- Android Emulator: `http://10.0.2.2:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09`

La interfaz web puede usar otro endpoint mediante la propiedad Java `ws.base.url` o la variable `WS_BASE_URL`.

## Compilar y operar Payara

Usa JDK 25 para NetBeans, compilación y runtime; los módulos conservan bytecode Java 21. Desde la raíz del repositorio, compila el servicio y el cliente web:

```powershell
$repoRoot = (& git rev-parse --show-toplevel).Trim()
$toolsDir = Join-Path (Split-Path $repoRoot -Parent) 'tools'
$env:JAVA_HOME = Join-Path $toolsDir 'mise\installs\java\temurin-25.0.4+101.0.LTS'
$mvn = Join-Path $toolsDir 'mise\installs\maven\3.10.0\apache-maven-3.10.0\bin\mvn.cmd'
$mavenRepo = Join-Path $toolsDir 'm2'
Set-Location (Join-Path $repoRoot 'U1\TI.1.1\TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09\04. SERVIDOR\WSConversionUnidades_Java_SOAP_GR09')
& $mvn "-Dmaven.repo.local=$mavenRepo" test package
Set-Location (Join-Path $repoRoot 'U1\TI.1.1\TI.1.1. PAO202651_SOAP_JAVA_SINBDD_NRC36884_GR09\04. CLIWEB\Cliente_Web')
& $mvn "-Dmaven.repo.local=$mavenRepo" test package
Set-Location $repoRoot
```

El operador `scripts\payara-gr09.ps1` configura, inicia, consulta y detiene el dominio local `gr09-payara`. Para la validación temporal usa el puerto `18080`; en operación estable SOAP y las dos interfaces web quedan en `19080`.

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action ConfigureStage
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action Start
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action Deploy -Target Soap
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action Status
```

El endpoint predeterminado y el contexto `/04.SERVIDOR` se conservan para los clientes SOAP de GR09.
