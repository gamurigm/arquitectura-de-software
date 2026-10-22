# EurekaBank SOAP Java GR09

- Este proyecto implementa operaciones bancarias EurekaBank con SOAP y Java. No es el conversor de unidades.
- Usa MySQL mediante JDBC. Incluye el controlador con Maven: `com.mysql:mysql-connector-j` en el `pom.xml` del servidor.
- Despliega el servidor WAR exclusivamente en Payara Server Community 7. Usa su configuración y sus herramientas oficiales.
- Mantén el servidor en `04. SERVIDOR` y los clientes en `04. CLICON`, `04. CLIESC`, `04. CLIMOV` y `04. CLIWEB`.
- Conserva `01. UML`, `02. MER`, `03. BDD` y `05. DOCUMENTACION`; los scripts MySQL viven en `03. BDD`.
- No guardes credenciales de base de datos en el código ni en Git.

## Crear ERS y casos de uso

- Mantén las fuentes editables Markdown y los DOCX en `01. UML/01. ERS`. El estilo se toma de `PLANTILLA_ERS_SOAP_JAVA_GR09.docx`, copia local de la plantilla original señalada por el usuario en el proyecto de referencia de `U1/TI.1.1`. Úsala como referencia de estilos, márgenes y encabezados; no reutilices su contenido funcional.
- Genera el ERS con el script local desde la raíz del proyecto. El script copia la plantilla completa, conserva su carátula, inserta el contenido EurekaBank después de ella y actualiza el índice en Word:

  ```powershell
  & "$env:WINDIR\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -ExecutionPolicy Bypass -File '.\01. UML\01. ERS\generar_ers_desde_plantilla.ps1' -Force
  ```

- Mantén el ERS completo con la organización del ejemplo SOAP sin base de datos: caso de estudio, introducción, propósito, alcance, definiciones, referencias del código, visión general, descripción del producto, interfaces, objetivos, actores, fichas RF y RNF, reglas de negocio, restricciones, atributos de calidad, rendimiento, trazabilidad, recomendaciones, conclusiones y bibliografía. Adapta cada sección a EurekaBank; distingue los mecanismos implementados y los aspectos pendientes sin declarar aprobaciones o mediciones inexistentes.
- Cada tabla lleva una leyenda `Tabla N. ...`; la imagen exportada de PowerDesigner lleva `Ilustración N. ...`. El generador convierte las leyendas en campos SEQ nativos y actualiza los índices de tablas e ilustraciones. Resuelve las imágenes relativas desde la carpeta del Markdown y evita dividir una fila de tabla entre páginas.

- Para regenerar el DOCX del taller desde la raíz del proyecto, usa Pandoc y la plantilla local:

  ```powershell
  Push-Location '.\01. UML\01. ERS'
  pandoc '.\TALLER_CASOS_DE_USO_EUREKABANK_SOAP_JAVA_GR09.md' --from markdown --to docx --standalone --number-sections --toc --toc-depth=3 --reference-doc '.\PLANTILLA_ERS_SOAP_JAVA_GR09.docx' --output '.\TALLER_CASOS_DE_USO_EUREKABANK_SOAP_JAVA_GR09.docx'
  Pop-Location
  ```

- Conserva el bloque OpenXML de salto de página después de `lang: es` en el Markdown del ERS; el generador lo usa para separar el índice del cuerpo después de la carátula. El diagrama del taller se inserta desde `../03. DIAGRAMAS UML/01_CASOS_DE_USO/GR09_CasosUso_EurekaBank.png`; conserva esa ubicación o actualiza su ruta relativa.
- El ERS describe EurekaBank y sus fuentes de código; no incluyas referencias a DOCX de otros proyectos ni al taller de casos de uso. Tras generar los DOCX, exporta cada uno a PDF y renderiza todas sus páginas a PNG para inspección visual. En Windows puede automatizarse con Word COM y `pdftoppm`; revisa la carátula, índice, saltos, tablas, títulos huérfanos e imagen del diagrama antes de dar por finalizados los documentos.

## Crear el diagrama de casos de uso con PowerDesigner

- El modelo UML fuente es `01. UML/03. DIAGRAMAS UML/01_CASOS_DE_USO/GR09_CasosUso_EurekaBank.oom`. La imagen de entrega `GR09_CasosUso_EurekaBank.png` se exporta desde ese modelo en PowerDesigner; no la sustituyas por un dibujo independiente.
- Para regenerar el modelo y su PNG, usa Windows PowerShell de 32 bits y la automatización OLE de PowerDesigner 16:

  ```powershell
  & "$env:WINDIR\SysWOW64\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -ExecutionPolicy Bypass -File '.\01. UML\03. DIAGRAMAS UML\01_CASOS_DE_USO\crear_diagrama_casos_uso.ps1'
  ```

- `crear_diagrama_casos_uso.ps1` crea actores, casos y relaciones; `ajustar_diagrama_casos_uso.vbs` distribuye los símbolos, rehace los conectores, corrige sus rutas y exporta el PNG mediante PowerDesigner. El modelo se cierra tras exportar para liberar el archivo.
- Si los archivos de salida ya existen, agrega `-Force` para reemplazarlos. Cierra antes el modelo en PowerDesigner si alguien lo dejó abierto; un modelo abierto bloquea el `.oom` en Windows.
- Después de cambiar actores, casos o relaciones, actualiza ambos scripts y la sección de relaciones del documento de casos de uso. Confirma que las flechas `<<include>>` salgan del caso principal hacia el caso incluido y revisa visualmente el PNG exportado.
