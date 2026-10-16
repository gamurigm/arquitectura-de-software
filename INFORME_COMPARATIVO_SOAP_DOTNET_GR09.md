# Comparación de implementaciones SOAP .NET: GR09 frente a GR04, GR05 y GR08

**Fecha de revisión:** 15 de octubre de 2026  
**Alcance:** comparación de los tres proyectos alojados en `tmp/` con el proyecto propio `U1/TI.1.1/TI.1.1. PAO202651_SOAP_DOTNET_SINBDD_NRC36884_GR09/`. Se revisaron estructura e inventario de archivos, código fuente del servidor y clientes, configuración, documentación de texto y presencia de documentos y diagramas. No se modificó ningún proyecto comparado.

> Los proyectos de `tmp/` ya estaban sin seguimiento de Git (`?? tmp/`) al comenzar la revisión. Este informe se añade en la raíz del repositorio.

## 1. Resumen ejecutivo

GR09 implementa una solución más integrada y actual: ASP.NET Core 10, CoreWCF 1.9.1 y Kestrel; un contrato SOAP con operaciones genéricas para tres magnitudes; token temporal transportado en cabecera SOAP; cuatro consumidores .NET 10 organizados por capas; y una estructura de documentación que declara expresamente el alcance sin base de datos.

Los referentes ofrecen tres enfoques distintos:

| Proyecto | Implementación observada | Rasgo principal |
|---|---|---|
| GR04 | WCF clásico, contratos de servicio y clientes separados; aplicación organizada en carpetas de UML, servidor, clientes y documentación | Tiene una base grande de clientes y artefactos, pero el `IService1` visible conserva operaciones de plantilla (`GetData`) mientras la lógica Monster aparece en una interfaz/clase distinta. |
| GR05 | WCF clásico alojado como `.svc`, `ServiceContract`/`OperationContract`, configuración `Web.config`; cuatro clientes | Contrato amplio de operaciones individuales y autenticación booleana; presenta nombres y contenidos heredados/inconsistentes (por ejemplo, proyecto GR05 con espacios de nombres y `.csproj` GR09). |
| GR08 | Servicio ASP.NET clásico, interfaz y clase de conversión bajo `App_Data`, solución `.sln` | Implementación pequeña con operaciones individuales, usuario y contraseña fijos, y conjunto de conversiones diferente. |
| GR09 | ASP.NET Core 10 + CoreWCF 1.9.1, endpoint `BasicHttpBinding`, WSDL HTTP; cuatro clientes .NET 10 | Contrato compacto y explícito, lógica separada, controles de token y fallos SOAP; coincide con ERS/manual en su enfoque general. |

**Conclusión:** GR09 se distingue en modernización del host, límites de capas, seguridad por token y normalización de conversiones. Sus áreas de mejora frente al material de referencia están en la cobertura de artefactos UML editables y en hacer más demostrable la documentación de clientes y operación. Los referentes tienen más material visual o guías de despliegue en ciertos casos, pero también arrastran plantillas, copias generadas, etiquetas incorrectas y contradicciones; su volumen no equivale a mayor consistencia.

## 2. Identificación y límites de evidencia

### GR09

Raíz revisada: `U1/TI.1.1/TI.1.1. PAO202651_SOAP_DOTNET_SINBDD_NRC36884_GR09/`.

- Servidor: `04. SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09/`.
- Clientes: `04. CLICON`, `04. CLIESC`, `04. CLIWEB` y `04. CLIMOV`.
- ERS: `01. UML/01. ERS/ERS_SOAP_DOTNET_SINBDD_NRC36884_GR09.docx`.
- Diagrama visible: `01. UML/03. DIAGRAMAS UML/01_CASOS_DE_USO/CASOS_DE_USO_SOAP_DOTNET_GR09.svg`.
- Manual técnico: `05. DOCUMENTACION/02. MANUAL TECNICO/MANUAL_TECNICO_SOAP_DOTNET_SINBDD_NRC36884_GR09.docx`.
- README de raíz y servidor revisados. El README declara cuatro clientes; su frase inicial dice “cuatro clientes MVC”, aunque la consola y MAUI tienen estructuras propias y no son MVC web en sentido estricto.

### Referentes de `tmp/`

- GR04: `tmp/TI.1.2. PAO202650_SOAP_DOTNET_SINBDD_NRC30742_GR04/DOTNET SOAP/`.
- GR05: `tmp/TI.1.2. PAO202650_SOAP_DOTNET_SINBDD_NRC30742_GR05/TI.1.2. PAO202650_SOAP_DOTNET_SINBDD_NRC30742_GR5/`.
- GR08: `tmp/TI.1.2. PAO202650_SOAP_DOTNET_SINBDD_NRC30742_GR08/TI.1.2.SOAP_DOTNET_CONVUNI_SINBDD/`.

La revisión encontró artefactos compilados y carpetas de IDE en los referentes. Se excluyeron `bin`, `obj`, `target`, `build` y paquetes de la evaluación conceptual. No se tomó la existencia de un `.dll`, `.apk`, `.war` u otro binario como prueba de que el servicio se despliega o funciona hoy. No se ejecutaron aplicaciones ni se invocaron endpoints remotos.

Los documentos DOCX/PPTX se inventariaron y se extrajo texto para identificar alcance y estructura. No se hizo una evaluación visual página por página de cada diapositiva, captura o diagrama embebido en documentos ofimáticos. La tabla de diagramas describe archivos localizados y sus formatos, no certifica cada detalle gráfico.

## 3. Diferencias de arquitectura y tecnología

| Aspecto | GR04 | GR05 | GR08 | GR09 |
|---|---|---|---|---|
| Host SOAP | Proyecto WCF tradicional (`.slnx`, `System.ServiceModel`) y configuración del estilo IIS/WCF | WCF tradicional con `.svc`, `Web.config`, archivos `.config` y scripts de IIS/WCF | ASP.NET clásico con `.sln` y archivos de servicio separados | ASP.NET Core 10, CoreWCF 1.9.1 y Kestrel |
| Protocolo/endpoint observado | Configuración de WCF; hay `.svc`, `.wsdl`, `.xsd` y `.disco` en el proyecto | `.svc` y configuración WCF; scripts y documentos de instalación/verificación | El proyecto tiene contrato WCF; configuración heredada de host clásico | `BasicHttpBinding` y ruta explícita `/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09`; WSDL habilitado por `ServiceMetadataBehavior` |
| Estructura del servidor | Ejemplo estándar `IService1`/`Service1`; lógica Monster aparece en `controlador`, `modelo` y `vista` | `IWSConversion` y `WSConversion`, con autenticación dentro del mismo archivo de servicio | `IConversionService` y `ConversionService` en `App_Data` | `Controladores`, `Servicios`, `Modelos`, `Seguridad`; composición y DI en `Aplicacion.cs` |
| Contrato | Hay interfaz estándar con operaciones de demostración y una interfaz Monster independiente | Una operación SOAP por conversión | Una operación SOAP por conversión | Tres operaciones genéricas de conversión, login y cambio de contraseña |
| Estado | El servicio legado no presenta token en el fragmento revisado; validar autenticación es método booleano | Login devuelve `bool`; no se observó flujo de token en contrato/implementación | Login devuelve `bool` con credenciales fijas | Token emitido en login, enviado en header SOAP `token`, con caducidad declarada de 15 minutos |
| Conversión | Métodos concretos por pareja de unidades | Métodos concretos por pareja de unidades | Métodos concretos por pareja de unidades | Un catálogo de unidad origen/destino por magnitud, con conversores especializados |
| Clientes | Consola, escritorio, móvil MAUI y web ASP.NET Web Forms | Consola, escritorio Windows Forms, móvil Android (carpeta/proyecto contiene Kotlin/Gradle) y web | Inventario del proyecto muestra más de un tipo de cliente y documentación, aunque no se halló una matriz completa equivalente a GR09 en los archivos revisados | Consola, escritorio WPF, web ASP.NET Core MVC y Android .NET MAUI |

### Qué implica para mantenimiento

GR09 concentra las conversiones en tres operaciones de dominio: `ConvertirLongitud`, `ConvertirMasa` y `ConvertirTemperatura`. Cada llamada envía valor, unidad de origen y unidad final. GR04, GR05 y GR08 exponen en general operaciones dedicadas como `CelsiusAFahrenheit` o `MetrosAPies`. El contrato genérico reduce operaciones repetidas y facilita agregar pares dentro del catálogo, aunque requiere que clientes y servidor acuerden exactamente los nombres de unidad.

GR09 registra el nombre de servicio como `WSConversionUnidades_Java_SOAP_GR09` y el puerto como `WSConversionUnidades_Java_SOAP_GR09Port`, mientras el ensamblado, proyecto y endpoint son `..._DotNet_SOAP_GR09`. El identificador del contrato coincide con la variante Java para interoperar con el servidor Java GR09. Esta diferencia parece deliberada, pero debe describirse en el manual para que no se interprete como copia o error.

## 4. Comparación del código del servidor

### 4.1 GR09: recorrido de solicitud

`Aplicacion.cs` registra `AdministradorTokens`, `ServicioAutenticacion`, los tres `IConversor<T>`, `ServicioConversor` y el controlador. Configura servicios y metadatos CoreWCF, agrega el endpoint `BasicHttpBinding`, publica metadatos HTTP y ejecuta Kestrel.

`IContratoConversorSoap.cs` fija espacio de nombres, nombre del servicio y puerto. Cada operación tiene nombre SOAP explícito (`login`, `cambiarContrasenia`, `convertirLongitud`, `convertirMasa`, `convertirTemperatura`) y nombres explícitos para parámetros y resultado. Esta decisión protege la forma XML/WSDL ante cambios de nombres C#.

`ControladorConversorSoap.cs` adapta contrato a servicios. Login devuelve un token. Las otras cuatro operaciones protegidas validan primero el token de cabecera. La extracción usa el namespace SOAP acordado y devuelve `FaultException` ante token ausente, inválido o fallo de validación. `Ejecutar` convierte errores de argumento y estado en fallos SOAP.

`ServicioAutenticacion.cs` compara credenciales iniciales con comparación ordinal, permite cambiar la contraseña en memoria y delega generación/validación de token a `AdministradorTokens`. El estado vive en memoria: reiniciar el proceso reinicia la contraseña a la inicial y elimina tokens/sesiones.

`Servicios/ServicioConversor.cs`, `Modelos/Utilidades/ConversorLongitud.cs`, `ConversorMasa.cs`, `ConversorTemperatura.cs` y los tipos de `Modelos/Unidades/` contienen la lógica/catálogo. Las implementaciones deben leerse como fuente de verdad del comportamiento; ERS y manual son contratos descriptivos que deben seguirlas.

### 4.2 GR04

El `IService1.cs` encontrado es la interfaz clásica de plantilla Microsoft: `GetData(int)` y `GetDataUsingDataContract(CompositeType)`. `Service1.cs` implementa esas operaciones de ejemplo. En paralelo existen `ec.edu.monster.modelo.IServicioMonster` y `ec.edu.monster.controlador.ServicioMonster`, con autenticación `ValidarAcceso` y conversiones.

Esto crea una diferencia importante entre el servicio estándar que aparece como unidad principal del proyecto y la lógica Monster separada. El código de `ServicioMonster` tiene métodos de conversión directos, pero la evidencia leída no permite afirmar que todas esas funciones estén publicadas por el endpoint WCF principal. Conviene comprobar la configuración/contratos y no suponer que el folder basta para exponerlos.

La autenticación que se leyó compara texto fijo (`Monster`/`Monster9`) y devuelve `bool`. La lógica de conversión incluye masa, temperatura y longitud. No se observó caducidad ni protección por token en el código revisado.

### 4.3 GR05

`IService1.cs` declara `IWSConversion` con login y operaciones SOAP individuales usando `ServiceContract` y `OperationContract` explícitos. `Service1.svc.cs` implementa la conversión directamente y agrega `AutenticacionService` en el mismo archivo. Login delega a validación de credenciales y devuelve booleano.

La implementación registra argumentos mediante `Debug.WriteLine`. Ofrece temperatura, longitud y masa, con pares directos y factores inversos. El archivo reporta ser “Replicado desde Java”, pero la revisión no contrasta esa afirmación con el proyecto Java de origen.

La validación exige campos no vacíos, normaliza usuario con `Trim().ToUpper()` y compara la contraseña luego de `Trim()`. Por ello, el comportamiento de mayúsculas/espacios difiere del login ordinal de GR09. No hay token en el contrato mostrado: autenticación no equivale a autorización por sesión para cada solicitud.

El namespace del contrato y el archivo de servicio es `CONUNI_SOAP_DOTNET_GR09`, aunque el árbol y documentos son GR05; ambos proyectos cliente de consola/escritorio contienen nombres `.csproj` terminados en `G09`. El servidor se aloja bajo un árbol `GR05` pero contiene, además, archivos `SOLUCION_FINAL.md`, `SOLUCION_500.21.md`, instrucciones IIS, scripts WCF y varias variantes de `Web.config`. Es una acumulación de resolución de problemas que hace menos claro cuál configuración está activa.

### 4.4 GR08

`ConversionService.cs` implementa `IConversionService` y devuelve `bool` para login con comparación directa a `MONSTER`/`MONSTER9`. Cada conversión es un método explícito. El catálogo observado incluye longitud, masa y cinco conversiones de temperatura, entre ellas Rankine y Réaumur.

El servicio está en `App_Data`, ubicación poco descriptiva para la capa de servicio y asociada a proyectos ASP.NET clásicos. No se encontró evidencia en el fragmento revisado de token, expiración o cambio de contraseña. La carpeta identifica GR69 aunque está incluida bajo el paquete GR08; se debe tratar como la etiqueta interna del artefacto, no corregirla por inferencia.

## 5. Contrato, catálogo y comportamiento funcional

### Operaciones GR09

| Operación SOAP | Acceso | Entrada/salida observada |
|---|---|---|
| `login` | Pública | `usuario`, `contrasenia`; retorna `string` con token |
| `cambiarContrasenia` | Token | `contraseniaActual`, `contraseniaNueva`; retorna mensaje |
| `convertirLongitud` | Token | `valor`, `unidadInicial`, `unidadFinal`; retorna `double` |
| `convertirMasa` | Token | `valor`, `unidadInicial`, `unidadFinal`; retorna `double` |
| `convertirTemperatura` | Token | `valor`, `unidadInicial`, `unidadFinal`; retorna `double` |

ERS declara longitud: milímetros, centímetros, metros, kilómetros, pies, yardas y millas; masa: miligramos, gramos, kilogramos, toneladas, onzas, libras y quintales; temperatura: Celsius, Fahrenheit, Kelvin, Rankine, Newton y Réaumur. Declara rechazo de valores no finitos, masa negativa, unidades no soportadas, credenciales incorrectas y contraseña nueva vacía.

Los referentes presentan catálogos distintos. GR04 leído ofrece 5 métodos de masa, 5 de temperatura y 5 de longitud en la clase `ServicioMonster`; GR05 expone 6 de temperatura, 6 de longitud y 4 de masa; GR08 expone 5 de temperatura, 5 de longitud y 5 de masa. Los nombres y unidades no son uniformes entre proyectos, así que comparar solo por número de métodos sería engañoso.

Un caso concreto: el fragmento GR04 contiene `KilometrosAMillas(valor) => valor / 1.60934`; GR05 y GR08 usan multiplicación por aproximadamente `0.621371`; ambas expresiones son equivalentes por redondeo. GR09 debe evaluarse contra sus factores y pruebas de conversión propios, no copiar factores de un referente.

## 6. Clientes y consumo SOAP

### GR09

Los cuatro clientes declaran un adaptador/servicio SOAP independiente y mantienen modelos y controladores/vistas apropiados para cada interfaz:

- **Consola:** `Servicios/ClienteSOAP.cs`, `Controladores/ControladorConsola.cs`, `Modelos/ModelosConversion.cs`, `Vistas/VistaConsola.cs` y `Aplicacion.cs`.
- **Escritorio:** cliente WPF, `Servicios/ClienteSOAP.cs`, `Controladores/ControladorConversor.cs`, modelos y vistas XAML.
- **Web:** ASP.NET Core MVC, controlador, modelos de login/conversión, vistas Razor y `Servicios/ClienteSOAP.cs`; configuración en `appsettings*.json`.
- **Móvil:** .NET MAUI Android, `Servicios/ClienteSOAP.cs`, controlador, modelos, páginas y archivos específicos de plataforma.

La coincidencia de `ClienteSOAP` en canales facilita mantener el contrato a través de adaptadores similares. El cliente web agrega sesión temporal de interfaz según el ERS. README/manual indican el endpoint local `http://127.0.0.1:19081/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09`; para un emulador Android, el host `127.0.0.1` puede requerir una dirección accesible desde el emulador (el manual debe distinguir ejecución local de emulador). La memoria de trabajos anteriores documenta el uso de `10.0.2.2` en el emulador; es un dato previo y no sustituye una lectura actual de cada configuración cliente.

### Referentes

- GR04 documenta e inventaría consola, escritorio, MAUI y ASP.NET Web Forms. Sus clientes usan proxy de servicio conectado (por ejemplo `Reference.cs`) y estructura orientada a vistas/controladores. El servidor y los clientes son proyectos separados.
- GR05 contiene consola y Windows Forms .NET, web con archivos `.cshtml`, un cliente Android con recursos Kotlin/Gradle y un gran conjunto de archivos XML/IDE. Su forma de cliente móvil difiere de MAUI y de GR09. La presencia de archivos generados no demuestra que la última versión del cliente consuma el endpoint activo.
- GR08 tiene carpetas de servidor, consola, escritorio, web y móvil en el inventario, además de archivos de documentación. No se revisó cada clase de cada consumidor con la misma profundidad que los cuatro adaptadores GR09; por tanto, las diferencias cliente a cliente de GR08 se dejan como inventario, no como afirmaciones de comportamiento.

## 7. Documentación y coherencia con implementación

### GR09

**Fortalezas observables:**

- ERS específico de SOAP .NET GR09, con propósito, alcance sin BDD, arquitectura, stack, requisitos, operaciones y seguridad.
- README raíz enumera servidor y cuatro clientes y reconoce que MER/BDD son carpetas conservadas sin modelos/esquemas.
- README del servidor declara CoreWCF 1.9.1, .NET 10, SOAP 1.1, URL, WSDL, credenciales iniciales y token de 15 minutos.
- Manual técnico separado y nombrado específicamente para SOAP .NET GR09.
- Los nombres de contrato y operaciones están fijos mediante atributos, lo cual ayuda a mantener correspondencia con WSDL y clientes.

**Diferencias o vacíos documentales localizados:**

- README raíz llama a toda la solución “cuatro clientes MVC”. Eso simplifica en exceso el patrón: consola, WPF y MAUI tienen separación propia; el cliente web sí es ASP.NET Core MVC.
- Solo se encontró un diagrama UML renderizado como archivo fuente visible: casos de uso SVG. Las carpetas de actividad, secuencia, clases y arquitectura contienen README, pero no se localizaron diagramas equivalentes en formatos `.svg`, `.png`, `.drawio`, `.eap` o fuente en esas carpetas del proyecto GR09.
- ERS describe cinco operaciones y criterios funcionales; el diagrama SVG declara cinco operaciones y token requerido para las protegidas. La distribución es coherente a nivel alto, pero actividad/secuencia/clases/arquitectura no permiten revisar visualmente flujos, excepciones, capas y despliegue.
- La dirección documentada como `127.0.0.1` es adecuada para llamada desde el mismo host; el documento debería incluir la dirección/ajuste aplicable a emulador y dispositivos, y dónde configurar endpoint por cliente.
- La prueba de errores SOAP está descrita en ERS, pero la documentación debería incluir ejemplos de request/response para token ausente/expirado, credenciales fallidas, conversión inválida y cambio de contraseña.
- El estado sin base de datos implica que credenciales y token se mantienen en memoria. El reinicio pierde cambios de contraseña y sesiones; conviene exponer este comportamiento en el manual de operación.

### GR04

La estructura documental es extensa: ERS y ECUD en DOCX; directorios para casos de uso, actividad, secuencia, clases y arquitectura; README por sección; manual DOCX y presentación PPTX. También tiene carpetas MER/BDD y tres SVG de acuerdo con el inventario. La cantidad sugiere cobertura documental más amplia que GR09.

El riesgo es que la estructura prometa diagramas cuyo directorio solo tiene README o que el contenido no coincida con el código ejecutable. El `IService1` que se leyó aún es el ejemplo `GetData`, y la lógica Monster está separada. Debe comprobarse el endpoint real antes de tomar su ERS/manual como implementación efectiva.

### GR05

Incluye ERS y ECUD DOCX, un diagrama de actividad PNG, documentación DOCX y PPTX, README, material sobre MER/BDD y documentación técnica adicional de IIS/WCF. Al mismo tiempo, el archivo ECUD se llama `ECUD_RESTFUL_JAVA_SINBDD_GR69.docx` dentro del proyecto SOAP GR05, y aparecen namespace/proyecto cliente `GR09`. Esas señales reducen confianza en trazabilidad de versiones y pertenencia.

Los documentos de resolución de problemas y scripts de `Web.config` ayudan a reproducir una reparación concreta, pero hay que indicar cuál configuración es la fuente activa y mover las variantes históricas a un registro de cambios para no confundir al operador.

### GR08

Se localizaron un ERS y un manual técnico, ambos DOCX, además de una presentación PPTX y material de UML/arquitectura en la estructura. La carpeta interna usa `GR69` pese a estar en el paquete llamado GR08. Se recomienda validar portada, alcance, autoría/identificador, operaciones y capturas del DOCX/PPTX frente al código antes de reutilizarlos.

## 8. Diagramas y artefactos de modelado

| Proyecto | Artefactos encontrados | Lectura comparativa |
|---|---|---|
| GR04 | Carpetas para casos de uso, actividad, secuencia, clases y arquitectura; tres SVG en el árbol general; documentos ERS/ECUD | Mayor estructura explícita de modelado. Hay que distinguir carpetas-guía de archivos de diagrama reales y confirmar que muestren `ServicioMonster` publicado por WCF. |
| GR05 | Diagrama de actividad PNG; archivo Enterprise Architect (`.eam`); ERS/ECUD | Más de un formato de modelado. El ERS SOAP y el ECUD con nombre REST Java no guardan trazabilidad obvia. |
| GR08 | Documentación y árbol UML; artefactos de Enterprise Architect en el paquete; DOCX/PPTX | Revisar identificadores internos GR69 y correspondencia con servicio `WS_SOAP_DOTNET_GR69`. |
| GR09 | SVG de casos de uso con actor Usuario, cinco operaciones, límite del sistema y relación de token; README en carpetas de actividad/secuencia/clases/arquitectura | El diagrama de casos es accesible, legible como vector y describe la protección por token. Faltan archivos de diagrama equivalentes para explicar secuencia de autenticación, clases/capas y despliegue. |

El SVG de GR09 tiene título y descripción accesibles, dimensiones 1200×720, y nombra explícitamente autenticación y token. Es un artefacto fuente inspeccionable y versionable. Para una entrega de arquitectura más completa, los diagramas prioritarios serían: secuencia login → token → conversión; clases/paquetes reales del servidor y adaptadores; componentes/despliegue con Kestrel/CoreWCF, cuatro clientes y endpoint/WSDL. Mantenerlos sincronizados con el contrato evita diagramas decorativos o desfasados.

## 9. Seguridad y manejo de errores

| Control | GR04 | GR05 | GR08 | GR09 |
|---|---|---|---|---|
| Credenciales | Valores fijos observados en método `ValidarAcceso` | Valores constantes en clase de autenticación; usuario normalizado con mayúsculas y espacios recortados | Comparación literal con usuario/clave fijos | Valores iniciales en servicio de autenticación; contraseña puede cambiar en memoria |
| Login | Booleano | Booleano | Booleano | Token temporal |
| Reautorización por llamada | No visible en fragmentos revisados | No aparece en contrato leído | No visible en servicio leído | Se exige cabecera `token` en cambiar clave y conversiones |
| Expiración | No observada | No observada | No observada | 15 minutos declarados en ERS/README; el detalle de almacenamiento está en `AdministradorTokens.cs` |
| Errores de contrato | Patrón de plantilla usa excepción de argumento en método demo | Principalmente resultados directos y trazas `Debug` en servicio leído | Operaciones directas; tratamiento no observado en clase consultada | `FaultException` con mensajes ante auth/validación y traducción de excepciones de dominio |
| Contraseñas/estado persistente | Sin base de datos declarada; no se vio cambio de clave | Credenciales constantes en código | Credenciales constantes en código | Estado en memoria; no equivale a persistencia ni almacenamiento seguro de credenciales |

GR09 ofrece el control de acceso más completo entre las implementaciones cuyo servidor se leyó: separar autenticación de autorización, expirar token y devolver fallos SOAP. Sin embargo, los identificadores/credenciales iniciales están en código/configuración del proyecto académico y no debe presentarse esto como almacenamiento de contraseñas apto para producción. Tampoco se verificó auditoría, protección contra fuerza bruta o transporte TLS.

## 10. Organización, calidad y trazabilidad

### Ventajas de GR09

1. Responsabilidades explícitas en el servidor; las conversiones no viven dentro del adaptador SOAP.
2. Registro por DI y tipos especializados por magnitud.
3. Contrato wire con nombres SOAP estables, desacoplado de convenciones internas C#.
4. Un mismo patrón de cliente SOAP presente para los cuatro canales.
5. ERS/README reconocen claramente “sin BDD” y no presentan MER/BDD vacíos como una base instalada.
6. Nomenclatura GR09 bastante consistente entre carpeta, ensamblado, endpoint y documento, con la excepción documentada del contrato Java-compatible.

### Riesgos observados en los referentes

1. Plantilla WCF sin reemplazar en GR04 (`GetData`/`CompositeType`) convive con servicio Monster separado.
2. GR05 cruza identificadores de grupos, tecnologías y proyectos: ECUD REST Java GR69 y proyectos/namespace cliente GR09 dentro de GR05.
3. Configuraciones duplicadas/históricas en GR05 dificultan determinar el `Web.config` activo.
4. GR08 está empaquetado como GR08, pero los nombres internos apuntan a GR69.
5. Clientes móviles no son equivalentes tecnológicamente: MAUI en GR04, Android Kotlin en GR05 y MAUI .NET en GR09.
6. Los catálogos por método requieren mantener muchas operaciones, proxies y pantallas alineados al añadir unidades.

### Riesgos o brechas de GR09

1. Catálogo y tabla de unidades del ERS deben cotejarse operación por operación con `ConversorLongitud`, `ConversorMasa`, `ConversorTemperatura` y con catálogos de cada cliente; la presente revisión confirma estructura e intención, pero no recalculó exhaustivamente cada factor ni cada pareja.
2. El manual debe distinguir endpoint local, endpoint de cliente web y endpoint accesible desde emulador.
3. Completar diagramas UML y anexar ejemplos SOAP/WSDL aumentaría la capacidad de revisión independiente.
4. README dice “MVC” para los cuatro clientes. Ajustar esa oración a “separación por capas; ASP.NET Core MVC en cliente web” evitaría una afirmación técnica demasiado amplia.
5. Contrato usa identificador Java para compatibilidad. Documentar la decisión previene renombrado accidental que rompa interoperabilidad.
6. Servicio de autenticación guarda contraseña actual en memoria y no la persiste. Es coherente con el alcance académico sin BDD, pero limita el comportamiento tras reinicio.

## 11. Recomendaciones ordenadas

### Prioridad alta: cerrar consistencia de GR09

1. **Alinear README y ERS con arquitectura real.** Cambiar “cuatro clientes MVC” por descripción precisa de capas por canal y MVC solo para web.
2. **Añadir diagrama de secuencia SOAP.** Mostrar login, emisión, transporte de `token`, conversión y los caminos de token ausente/expirado.
3. **Añadir diagrama de componentes/despliegue.** Mostrar Kestrel, CoreWCF, endpoint/`?wsdl`, servidor, clientes y configuración local/emulador.
4. **Completar el manual de ejecución.** Incluir comandos por cliente, requisito del servidor activo, endpoint por ambiente y resolución de errores de conexión; no copiar contraseñas fuera de las credenciales de prueba ya autorizadas.
5. **Añadir tabla de catálogo verificable.** Por unidad, símbolo/nombre aceptado por el contrato y factor o fórmula respecto de la unidad base; indicar tratamiento de conversiones afines de temperatura.
6. **Documentar ciclo de vida del estado.** Explicar que token y contraseña cambiada no sobreviven al reinicio, en línea con la ausencia de BDD.

### Prioridad media: pruebas de interoperabilidad documental

7. Incluir requests/responses SOAP mínimos para cada operación, con cabecera XML `token` y Faults representativos.
8. Publicar un WSDL de ejemplo o una instrucción reproducible para obtenerlo desde `?wsdl`, sin tratar una copia generada antigua como fuente de verdad.
9. Incorporar al manual una matriz “requisito → operación → clase → cliente → evidencia”, en especial para masa negativa, `NaN`/infinito y unidades inválidas.
10. Explicar el nombre `WSConversionUnidades_Java_SOAP_GR09` del contrato y su propósito de compatibilidad.

### Recomendaciones para aprender de los referentes

11. De GR04, tomar la organización de documentación por tipo de diagrama, pero exigir que cada carpeta contenga el archivo editable/rendereado y que cada figura coincida con el endpoint ejecutable.
12. De GR05, tomar la guía operativa IIS/WCF solo si GR09 adopta ese host; en GR09 actual, documentar CoreWCF/Kestrel y no trasladar configuración WCF clásica.
13. De los tres referentes, no copiar directamente contratos de una operación por par: el contrato genérico actual de GR09 reduce duplicación y corresponde a sus modelos de unidad.
14. Corregir nombres de grupo únicamente en los proyectos que se mantengan y con evidencia de autoría/versión; `tmp/` contiene material comparativo y no se alteró.

## 12. Catálogo de artefactos revisados

### GR09

- Servidor: `Aplicacion.cs`, `Controladores/ControladorConversorSoap.cs`, `Controladores/IContratoConversorSoap.cs`, `Seguridad/ServicioAutenticacion.cs`, `WSConversionUnidades_DotNet_SOAP_GR09.csproj`, `appsettings.json` y README de servidor.
- Modelos/servicios del servidor: unidades de longitud/masa/temperatura, conversores por magnitud, `IConversor<T>` y `ServicioConversor`.
- Clientes: adaptadores `ClienteSOAP.cs`, controladores, modelos, vistas y archivos de proyecto de consola, WPF, MVC web y MAUI Android.
- Documentación: README raíz, README de servidor y clientes, ERS DOCX y manual técnico DOCX.
- Modelado: SVG casos de uso; READMEs de carpetas ECUD, MER, BDD, actividad, secuencia, clases y arquitectura.

### GR04

- Servidor WCF en `03. SERVIDOR/WS_CON_UNI_SOAP_DOTNET/ServidorMonster/`, interfaz y clase `IService1.cs`/`Service1.cs`, `ServicioMonster.cs`, modelo `IServicioMonster.cs`, archivos de solución/proyecto y configuración WCF.
- Clientes en `04. CLICONS`, `04. CLIESCRI`, `04. CLIMOV` y `04. CLIWEB`.
- ERS/ECUD DOCX, manual DOCX, presentación PPTX, README por áreas, diagramas y esquemas WSDL/XSD del árbol.

### GR05

- Servidor en `04. SERVIDOR/CONUNI_SOAP_DOTNET_GR05/CONUNI_SOAP_DOTNET_GR05/`: `IService1.cs`, `Service1.svc.cs`, `.svc`, `Web.config` y variantes, scripts/documentos IIS/WCF.
- Clientes consola, escritorio, móvil y web; proyectos de consola/escritorio con etiqueta G09 en el nombre de archivo; móvil con artefactos Android/Gradle/Kotlin.
- ERS/ECUD DOCX, manual DOCX, presentación PPTX, diagrama actividad PNG y archivo `.eam`.

### GR08

- Servidor en `04. SERVIDOR/WS_SOAP_DOTNET_GR69/`: `IConversionService.cs`, `ConversionService.cs`, solución/proyecto .NET clásico.
- Clientes y artefactos auxiliares en el paquete `TI.1.2.SOAP_DOTNET_CONVUNI_SINBDD`.
- ERS/manual DOCX y presentación PPTX; identificador interno GR69.

## 13. Veredicto

GR09 presenta la implementación más coherente con un backend SOAP moderno y con autorización temporal entre las cuatro comparadas. La diferencia más sustancial respecto a GR04/GR05/GR08 es de diseño del contrato: conversión por magnitud con unidades de entrada/salida y token por operación protegida, en lugar de una operación distinta por pareja y login booleano. Su servidor también tiene una separación más nítida entre contrato, seguridad y lógica de dominio.

La principal mejora concreta para GR09 es documental: complementar el caso de uso SVG con secuencia, componentes/despliegue y evidencia SOAP reproducible; corregir el uso general de “MVC”; y documentar el identificador Java-compatible del contrato y el comportamiento de estado en memoria. Los materiales de `tmp/` sirven como referencia visual y operativa, pero sus inconsistencias internas hacen necesario validar cada artefacto antes de adoptarlo.

## 14. Verificación del archivo

Este informe es una comparación estática basada en archivos presentes el 15 de octubre de 2026. No afirma que los referentes compilen ni que sus endpoints respondan. Tampoco certifica equivalencia matemática exhaustiva de todos los factores, revisión visual completa de DOCX/PPTX, ni pruebas de interacción de los cuatro clientes.
