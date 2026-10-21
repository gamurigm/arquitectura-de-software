# Cliente web SOAP — GR09

`Cliente_Web` es una interfaz JSP/Servlet empaquetada como WAR. Payara sirve la aplicación en el contexto `/soap-web`.

## Compilar

Usa JDK 25 con bytecode Java 21. Desde esta carpeta ejecuta:

```powershell
$repoRoot = (& git rev-parse --show-toplevel).Trim()
$toolsDir = Join-Path (Split-Path $repoRoot -Parent) 'tools'
$env:JAVA_HOME = Join-Path $toolsDir 'mise\installs\java\temurin-25.0.4+101.0.LTS'
$mvn = Join-Path $toolsDir 'mise\installs\maven\3.10.0\apache-maven-3.10.0\bin\mvn.cmd'
& $mvn "-Dmaven.repo.local=$(Join-Path $toolsDir 'm2')" test package
```

El WAR generado es `Cliente_Web/target/Cliente_Web.war`.

## URLs en Payara

- Inicio: `http://localhost:19080/soap-web/inicio.jsp`
- Conversor: `http://localhost:19080/soap-web/conversor.jsp`
- Servicio SOAP predeterminado: `http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09`

El cliente acepta `-Dws.base.url=<URL>` o `WS_BASE_URL` para cambiar la dirección SOAP. El usuario y la contraseña se envían al servicio para iniciar sesión; las operaciones protegidas usan el token SOAP de la sesión.

El servicio y la interfaz web se despliegan en el dominio `gr09-payara` con `scripts\payara-gr09.ps1 -Action Deploy -Target Soap` desde la raíz del repositorio.
