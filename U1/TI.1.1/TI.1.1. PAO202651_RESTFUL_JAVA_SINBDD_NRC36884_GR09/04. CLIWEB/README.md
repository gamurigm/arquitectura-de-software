# 04. CLIWEB

Cliente web JSP/Servlet que consume la API REST GR09 mediante `HttpClient`. Payara sirve esta interfaz WAR en el contexto `/rest-web`.

## Compilar

Usa JDK 25 con bytecode Java 21. Desde la carpeta `Cliente_Web` ejecuta:

```powershell
$repoRoot = (& git rev-parse --show-toplevel).Trim()
$toolsDir = Join-Path (Split-Path $repoRoot -Parent) 'tools'
$env:JAVA_HOME = Join-Path $toolsDir 'mise\installs\java\temurin-25.0.4+101.0.LTS'
$mvn = Join-Path $toolsDir 'mise\installs\maven\3.10.0\apache-maven-3.10.0\bin\mvn.cmd'
& $mvn "-Dmaven.repo.local=$(Join-Path $toolsDir 'm2')" test package
```

El artefacto generado es `target/Cliente_Web.war`.

## URLs en Payara

- Inicio de sesión: `http://localhost:19080/rest-web/inicio.jsp`
- Backend REST: `http://localhost:8080/api`
- Salud: `http://localhost:8080/api/salud`

Durante la preparación del despliegue, el formulario de inicio permite indicar `http://localhost:18080` como dirección temporal de la API. En operación estable se usa `http://localhost:8080`.

## Despliegue

Desde la raíz del repositorio, usa el operador local:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action Deploy -Target Rest
```

La sesión web conserva el token en `HttpSession`; el backend guarda los tokens en memoria y los invalida al reiniciarse.
