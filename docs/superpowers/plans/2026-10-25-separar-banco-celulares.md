# Separar banco y celulares Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Dejar el servicio y los datos bancarios en el proyecto del banco, con la comercializadora consumiéndolos por REST.

**Architecture:** BanQuito.Api, dominio, contratos y scripts pasan al proyecto bancario REST .NET. El proyecto celular conserva una API de comercializadora y DTOs propios; no compila ni accede a código o tablas bancarias.

**Tech Stack:** .NET 10, C# 14, ASP.NET Core, SQL Server 2025.

**Spec:** `docs/superpowers/specs/2026-10-25-separar-banco-celulares-design.md`

## Global Constraints

- Conservar la API y la base EurekaBank existentes.
- No eliminar ni recrear las bases locales BanQuitoCore o ComercializadoraCelulares.
- Mantener la conexión entre proyectos por HTTP y no mediante ProjectReference.
- No correr pruebas en esta corrección.

## Review Focus

- Referencias de proyecto que vuelvan a acoplar la tienda al dominio del banco: revisar los `.csproj` y compilar ambas APIs.
- Scripts de tienda que creen o siembren la base bancaria: inspeccionar ambos scripts de cada proyecto.
- Documentación que describa dos APIs o dos bases como propiedad del proyecto celular: buscar menciones restantes.
- Rutas relativas rotas tras mover los proyectos bancarios: compilar API, dominio e inicializador bancarios.
- Eliminación accidental de cambios locales o del DOCX temporal sin seguimiento: revisar `git status` y excluirlo de movimientos.

---

### Task 1: Reubicar la implementación bancaria

**Files:**
- Move: API, dominio, inicializador y `Pruebas` desde `U1/TI.1.1/...CELULAR.../04. SERVIDOR` a `U1/TG.1/1.4. ...RESTFUL_DOTNET.../01. SERVIDOR`.
- Move: `CasosUsoCredito.md` y el modelo de datos bancario a la documentación del proyecto del banco.
- Create: `BanQuito.Contratos` independiente del proyecto celular.
- Modify: referencias `.csproj` del código trasladado para usar contratos BanQuito locales.

**Check:** buscar `BanQuito.Dominio` y `ReglasCredito` en el árbol celular; solo deben quedar DTOs remotos y documentación de integración.

### Task 2: Separar persistencia e inicialización

**Files:**
- Modify: scripts de `03. BDD` del proyecto celular para crear y sembrar solo ComercializadoraCelulares.
- Create: scripts BanQuito en `03. BDD` del proyecto bancario.
- Move/copy: inicializador para que cada proyecto aplique únicamente sus scripts y use una variable de conexión propia.

**Check:** revisar nombres de bases en cada script y compilar ambos inicializadores.

### Task 3: Eliminar dependencias bancarias de la comercializadora

**Files:**
- Modify: `Celulares.Contratos/Modelos.cs` para conservar DTOs de tienda y respuestas HTTP BanQuito, sin modelos internos del banco.
- Create: `Comercializadora.Dominio/ReglasFacturacion.cs`.
- Modify: `Comercializadora.Api.csproj`, `ServicioFacturas.cs` y configuración para eliminar la referencia al dominio bancario y mantener el cliente REST.

**Check:** compilar la API celular y confirmar que el banco solo aparece como URL/cliente HTTP o en referencias documentales.

### Task 4: Corregir documentación de las dos fronteras

**Files:** README, arquitectura, plan, avance, manual técnico, MER y README de BDD en ambos proyectos.

**Check:** buscar `BanQuito.Api`, `BanQuitoCore` y referencias a APIs en el proyecto celular y comprobar que indiquen dependencia externa.

### Task 5: Verificar compilación y estado

**Check:** compilar los proyectos BanQuito y Comercializadora con salidas bajo `C:\Users\GABI\Documents\ESPE VIII SII2026\tools`; ejecutar `git diff --check` y revisar `git status --short`. No ejecutar pruebas.
