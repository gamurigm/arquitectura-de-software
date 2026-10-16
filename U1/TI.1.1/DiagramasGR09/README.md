# Diagramas GR09

`GenerarDiagramas.vbs` genera modelos editables de PowerDesigner para SOAP Java, SOAP .NET, REST Java y REST .NET. Cada proyecto recibe los mismos diagramas funcionales: un caso de uso, dos actividades, cuatro secuencias, clases y arquitectura. Cambian los nombres técnicos y mensajes del protocolo.

El caso de uso común sigue la referencia visual: iniciar sesión, validar token, convertir unidades, validar unidades y especializaciones por magnitud. Cada proyecto exporta nueve PNG, 36 en total. Las actividades y secuencias comunes son idénticas; las secuencias muestran el protocolo y la tecnología de cada implementación.

Ejecuta desde la raíz del repositorio con PowerDesigner 16 instalado:

```powershell
& "$env:WINDIR\SysWOW64\cscript.exe" //nologo .\U1\TI.1.1\DiagramasGR09\GenerarDiagramas.vbs
```

El script actualiza los modelos `GR09_*` en la carpeta `01. UML\03. DIAGRAMAS UML` de cada proyecto y exporta cada diagrama a PNG en la misma subcarpeta. En la primera pasada distribuye las clases con rutas explícitas, aplica auto-layout a las actividades, separa participantes de secuencia y fija rutas en casos de uso y arquitectura. El diagrama de clases separa el cliente y la autenticación; arquitectura muestra por separado validación y almacén de tokens. Luego conserva los movimientos y rutas manuales al regenerar. Agrega 32 px de margen blanco a cada PNG. Conserva IDs, crea símbolos nuevos e informa de objetos `GR09_*` desconocidos sin borrarlos. También mantiene respaldos PowerDesigner: `.oob` para modelos, `.eab` para arquitectura y `.pjb` para proyectos. La primera ejecución crea respaldos iniciales; cada actualización guarda allí la versión previa.

Ejecuta las pruebas con `py -3 .\U1\TI.1.1\DiagramasGR09\test_generador.py`; los modelos de integración se crean en `D:\tools` y se eliminan al terminar.
