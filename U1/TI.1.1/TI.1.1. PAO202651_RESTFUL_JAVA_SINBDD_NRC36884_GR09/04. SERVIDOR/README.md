# 04. SERVIDOR

Servidor REST del sistema **Conversión de Unidades Monster**.

## Proyecto

`WS_ConUni_Java_REST` - Aplicación **Spring Boot 3.5.x** compilada con JDK 25 (bytecode Java 21), empaquetada como WAR y desplegada en Payara Server Community 7. **Sin base de datos** (estado en memoria).

Ver `WS_ConUni_Java_REST/README.md` para el detalle técnico y de endpoints.

## Compilar y desplegar

```powershell
$repoRoot = (& git rev-parse --show-toplevel).Trim()
$toolsDir = Join-Path (Split-Path $repoRoot -Parent) 'tools'
$env:JAVA_HOME = Join-Path $toolsDir 'mise\installs\java\temurin-25.0.4+101.0.LTS'
$mvn = Join-Path $toolsDir 'mise\installs\maven\3.10.0\apache-maven-3.10.0\bin\mvn.cmd'
Set-Location (Join-Path $repoRoot 'U1\TI.1.1\TI.1.1. PAO202651_RESTFUL_JAVA_SINBDD_NRC36884_GR09\04. SERVIDOR\WS_ConUni_Java_REST')
& $mvn "-Dmaven.repo.local=$(Join-Path $toolsDir 'm2')" test package
```

La raíz del repositorio incluye `scripts/payara-gr09.ps1`, que despliega la API en el contexto raíz `/`. La URL estable es `http://localhost:8080`; durante la validación escalonada se usa `http://localhost:18080`.

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action Deploy -Target Rest
powershell -ExecutionPolicy Bypass -File .\scripts\payara-gr09.ps1 -Action Status
```

Para recuperación local, el WAR de Spring Boot conserva el lanzador `java -jar`; Payara es el runtime del servicio web desplegado.

## Pruebas

```bash
mvn test
```

Incluye pruebas de integración de la API y pruebas unitarias del servicio de conversión.
