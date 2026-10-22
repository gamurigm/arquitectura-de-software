# Instrucciones locales — EurekaBank Grupo 1

## Plataforma de servicio

- El servicio SOAP y el WAR del cliente web se preparan para Payara Server Community 7.
- Los clientes de consola, escritorio, Android y web funcionan en su propio entorno; consumen el servicio SOAP desplegado en Payara. No se describen como aplicaciones desplegadas dentro de Payara.
- El código web usa APIs `javax.servlet`; validar o migrar su compatibilidad con Payara 7 antes del despliegue. El cliente web contiene rutas SOAP directas y una ruta servlet/JDBC separada; conservar esa distinción en documentación.
- Documentar el estado que se observa en el código. No declarar aprobadas operaciones financieras, pruebas o despliegues que no se hayan ejecutado.

## ERS de clientes

Fuentes Markdown, plantilla local y generador están en `04. DOCUMENTACIÓN/01. ERS CLIENTES`:

- `ERS_CLIENTE_CONSOLA_EUREKABANK.md` y `.docx`
- `ERS_CLIENTE_ESCRITORIO_EUREKABANK.md` y `.docx`
- `ERS_CLIENTE_MOVIL_EUREKABANK.md` y `.docx`
- `ERS_CLIENTE_WEB_EUREKABANK.md` y `.docx`
- `PLANTILLA_ERS_EUREKABANK.docx`
- `generar_ers_desde_plantilla.ps1`

Para regenerar, usar Windows PowerShell 5.1 de 32 bits, mantener la plantilla local y proporcionar `-Force` solo para reemplazar la salida existente:

```powershell
$docs = '04. DOCUMENTACIÓN/01. ERS CLIENTES'
$ps51 = "$env:WINDIR\SysWOW64\WindowsPowerShell\v1.0\powershell.exe"
& $ps51 -NoProfile -ExecutionPolicy Bypass -File "$docs/generar_ers_desde_plantilla.ps1" `
  -MarkdownPath "$docs/ERS_CLIENTE_CONSOLA_EUREKABANK.md" `
  -OutputPath "$docs/ERS_CLIENTE_CONSOLA_EUREKABANK.docx" `
  -CoverTitle 'ESPECIFICACIÓN DE REQUERIMIENTOS PARA EUREKABANK CLIENTE DE CONSOLA' -Force
```

Repetir con `ESCRITORIO`, `MOVIL` o `WEB` en las rutas y adaptar el título. El generador usa Pandoc, la carátula de la plantilla, Word y campos de índice. En Markdown, dejar una línea vacía después de cada leyenda `: Tabla N. ...` o `: Ilustración N. ...` antes de la tabla o imagen para que Pandoc la reconozca como leyenda y el índice de Word la recoja. Revisar visualmente el DOCX después de generarlo.

## Diagramas UML con PowerDesigner 16

Modelos editables `.oom`, PNG y generadores están bajo `04. DOCUMENTACIÓN/03. DIAGRAMAS UML`:

- `01. CASOS DE USO`: casos de uso y sus actores, dependencias e inclusión. Generador `crear_modelos_casos_uso.ps1`.
- `02. ACTIVIDAD`: flujo principal observado en cada cliente.
- `03. SECUENCIA`: intercambio de solicitudes y respuestas entre cliente, adaptador y servicio SOAP.
- `04. CLASES`: clases/adaptadores representativos observados en cada cliente.
- `05. ARQUITECTURA`: vista de despliegue del cliente, Payara Server Community 7 y MySQL.
- `crear_uml_adicional.ps1`: actividad, secuencia, clases y despliegue a partir del modelo del cliente.

PowerDesigner 16 requiere Windows PowerShell de 32 bits para automatizar COM. Ejecutar desde la raíz del proyecto:

```powershell
$ps32 = "$env:WINDIR\SysWOW64\WindowsPowerShell\v1.0\powershell.exe"
$uml = '04. DOCUMENTACIÓN/03. DIAGRAMAS UML'
foreach ($cliente in 'consola','escritorio','movil','web') {
  & $ps32 -NoProfile -ExecutionPolicy Bypass -File "$uml/01. CASOS DE USO/crear_modelos_casos_uso.ps1" -Cliente $cliente -Force
  & $ps32 -NoProfile -ExecutionPolicy Bypass -File "$uml/crear_uml_adicional.ps1" -Cliente $cliente -Force
}
```

Abrir los `.oom` con PowerDesigner 16 para modificar sus objetos y diagramas. Revisar cada PNG exportado, especialmente el flujo de secuencia. Los diagramas representan el código cliente disponible: el proyecto Android tiene pantallas/adaptadores incompletos y el cliente web conserva dos rutas de depósito distintas.
