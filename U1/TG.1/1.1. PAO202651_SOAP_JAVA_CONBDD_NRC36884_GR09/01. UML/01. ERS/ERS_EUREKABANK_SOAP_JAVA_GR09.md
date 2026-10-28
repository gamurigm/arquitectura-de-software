---
lang: es
---

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

```{=openxml}
<w:p><w:pPr><w:pStyle w:val="TtuloTDC"/></w:pPr><w:r><w:t>ÍNDICE DE TABLAS</w:t></w:r></w:p>
<w:p><w:r><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:instrText xml:space="preserve"> TOC \h \z \c "Tabla" </w:instrText></w:r><w:r><w:fldChar w:fldCharType="separate"/></w:r><w:r><w:t>Actualizar índice de tablas</w:t></w:r><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>
<w:p><w:pPr><w:pStyle w:val="TtuloTDC"/></w:pPr><w:r><w:t>ÍNDICE DE ILUSTRACIONES</w:t></w:r></w:p>
<w:p><w:r><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:instrText xml:space="preserve"> TOC \h \z \c "Ilustración" </w:instrText></w:r><w:r><w:fldChar w:fldCharType="separate"/></w:r><w:r><w:t>Actualizar índice de ilustraciones</w:t></w:r><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Caso de estudio {.unnumbered}

EurekaBank ofrece a sus clientes un servicio central para consultar cuentas y movimientos, registrar depósitos y transferir fondos. El producto combina un servidor Java SOAP, persistencia MySQL y clientes para consola, escritorio Swing, móvil Android y web. El servidor mantiene las credenciales, las sesiones temporales y las reglas para actualizar las cuentas.

La autenticación crea un token temporal. El token delimita las consultas y operaciones a la identidad del cliente; las transferencias aplican un débito y un crédito en una única transacción y aceptan reintentos con la misma clave y datos. Los depósitos también son transaccionales, pero carecen de clave idempotente y usan el empleado técnico 0001.

La línea base describe el código del repositorio. El estado documentado indica que el comportamiento se identificó en las fuentes; no representa aprobación formal ni resultados de pruebas de aceptación.

| Dato | Valor |
|---|---|
| Proyecto | EurekaBank SOAP Java con base de datos |
| Asignatura y grupo | Arquitectura de software, grupo 09 |
| Versión del ERS | 2.1 |
| Fecha de la línea base | 28 de octubre de 2026 |
| Producto documentado | Servidor SOAP, clientes de consola, escritorio, móvil y web, y persistencia MySQL |
| Estado | Especificación alineada al código del repositorio |

: Tabla 1. Control de la línea base documental


# Introducción

La especificación de requerimientos de software describe los servicios que EurekaBank proporciona al cliente, las reglas de sesiones, depósitos y transferencias, la información que intercambia y las condiciones técnicas necesarias para ejecutarse. Su finalidad es ofrecer una descripción verificable y común para quienes desarrollan, mantienen, revisan y utilizan la solución.

La arquitectura distribuye la interacción en los clientes y concentra las reglas de servicio y la persistencia en el servidor. Los mensajes SOAP transportan las solicitudes y respuestas; las operaciones JDBC permiten consultar credenciales y movimientos, bloquear una cuenta durante una actualización e insertar el registro del depósito.

El documento se basa en las firmas SOAP, la implementación Java, los manifiestos Maven y los scripts de creación y carga de la base de datos. Mantiene los nombres de las operaciones publicados por el sistema. No atribuye funcionalidades a carpetas vacías ni considera que las tablas disponibles en MySQL impliquen, por sí mismas, servicios expuestos.

# Propósito

El propósito del ERS es especificar de forma ordenada las funciones bancarias de EurekaBank y proporcionar criterios observables para revisar su cumplimiento. También establece las restricciones de diseño, las interfaces externas y los puntos que deben resolverse antes de ampliar la solución.

El documento está dirigido al grupo de desarrollo, al docente que revisa el proyecto, a quienes administran el entorno de ejecución y a los operadores que necesitan comprender el alcance funcional. Para el desarrollo sirve como base de mantenimiento; para la revisión permite relacionar objetivos, requisitos, casos de uso y componentes; para la operación aclara qué resultados puede confirmar el servicio.

Los cambios futuros deberán conservar la trazabilidad de los requisitos. Una modificación de las firmas SOAP, las reglas de depósito o transferencia, los datos de respuesta o la política de sesión requiere actualizar esta especificación y revisar su efecto sobre los cuatro clientes.

# Alcance

El producto proporciona autenticación mediante SOAP, crea sesiones remotas con token, consulta las cuentas activas del cliente, consulta sus movimientos, registra depósitos, transfiere fondos entre cuentas y revoca sesiones. El servidor concentra las reglas y el acceso JDBC a MySQL; cuatro clientes —consola, escritorio Swing, móvil Android y web— consumen el contrato SOAP.

| Función | Resultado de la línea base |
|---|---|
| Iniciar sesión | Validar credenciales y emitir un token de sesión cuando el usuario está vinculado a un cliente |
| Consultar cuentas | Presentar las cuentas activas asociadas al cliente autenticado |
| Consultar movimientos | Presentar el historial de una cuenta activa del cliente autenticado |
| Registrar depósito | Validar la cuenta y el importe; actualizar saldo y movimiento en una transacción |
| Transferir fondos | Debitar origen y acreditar destino en una transacción, con clave de idempotencia |
| Cerrar sesión | Revocar en el servidor el token de sesión |

: Tabla 2. Alcance funcional de EurekaBank

El servidor conserva también la operación SOAP login, que valida credenciales y responde 1 o -1, pero no emite token. Esa operación no habilita las operaciones bancarias protegidas; los flujos de los clientes utilizan crearSesion. Las operaciones de consulta de cuentas, consulta de movimientos, depósito y transferencia requieren un token vigente.

Quedan fuera de esta versión los retiros, pagos, reversos, apertura y cierre de cuentas, la administración de clientes, empleados o usuarios, y la gestión de permisos por operación. El depósito registra el código técnico de empleado 0001 y no es idempotente; una pérdida de respuesta puede dejar incierto el resultado. El transporte configurado de forma predeterminada no acredita cifrado HTTPS.

# Definiciones, acrónimos y abreviaturas

## Definiciones

| Término | Definición en el sistema |
|---|---|
| Cuenta activa | Registro de Cuenta cuyo estado es ACTIVO y que puede recibir depósitos |
| Depósito | Operación que suma un importe positivo al saldo y crea un movimiento |
| Movimiento | Registro bancario identificado por cuenta y número de movimiento |
| Operador | Persona que utiliza un cliente para ejecutar las funciones disponibles |
| Transacción | Unidad de trabajo de base de datos que confirma o revierte sus cambios |
| Confirmación | Resultado positivo comunicado después del commit de una operación bancaria |
| Resultado incierto | Situación en la que el cliente no recibe confirmación y no puede determinar si el depósito se aplicó |
| Sesión autenticada | Registro temporal en memoria del servidor asociado a un token opaco, usuario y cliente |
| Contrato de servicio | Definición de operaciones, parámetros y respuestas publicada mediante WSDL |
| Idempotencia | Tratamiento de una solicitud repetida con la misma clave y los mismos datos como una única transferencia |

: Tabla 3. Definiciones del dominio

## Acrónimos y abreviaturas

| Sigla | Significado |
|---|---|
| ERS | Especificación de requerimientos de software |
| SOA | Arquitectura orientada a servicios |
| SOAP | Protocolo de mensajería empleado para las solicitudes y respuestas del servicio |
| XML | Formato de representación de los mensajes SOAP |
| WSDL | Descripción del contrato del servicio web |
| JAX-WS | API Java para publicar y consumir servicios web SOAP |
| JDBC | API Java para acceder a la base de datos |
| WAR | Paquete de despliegue de la aplicación web del servidor |
| MVC | Separación de modelo, vista y controlador en los clientes |
| RF / RNF | Requisito funcional / requisito no funcional |
| CU / OBJ | Caso de uso / objetivo del sistema |
| PBKDF2 | Función de derivación utilizada para verificar contraseñas |

: Tabla 4. Acrónimos y abreviaturas

# Referencias

Las fuentes primarias pertenecen al proyecto EurekaBank. Sus identificadores se utilizan en las fichas de requisitos para facilitar la revisión sin repetir rutas extensas.

| ID | Fuente del repositorio | Uso en la especificación |
|---|---|---|
| F1 | WSEureka.java, paquete ec.edu.monster.ws | Operaciones y respuestas públicas del servicio |
| F2 | EurekaService.java, paquete ec.edu.monster.servicios | Validación de credenciales, cuenta e importe |
| F3 | EurekaBankDao.java, paquete ec.edu.monster.db | Autenticación, consulta y transacción bancaria |
| F4 | PasswordHasher.java, paquete ec.edu.monster.seguridad | Formato y verificación de contraseñas |
| F5 | ConexionDB.java, paquete ec.edu.monster.db | Configuración de conexión JDBC |
| F6 | Controladores, servicios y vistas de los clientes de consola y escritorio | Flujos de autenticación, consultas, depósitos y transferencias |
| F7 | Controladores, adaptadores y vistas de los clientes móvil Android y web | Flujos de autenticación, consultas, depósitos y transferencias |
| F8 | Crea_BD.sql, Carga_Datos.sql y Migrar_Transferencias_MySQL.sql, carpeta 03. BDD | Entidades, tipos, restricciones, transferencias y datos iniciales |
| F9 | pom.xml, manifiestos Android y configuración de los cuatro clientes y del servidor | Versiones, dependencias y empaquetado |
| F10 | WSEureka.java, WSDL y adaptadores SOAP incluidos en los clientes | Contrato, parámetros, respuestas y consumo del servicio |

| F11 | TransferenciaValidator.java y SolicitudTransferencia.java | Validación de cuentas, importe y clave UUID de transferencia |

# Visión general del documento

Las primeras secciones presentan el caso de estudio, el propósito, el alcance y el vocabulario. La descripción general explica la arquitectura, las funciones del producto, los usuarios, el entorno y las dependencias. La especificación desarrolla las interfaces y los requisitos funcionales y no funcionales.

Cada requisito conserva un identificador estable y señala su importancia, estado, objetivo o criterio de aceptación y fuente. Las reglas de negocio precisan las condiciones que deben respetar las operaciones bancarias. La sección de rendimiento define qué debe medirse y qué valores siguen pendientes de acordar, sin presentar estimaciones como mediciones.

La trazabilidad relaciona los requisitos con los seis casos de uso principales y los componentes del código. Las recomendaciones y conclusiones identifican las prioridades de evolución y los límites de la versión documentada.

# Descripción general

## Perspectiva del producto

EurekaBank es una solución cliente-servidor con un servicio SOAP central. Los cuatro clientes ofrecen la interacción adecuada a su plataforma y mantienen controladores o componentes de coordinación separados de las vistas y los adaptadores de servicio. WSEureka publica el contrato; EurekaService aplica validaciones y reglas; EurekaBankDao consulta y actualiza MySQL.

El recorrido común es operador → cliente de consola, escritorio, móvil o web → SOAP/XML → WSEureka → EurekaService → EurekaBankDao → JDBC → MySQL. Los clientes no acceden directamente a la base de datos. El servidor asocia cada token temporal al usuario y al código de cliente autenticado.

| Componente | Responsabilidad |
|---|---|
| Vistas de consola, escritorio, móvil y web | Capturar datos y presentar resultados, validaciones y errores |
| Controladores de cliente | Coordinar navegación, estado de sesión e interacción con el servicio |
| Adaptadores SOAP | Invocar crearSesion, traerCuentas, traerMovimientos, regDeposito, transferir y cerrarSesion |
| WSEureka | Publicar el contrato SOAP y traducir errores a respuestas o excepciones |
| EurekaService | Validar token, entradas bancarias y reglas de sesión |
| EurekaBankDao | Consultar cuentas y movimientos y ejecutar transacciones de depósito y transferencia |
| MySQL | Almacenar credenciales, cuentas, movimientos y registros de transferencia |

: Tabla 6. Responsabilidades de la arquitectura

![Ilustración 1. Casos de uso de EurekaBank generados en PowerDesigner](<../03. DIAGRAMAS UML/01_CASOS_DE_USO/GR09_CasosUso_EurekaBank.png>){width=14cm}

El modelo representa seis objetivos principales del usuario y cuatro casos incluidos para validar credenciales, cuentas y datos del depósito, y aplicar el depósito. Los casos bancarios protegidos exigen un token vigente como precondición; cerrar sesión revoca el token en el servidor.

## Funciones del producto

| Función | Entrada | Resultado |
|---|---|---|
| Iniciar sesión | Usuario y contraseña | Token opaco si la autenticación es válida; null si se rechaza |
| Consultar cuentas | Token vigente | Cuentas activas asociadas al cliente de la sesión |
| Consultar movimientos | Token y cuenta | Lista por movimiento descendente; solo para cuenta activa del cliente |
| Registrar depósito | Token, cuenta e importe decimal | 1 al confirmar; -1 al rechazar o fallar |
| Transferir fondos | Token, origen, destino, importe y clave UUID | 1 al confirmar o aceptar un reintento idempotente; -1 al rechazar |
| Cerrar sesión | Token | Revocación en memoria y resultado 1 |

: Tabla 7. Funciones del producto

El depósito actualiza saldo y movimiento de tipo 003 en la misma transacción y registra el empleado fijo 0001. La transferencia actualiza ambas cuentas y registra un débito tipo 009 y un crédito tipo 008 en una sola transacción; mantiene un registro de transferencia para reconocer solicitudes repetidas. La consulta no altera el saldo y devuelve una lista vacía cuando no hay movimientos o la cuenta no pertenece al cliente, por lo que el cliente debe distinguir también los errores de sesión y comunicación.


## Condiciones del entorno

El servidor y MySQL deben estar disponibles y comunicados. Los cuatro clientes requieren conectividad con el endpoint SOAP; el servidor accede a MySQL mediante JDBC. Cada plataforma necesita su entorno de ejecución y configuración de endpoint correspondiente.

El servidor Java está configurado para Java 25, Jakarta EE 11 y despliegue WAR en Payara 7. Los clientes Java de consola y escritorio usan Metro 4.0.4; el cliente móvil es Android y el web consume el mismo contrato SOAP. El servidor declara MySQL Connector/J 9.7.0 en Maven.

| Elemento | Condición requerida |
|---|---|
| Servidor | JDK compatible con release 25, Maven y Payara Server Community 7 |
| Persistencia | MySQL con esquema eurekabank_gr09 y tablas cargadas |
| Conectividad | Cliente a SOAP; servidor a MySQL |
| Consola | Java compatible y terminal con entrada de teclado |
| Escritorio | Entorno gráfico capaz de mostrar ventanas Swing |
| Móvil | Dispositivo o emulador Android compatible con el manifiesto del proyecto |
| Web | Contenedor web Java y navegador compatible |

: Tabla 8. Condiciones del entorno


## Características de los usuarios

El usuario funcional consulta sus cuentas y movimientos, registra depósitos y transfiere fondos desde cualquiera de los clientes disponibles. Debe elegir una cuenta de origen autorizada, revisar los datos bancarios antes de confirmar y conservar la clave de operación de una transferencia cuando se necesite reintentarla.

El responsable técnico configura Payara, MySQL, el endpoint SOAP y las variables de conexión. Ese trabajo pertenece a la administración del entorno; la aplicación no incorpora pantallas para administrar usuarios, roles o permisos.

| Participante | Conocimientos y responsabilidad |
|---|---|
| Cliente de EurekaBank | Operar uno de los clientes y verificar la cuenta, el importe y el resultado |
| Responsable técnico | Desplegar el servidor, configurar el endpoint y administrar MySQL |
| Equipo de desarrollo | Mantener el contrato SOAP, las reglas bancarias, las transacciones y los clientes |

: Tabla 9. Características de los participantes

## Interfaces externas

Las interfaces externas comprenden las vistas de los cuatro clientes, el contrato SOAP y la conexión JDBC del servidor con MySQL. No se integra con pasarelas de pago, notificaciones, correo ni otras entidades financieras.

| Interfaz | Descripción del intercambio |
|---|---|
| Operador con consola | Entrada por teclado y presentación en terminal |
| Operador con escritorio | Formularios Swing de sesión, cuentas, movimientos, depósitos y transferencias |
| Operador con móvil | Pantallas Android con navegación y solicitudes SOAP |
| Operador con web | Formularios y páginas web coordinados por controladores |
| Cliente con servidor | Solicitudes y respuestas SOAP/XML descritas por WSDL |
| Servidor con MySQL | Consultas parametrizadas y transacciones JDBC |

: Tabla 10. Resumen de interfaces externas

## Restricciones

El contrato público es SOAP. Sus nombres de operación, parámetros, tipos de respuesta y errores deben mantenerse compatibles con los WSDL y adaptadores incluidos en los clientes. MySQL es el motor de persistencia documentado; el servidor se empaqueta como WAR y se despliega en Payara Server Community 7.

Las sesiones se guardan en memoria del servidor y caducan a los 20 minutos. Un reinicio del servidor invalida las sesiones existentes. El depósito usa el empleado 0001 y el tipo de movimiento 003; la identidad autenticada no sustituye ese código técnico. La transferencia registra el usuario de la sesión y su clave de operación.

El entorno predeterminado usa HTTP local y la URL JDBC predeterminada desactiva SSL. No se acredita cifrado de extremo a extremo para una instalación distribuida. El depósito tampoco dispone de una clave de idempotencia; la transferencia sí la usa.

## Suposiciones y dependencias

Se supone que el esquema está creado, que cada usuario autenticable tiene un código de cliente asociado y que existen las relaciones necesarias para registrar movimientos. Para una operación bancaria se requiere un token vigente y una cuenta en estado ACTIVO. La transferencia requiere que ambas cuentas estén activas, que compartan moneda, que el origen pertenezca al cliente de la sesión y que tenga saldo suficiente.

La solución depende de MySQL, de su controlador JDBC, de Payara, de la configuración de conexión y de la disponibilidad de la red entre clientes y servidor. La clave de idempotencia debe conservarse cuando un consumidor reintenta una transferencia cuyo resultado no recibió.


# Especificación de requerimientos

## Requisitos comunes de las interfaces

### Interfaces de usuario

Los clientes de consola, escritorio, móvil y web deben ofrecer las mismas funciones bancarias —inicio y cierre de sesión, consulta de cuentas y movimientos, depósitos y transferencias— adaptando los controles a cada plataforma. Los controladores coordinan la navegación y llaman a los servicios de cliente; las vistas no deben acceder a MySQL.

| Pantalla o vista | Información y acciones |
|---|---|
| Inicio de sesión | Usuario, contraseña, creación de sesión y mensaje de rechazo |
| Menú | Acceso a cuentas, movimientos, depósito, transferencia y cierre de sesión |
| Cuentas | Cuentas activas del cliente, moneda y saldo |
| Movimientos | Selección de cuenta e historial ordenado |
| Depósito | Cuenta, importe y confirmación, rechazo o resultado incierto |
| Transferencia | Origen, destino, importe y resultado de la operación |
| Terminal | Acciones equivalentes y presentación textual de resultados |
| Web y móvil | Navegación y formularios adaptados a navegador o Android |

: Tabla 11. Requisitos de interacción de usuario


### Interfaces de hardware

El sistema no controla dispositivos bancarios, lectores de tarjetas, impresoras de comprobantes o cajeros automáticos. Utiliza teclado y pantalla; el cliente de escritorio también permite interacción con un dispositivo apuntador.

No se han establecido mínimos medidos de memoria, procesador, resolución o almacenamiento para una carga bancaria determinada. El dimensionamiento debe considerar Java, Payara, MySQL, los clientes y el volumen de datos. No corresponde presentar una cifra de hardware como requisito validado sin realizar esa evaluación.

### Interfaces de software

| Componente | Especificación declarada |
|---|---|
| Servidor | Java 25, Jakarta EE 11, JAX-WS y paquete WAR |
| Contenedor | Payara Server Community 7 |
| Clientes de consola y escritorio | Java 25, Maven y Metro 4.0.4 |
| Escritorio | Java Swing y llamadas SOAP fuera del hilo de eventos |
| Cliente móvil | Android y cliente SOAP sobre HTTP/XML |
| Cliente web | Java web, controladores y adaptadores SOAP |
| Base de datos | MySQL; esquema eurekabank_gr09 |
| Acceso a datos | JDBC y MySQL Connector/J 9.7.0 declarado en Maven |
| Contraseñas y sesiones | PBKDF2-HMAC-SHA-256 y token opaco con expiración de 20 minutos |

: Tabla 12. Interfaces de software

Los adaptadores de los clientes deben corresponder al contrato publicado. Un cambio incompatible exige revisar todos los consumidores. El entorno de desarrollo puede variar mientras respete los manifiestos y convenciones del repositorio; no se establece un IDE obligatorio.


### Interfaces de comunicación

El namespace publicado es http://ws.monster.edu.ec/. El WSDL local predeterminado es http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka?wsdl. El contrato expone las operaciones siguientes:

| Operación SOAP | Parámetros | Respuesta y errores |
|---|---|---|
| login | usuario y contrasena: texto | Entero 1 o -1; valida credenciales sin crear token |
| crearSesion | usuario y contrasena: texto | Token de sesión opaco; null ante rechazo o error |
| traerCuentas | token: texto | Lista de cuentas activas del cliente autenticado |
| traerMovimientos | token y cuenta: texto | Lista de movimientos; error SOAP ante fallo o sesión inválida |
| regDeposito | token, cuenta: texto e importe: decimal | Entero 1 al confirmar o -1 al rechazar o fallar |
| transferir | token, origen, destino, importe y claveOperacion: UUID | Entero 1 al confirmar o repetir idempotentemente; -1 al rechazar |
| cerrarSesion | token: texto | Revoca la sesión y devuelve 1 |

: Tabla 13. Contrato de operaciones SOAP

El token acompaña las operaciones bancarias protegidas y se mantiene en memoria del servidor por un máximo de 20 minutos. La operación login continúa disponible para compatibilidad, pero no habilita consultas ni operaciones bancarias. El endpoint HTTP local no acredita cifrado para entornos distribuidos.


## Requisitos funcionales

### Objetivo general

**OBJ-001.** Proporcionar acceso común mediante SOAP a la validación de credenciales, consulta de movimientos y registro transaccional de depósitos de EurekaBank desde clientes Java.

### Objetivos específicos

| ID | Objetivo específico |
|---|---|
| OBJ-002 | Validar las credenciales y controlar la navegación local del operador |
| OBJ-003 | Recuperar y presentar los movimientos de una cuenta sin modificarla |
| OBJ-004 | Registrar depósitos válidos manteniendo la consistencia entre saldo y movimiento |
| OBJ-005 | Comunicar confirmaciones, rechazos y fallos para orientar la acción del operador |

: Tabla 14. Objetivos específicos

### Actores

El actor principal es el usuario autorizado que opera la consola o el escritorio. MySQL y Payara son dependencias técnicas, no actores humanos del flujo. El responsable técnico prepara el entorno fuera del menú del producto.

| Actor | Meta y participación |
|---|---|
| Usuario autorizado | Iniciar sesión, consultar movimientos, registrar un depósito y cerrar el acceso local |
| Responsable técnico | Preparar el entorno y atender fallos de despliegue o conexión; sin caso de uso administrativo en la aplicación |

: Tabla 15. Actores y participación

### Lista de requisitos funcionales

| ID | Requisito | Importancia | Caso de uso |
|---|---|---|---|
| RF-01 | Validar credenciales y crear una sesión | Alta | CU-01 |
| RF-02 | Proteger operaciones con un token vigente y listar cuentas | Alta | CU-01, CU-02 |
| RF-03 | Consultar movimientos de una cuenta del cliente | Alta | CU-03 |
| RF-04 | Validar los datos del depósito | Alta | CU-04 |
| RF-05 | Comprobar titularidad y estado de la cuenta para depósitos | Alta | CU-04 |
| RF-06 | Confirmar saldo y movimiento del depósito de forma atómica | Alta | CU-04 |
| RF-07 | Revertir el depósito ante un error | Alta | CU-04 |
| RF-08 | Validar y registrar transferencias de forma atómica e idempotente | Alta | CU-05 |
| RF-09 | Comunicar resultados y fallos del servicio | Media | CU-01 a CU-06 |
| RF-10 | Revocar la sesión al cerrar sesión | Media | CU-06 |

: Tabla 16. Catálogo de requisitos funcionales

### Especificación detallada

El estado documentado indica que el comportamiento aparece en el código; no representa aprobación formal ni ejecución de pruebas de aceptación.

#### RF-01 Validar credenciales y crear una sesión {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-01 |
| Descripción | Verificar credenciales y emitir un token cuando el usuario tiene código de cliente. |
| Objetivo / importancia | OBJ-002 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | Servicio y base de autenticación disponibles. |
| Entradas | Usuario no vacío de hasta 30 caracteres y contraseña no vacía de hasta 1024 caracteres. |
| Procesamiento y criterio | PBKDF2 verifica el hash. Credenciales válidas y cliente asociado producen token; el rechazo o error de crearSesion devuelve null. |
| Trazabilidad / observaciones | CU-01; F1 a F4. login devuelve 1/-1 sin emitir token. |

: Tabla 17. Ficha de RF-01 crear sesión

#### RF-02 Proteger operaciones con un token vigente y listar cuentas {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-02 |
| Descripción | Exigir un token válido para consultar cuentas o movimientos y registrar depósitos o transferencias. |
| Objetivo / importancia | OBJ-002, OBJ-003 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | El cliente completó crearSesion. |
| Entradas | Token no nulo, conocido y no vencido; la sesión dura 20 minutos. |
| Procesamiento y criterio | traerCuentas devuelve las cuentas ACTIVO del cliente de la sesión. Un token ausente, inválido o vencido impide la operación protegida. |
| Trazabilidad / observaciones | CU-01, CU-02; F1 a F3. Las sesiones viven en memoria y se pierden al reiniciar el servidor. |

: Tabla 18. Ficha de RF-02 sesión y cuentas

#### RF-03 Consultar movimientos de una cuenta del cliente {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-03 |
| Descripción | Recuperar movimientos de una cuenta ACTIVO perteneciente al cliente autenticado. |
| Objetivo / importancia | OBJ-003 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | Token vigente y cuenta seleccionada. |
| Entradas | Cuenta no vacía y de hasta ocho caracteres en el servicio. |
| Procesamiento y criterio | Devuelve cuenta, número, fecha, tipo, acción e importe en orden descendente; otra cuenta o lista sin datos da lista vacía. |
| Trazabilidad / observaciones | CU-03; F1 a F3 y F6 a F10. La lista no tiene paginación; un error de comunicación se informa como SOAP fault. |

: Tabla 19. Ficha de RF-03 consultar movimientos

#### RF-04 Validar los datos del depósito {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-04 |
| Descripción | Rechazar solicitudes de depósito con cuenta o importe inválidos. |
| Objetivo / importancia | OBJ-004 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | La solicitud incluye un token vigente. |
| Entradas | Cuenta no vacía de hasta ocho caracteres; importe positivo, exacto a dos decimales sin redondeo y precisión máxima de doce dígitos. |
| Procesamiento y criterio | Un importe válido, entre 0,01 y 9.999.999.999,99, pasa a la validación de cuenta; lo inválido devuelve -1. |
| Trazabilidad / observaciones | CU-04; F1, F2, F6 y F7. El depósito no tiene clave idempotente. |

: Tabla 20. Ficha de RF-04 validar depósito

#### RF-05 Comprobar titularidad y estado de la cuenta para depósitos {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-05 |
| Descripción | Aplicar el depósito solo a una cuenta ACTIVO del cliente asociado al token. |
| Objetivo / importancia | OBJ-004 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | RF-02 y RF-04 se cumplen. |
| Entradas | Cuenta, cliente de la sesión y estado de la cuenta. |
| Procesamiento y criterio | El DAO bloquea la cuenta ACTIVO que coincide con cliente y cuenta. Cuenta inexistente, inactiva o ajena no cambia. |
| Trazabilidad / observaciones | CU-04; F2, F3 y F8. |

: Tabla 21. Ficha de RF-05 validar cuenta

#### RF-06 Confirmar saldo y movimiento del depósito de forma atómica {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-06 |
| Descripción | Actualizar saldo y contador e insertar el movimiento en una transacción. |
| Objetivo / importancia | OBJ-004 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | RF-04 y RF-05 se cumplen. |
| Entradas | Cuenta autorizada, saldo, importe, empleado técnico 0001 y tipo de movimiento 003. |
| Procesamiento y criterio | El DAO obtiene el siguiente número, actualiza, inserta y confirma. Saldo nuevo = saldo anterior + importe; regDeposito devuelve 1 después del commit. |
| Trazabilidad / observaciones | CU-04; F1 a F3 y F8. La identidad autenticada no sustituye el empleado fijo. |

: Tabla 22. Ficha de RF-06 depósito atómico

#### RF-07 Revertir el depósito ante un error {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-07 |
| Descripción | Revertir las escrituras si falla la actualización o inserción antes del commit. |
| Objetivo / importancia | OBJ-004, OBJ-006 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | Inició una transacción JDBC. |
| Entradas | Error SQL o de ejecución durante el depósito. |
| Procesamiento y criterio | El DAO ejecuta rollback y la fachada devuelve -1; no queda saldo cambiado sin movimiento. |
| Trazabilidad / observaciones | CU-04; F1 a F3. Una respuesta perdida después del commit deja resultado incierto. |

: Tabla 23. Ficha de RF-07 reversión

#### RF-08 Validar y registrar transferencias de forma atómica e idempotente {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-08 |
| Descripción | Validar, autorizar y aplicar una transferencia en una transacción, reconociendo reintentos con la misma clave y carga. |
| Objetivo / importancia | OBJ-005 / Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | Token vigente; origen y destino ACTIVO; origen pertenece al cliente de sesión. |
| Entradas | Cuentas distintas de ocho dígitos; importe positivo, exacto a dos decimales y máximo 9.999.999.999,99; clave UUID. Igual moneda y saldo suficiente en origen. |
| Procesamiento y criterio | El DAO bloquea ambas cuentas, registra transferencia, actualiza saldos y contadores e inserta débito tipo 009 y crédito tipo 008 antes del commit. Misma clave y carga se aceptan como repetición; distinta carga se rechaza. |
| Trazabilidad / observaciones | CU-05; F1 a F3, F8 y F11. Un reintento debe conservar la clave. |

: Tabla 24. Ficha de RF-08 transferencia

#### RF-09 Comunicar resultados y fallos del servicio {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-09 |
| Descripción | Comunicar confirmaciones, rechazos y errores sin presentar una operación fallida como confirmada. |
| Objetivo / importancia | OBJ-006 / Media |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | El cliente envió una operación. |
| Procesamiento y criterio | crearSesion devuelve token o null; operaciones numéricas devuelven 1/-1; consultas devuelven listas o SOAP fault. Los clientes distinguen lista vacía de error. |
| Trazabilidad / observaciones | CU-01 a CU-06; F1 y F6 a F10. login es una operación de compatibilidad sin token. |

: Tabla 25. Ficha de RF-09 resultados

#### RF-10 Revocar la sesión al cerrar sesión {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-10 |
| Descripción | Eliminar del servidor la sesión asociada al token cuando el usuario cierra sesión. |
| Objetivo / importancia | OBJ-006 / Media |
| Estado | Documentado en código; aceptación formal por verificar |
| Precondición | El cliente mantiene un token. |
| Procesamiento y criterio | cerrarSesion revoca el token existente y devuelve 1; el cliente elimina su copia. El token revocado no autoriza operaciones posteriores. |
| Trazabilidad / observaciones | CU-06; F1, F2 y F6 a F10. También caduca a los 20 minutos o al reiniciar el servidor. |

: Tabla 26. Ficha de RF-10 cerrar sesión


### Reglas de negocio

| ID | Regla |
|---|---|
| RN-01 | Las operaciones bancarias protegidas requieren token vigente. |
| RN-02 | Las consultas y depósitos se limitan a cuentas ACTIVAS del cliente autenticado. |
| RN-03 | El depósito acepta importes positivos, exactos a dos decimales y de hasta doce dígitos; registra tipo 003 y empleado 0001. |
| RN-04 | Saldo, contador e inserción del movimiento de depósito se confirman o revierten juntos. |
| RN-05 | La transferencia exige origen y destino distintos, ambas cuentas activas y misma moneda; el origen debe pertenecer al cliente y tener saldo suficiente. |
| RN-06 | El importe de transferencia es positivo, exacto a dos decimales y no supera 9.999.999.999,99. |
| RN-07 | La clave de transferencia debe tener formato UUID y es única por usuario. Una repetición con la misma carga es idempotente; una carga distinta se rechaza. |
| RN-08 | El débito, crédito, contadores y dos movimientos de transferencia se confirman en una transacción. Se usan tipos 009 y 008. |
| RN-09 | Las sesiones expiran a los 20 minutos, se revocan al cerrar sesión y se invalidan al reiniciar el servidor. |

: Tabla 27. Reglas de negocio de EurekaBank


## Requisitos no funcionales

### Objetivo general

Establecer las condiciones de interoperabilidad, integridad, protección de credenciales, configuración y respuesta de la interfaz que acompañan a las funciones bancarias. Los requisitos documentados describen mecanismos presentes en el código; los aspectos pendientes identifican trabajo necesario para una operación más amplia.

### Lista y especificación de requisitos no funcionales

#### RNF-01 Verificación de contraseñas {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-01 |
| Descripción | Las credenciales deberán verificarse con PBKDF2-HMAC-SHA-256, 600 000 iteraciones, sal de 16 bytes y clave derivada de 256 bits. El formato almacenado debe contener el prefijo y los parámetros esperados. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | La verificación admite el formato configurado y rechaza formatos o parámetros inválidos. El hash esperado se compara mediante MessageDigest.isEqual. Los arreglos temporales de contraseña se limpian en los puntos implementados. |
| Fuente y observaciones | F3 y F4. La limpieza de arreglos no implica eliminación inmediata de todas las cadenas inmutables de los clientes o de SOAP. |

: Tabla 28. Ficha de RNF-01 verificación de contraseñas

#### RNF-02 Protección de la configuración de acceso {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-02 |
| Descripción | El usuario y la contraseña de MySQL deberán obtenerse de EUREKABANK_DB_USER y EUREKABANK_DB_PASSWORD, sin incorporar credenciales reales al código. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | La ausencia o valor blanco de una variable obligatoria produce un error de conexión controlado. EUREKABANK_DB_URL permite configurar la URL; existe una URL local predeterminada. |
| Fuente y observaciones | F5. La cuenta de MySQL y sus permisos se administran fuera de la aplicación. |

: Tabla 29. Ficha de RNF-02 protección de la configuración de acceso

#### RNF-03 Integridad y concurrencia del depósito {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-03 |
| Descripción | El depósito deberá utilizar una transacción JDBC y un bloqueo de fila de la cuenta durante la lectura y modificación del saldo. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | Las escrituras se confirman después de completar el movimiento o se revierten ante error. Los depósitos que utilizan esta ruta para una misma cuenta se coordinan mediante el bloqueo de esa cuenta. |
| Fuente y observaciones | F3. Otros procesos que modifiquen la base deben respetar reglas compatibles; no se acredita coordinación con integraciones externas. |

: Tabla 30. Ficha de RNF-03 integridad y concurrencia del depósito

#### RNF-04 Precisión monetaria {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-04 |
| Descripción | El importe y el saldo deberán tratarse como valores decimales. La validación no deberá redondear silenciosamente una cantidad con fracciones monetarias no representables. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | El servicio usa BigDecimal, escala dos y precisión máxima de doce dígitos para el importe. La base utiliza DECIMAL(12,2); un saldo resultante fuera de capacidad impide confirmar la transacción. |
| Fuente y observaciones | F2, F3 y F8. La capacidad del saldo es una restricción adicional a la validez del importe de entrada. |

: Tabla 31. Ficha de RNF-04 precisión monetaria

#### RNF-05 Respuesta de la interfaz de escritorio {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-05 |
| Descripción | Las llamadas remotas del escritorio deberán ejecutarse fuera del hilo de eventos de Swing. Los controles de envío deberán quedar ocupados mientras se procesa una solicitud. |
| Importancia | Media |
| Estado | Documentado |
| Criterio de revisión | El controlador usa SwingWorker para la tarea y actualiza la interfaz al finalizar. El formulario muestra el estado Procesando y recupera sus controles después del resultado. |
| Fuente y observaciones | F6. Este mecanismo conserva la interacción de la ventana; no establece una latencia máxima del servicio. |

: Tabla 32. Ficha de RNF-05 respuesta de la interfaz de escritorio

#### RNF-06 Interoperabilidad SOAP {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-06 |
| Descripción | Los clientes deberán consumir el contrato publicado mediante SOAP/XML y conservar los nombres de operaciones, parámetros y tipos de respuesta del WSDL. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | Los cuatro clientes consumen el contrato SOAP incluido o configurado. Un cambio incompatible debe revisarse en cada consumidor. |
| Fuente y observaciones | F1, F9 y F10. La interfaz del servidor documentado es SOAP. |

: Tabla 33. Ficha de RNF-06 interoperabilidad soap

#### RNF-07 Tratamiento de fallos {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-07 |
| Descripción | El servidor deberá registrar los errores internos y comunicar a los clientes los resultados establecidos por cada operación sin presentar un depósito fallido como confirmado. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | crearSesion puede devolver null; operaciones numéricas devuelven 1/-1; las consultas comunican errores SOAP. Los clientes informan estados sin confirmar operaciones rechazadas. |
| Fuente y observaciones | F1, F3 y F6 a F10. Los códigos numéricos no distinguen todas las causas del rechazo. |

: Tabla 34. Ficha de RNF-07 tratamiento de fallos

#### RNF-08 Mantenibilidad y construcción {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-08 |
| Descripción | El código deberá conservar la separación entre publicación SOAP, servicio, DAO y clientes. Las dependencias y el nivel Java se definirán en Maven. |
| Importancia | Media |
| Estado | Documentado |
| Criterio de revisión | La revisión puede identificar cada responsabilidad y sus manifiestos. La construcción requiere un JDK compatible con release 25; el WAR usa el nombre definido por el servidor. |
| Fuente y observaciones | F1, F2, F3 y F9. La especificación no acredita por sí misma una compilación ejecutada. |

: Tabla 35. Ficha de RNF-08 mantenibilidad y construcción

#### RNF-09 Confidencialidad y autorización del servicio {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-09 |
| Descripción | Las operaciones bancarias deberán exigir una sesión autenticada y el transporte deberá proteger las credenciales y datos en entornos distribuidos. |
| Importancia | Alta |
| Estado | Token y verificación de credenciales documentados; TLS y permisos por operación pendientes |
| Criterio de revisión | crearSesion verifica hash PBKDF2 y emite token opaco con caducidad de 20 minutos; las operaciones protegidas validan el token y la titularidad de cuenta. Antes de exposición externa se requiere HTTPS. |
| Fuente y observaciones | F1 a F5. Las sesiones son volátiles en memoria; HTTP local y JDBC con SSL desactivado no acreditan cifrado de extremo a extremo. |

: Tabla 36. Ficha de RNF-09 confidencialidad y autorización del servicio


## Otros requerimientos

### Restricciones de diseño

La capa SOAP debe mantenerse como fachada del contrato, el servicio como lugar de validación y el DAO como responsable de persistencia. Una nueva operación no debe permitir a los clientes escribir directamente en la base. Las consultas deben conservar el uso de parámetros preparados para los valores del usuario.

La autorización remota mediante token está implementada y limita consultas y depósitos al cliente de la sesión; la transferencia también registra el usuario. Antes de exponer el servicio fuera del entorno controlado debe definirse HTTPS, permisos por operación, distribución de sesiones y trazabilidad del empleado real del depósito.

La transferencia ya registra una clave de idempotencia por usuario y carga; el depósito todavía requiere un identificador de solicitud antes de permitir reintentos seguros. Deshabilitar un botón ayuda a evitar duplicaciones en una vista, pero no sustituye un mecanismo de idempotencia.

### Restricciones de hardware

La solución requiere recursos suficientes para ejecutar el entorno Java y las dependencias que se instalen en cada equipo. No hay una integración obligatoria con hardware bancario. La resolución, memoria y capacidad mínima deben definirse con el entorno de destino y una carga representativa.

La base de datos necesita almacenamiento persistente y un procedimiento de respaldo administrado. El repositorio no establece una política operativa de copias, retención o recuperación. La especificación distingue esa necesidad operativa del comportamiento transaccional ya implementado.

### Atributos de calidad

| Atributo | Mecanismo o condición de la línea base |
|---|---|
| Integridad | Transacción, bloqueo de cuenta, commit y rollback |
| Fiabilidad | Validación de datos y comunicación de rechazo o error |
| Usabilidad | Menús equivalentes, mensajes y tareas de escritorio en segundo plano |
| Portabilidad | Clientes Java y comunicación SOAP; requiere entornos compatibles |
| Mantenibilidad | Separación de capas y dependencias declaradas en Maven |
| Seguridad | PBKDF2 y token de sesión de 20 minutos; HTTPS, distribución de sesiones y permisos por operación pendientes |
| Disponibilidad | Depende de Payara, MySQL y red; no existe un compromiso cuantificado |
| Observabilidad | Registro de errores en el servidor; sin evidencia de un sistema central de métricas o auditoría de sesiones |

: Tabla 37. Atributos de calidad y mecanismos asociados

# Requisitos de rendimiento

La línea base no define un tiempo máximo de respuesta, una cantidad de usuarios simultáneos o un tamaño de historial garantizado. Este documento no presenta valores medidos ni compromisos de disponibilidad. El requisito de rendimiento consiste en preservar el comportamiento funcional bajo una carga definida y registrar las condiciones necesarias para fijar umbrales.

La consulta devuelve toda la lista de movimientos de una cuenta y no ofrece paginación. Su tiempo y consumo de memoria dependen del tamaño del historial y de la serialización SOAP. Las transferencias bloquean dos cuentas y deben evaluarse con concurrencia y claves repetidas. Un crecimiento importante de datos debe acompañarse de una evaluación del contrato y de la presentación de resultados.

El depósito bloquea la fila de la cuenta hasta confirmar o revertir. La concurrencia sobre una misma cuenta puede aumentar la espera. La derivación de contraseña también consume recursos de CPU debido a las 600 000 iteraciones. Estos mecanismos deben incluirse en el dimensionamiento; no deben eliminarse para aparentar menor latencia.

| ID | Escenario que debe evaluarse | Evidencia requerida |
|---|---|---|
| REND-01 | Crear sesión con credencial válida e inválida | Duración, carga de CPU y respuesta obtenida |
| REND-02 | Consulta con historial vacío, habitual y extenso | Cantidad de registros, tamaño de mensaje, duración y memoria |
| REND-03 | Depósitos sobre cuentas distintas | Resultados, latencia y consistencia final |
| REND-04 | Depósitos concurrentes sobre la misma cuenta | Espera por bloqueo, confirmaciones y saldo/movimientos finales |
| REND-05 | Fallo de red o base durante una operación | Tiempo observado hasta el mensaje y estado real de la transacción |

: Tabla 38. Escenarios para evaluar el rendimiento

Los volúmenes, concurrencia, duración y umbrales de aceptación se deben acordar antes de ejecutar una evaluación. El informe debe registrar versiones, recursos, ubicación de los componentes y cantidad de datos. La ausencia de una medición no permite afirmar disponibilidad continua ni capacidad de producción.

## Requisitos de interfaces externas

### Interfaces de usuario

Las cuatro interfaces deben mostrar claramente la cuenta elegida y los datos monetarios antes de enviar una operación. La consulta distingue una lista vacía de un error de sesión o comunicación. En un depósito, el mensaje de confirmación aparece solo después de recibir resultado positivo; si se pierde la respuesta, el operador consulta movimientos antes de decidir si repite.

En una transferencia, origen, destino e importe deben poder revisarse antes de confirmar. Para repetir una solicitud con resultado incierto, el consumidor conserva y reutiliza la misma clave UUID junto con los mismos datos. La vista no debe generar una clave nueva para ese reintento.

### Interfaces de hardware

La interfaz humana utiliza pantalla y teclado; el escritorio admite ratón y el cliente móvil puede usar controles táctiles. No se presupone integración con lectores de tarjetas, impresoras financieras o cajeros automáticos. La revisión de legibilidad y captura debe considerar el tamaño de pantalla de cada plataforma.

### Interfaces de software

El servidor requiere Java 25, Jakarta EE 11, Payara Server Community 7, MySQL y MySQL Connector/J. Los clientes de consola y escritorio usan Java y Metro; el cliente móvil se construye para Android y el web utiliza componentes Java web. Cada consumidor debe conservar un adaptador compatible con el WSDL del servidor.

La capa de servicio valida token y reglas bancarias; el DAO concentra las consultas parametrizadas y transacciones. Ningún cliente se conecta directamente a MySQL. El esquema debe incluir las tablas y restricciones requeridas para transferencias.

### Interfaces de comunicación

La evaluación debe considerar el recorrido cliente-servidor y servidor-base. El tamaño del XML, la lista completa de movimientos, los bloqueos de dos cuentas y los fallos de conexión pueden afectar al tiempo total. La política de tiempos de espera debe ser coherente con las operaciones bancarias.


# Trazabilidad y criterios de aceptación {.unnumbered}

| Caso de uso | Objetivo | Requisitos asociados | Componentes |
|---|---|---|---|
| CU-01 Iniciar sesión | OBJ-002 | RF-01, RF-02; RNF-01, RNF-02, RNF-09 | Clientes, WSEureka, EurekaService, DAO y sesiones |
| CU-02 Consultar cuentas | OBJ-003 | RF-02, RF-09; RNF-06, RNF-07 | Adaptadores SOAP y consulta del DAO |
| CU-03 Consultar movimientos | OBJ-003 | RF-02, RF-03, RF-09; RNF-06, RNF-07 | Vistas, controladores, SOAP y consulta del DAO |
| CU-04 Registrar depósito | OBJ-004, OBJ-006 | RF-02, RF-04 a RF-07, RF-09; RNF-03, RNF-04, RNF-07 | Servicio, DAO y tablas Cuenta/Movimiento |
| CU-05 Transferir fondos | OBJ-005 | RF-02, RF-08, RF-09; RNF-03, RNF-04, RNF-06 | Servicio, DAO y tablas Transferencias, Cuenta y Movimiento |
| CU-06 Cerrar sesión | OBJ-006 | RF-09, RF-10; RNF-09 | Controladores de los cuatro clientes y SesionesActivas |

: Tabla 39. Matriz de trazabilidad funcional

La revisión de aceptación debe comprobar criterios y conservar evidencia del estado inicial, las entradas y el resultado. Para operaciones bancarias se comparan los saldos, contadores y movimientos antes y después; una pantalla por sí sola no demuestra la consistencia transaccional.

| Criterio | Resultado esperado |
|---|---|
| Credencial válida con cliente asociado | crearSesion entrega un token; credenciales inválidas no habilitan operaciones |
| Token ausente, inválido o vencido | Operación protegida rechazada |
| Consulta de cuentas | Solo cuentas activas asociadas al cliente de la sesión |
| Consulta de movimientos | Historial de cuenta activa del cliente, descendente por número |
| Depósito inválido o no autorizado | Rechazo sin cambios en cuenta ni movimiento |
| Depósito válido | Saldo, contador y movimiento tipo 003 confirmados juntos |
| Error antes del commit del depósito | Rollback sin saldo actualizado sin movimiento |
| Transferencia válida | Débito, crédito, contadores y dos movimientos quedan confirmados juntos |
| Repetición de transferencia | Misma clave y datos no duplican la transferencia; clave repetida con otra carga se rechaza |
| Cierre de sesión | Token revocado deja de autorizar operaciones |

: Tabla 40. Criterios generales de aceptación


# Recomendaciones {.unnumbered}

La prioridad de evolución es proteger el transporte con HTTPS para entornos distribuidos, definir permisos por operación y registrar la identidad real del empleado que ejecuta un depósito. Las sesiones en memoria también deben revisarse si se requiere alta disponibilidad o varias instancias.

También se recomienda incorporar idempotencia al depósito, diferenciar errores de negocio e infraestructura mediante respuestas documentadas y definir paginación para historiales grandes. La transferencia ya dispone de clave idempotente UUID; esa capacidad debe preservarse al modificar el contrato o los clientes.

El entorno operativo debe acordar respaldos, recuperación y seguimiento de errores. Antes de establecer objetivos de capacidad o disponibilidad deben ejecutarse evaluaciones con datos y concurrencia representativos. Las decisiones y resultados deberán incorporarse a una nueva versión del ERS.

# Conclusiones {.unnumbered}

EurekaBank expone por SOAP autenticación, consulta de cuentas y movimientos, depósito, transferencia y cierre remoto de sesión. Consola, escritorio, móvil y web comparten el servidor y la persistencia MySQL mediante adaptadores del contrato.

El depósito es la operación de mayor impacto sobre la integridad: valida el importe, comprueba una cuenta activa, bloquea su fila y confirma de forma conjunta el saldo y el movimiento. La especificación detalla sus precondiciones, respuestas y criterios de revisión para evitar interpretar una salida del cliente como evidencia suficiente de consistencia.

La línea base incorpora token temporal y transferencia idempotente, pero mantiene límites: sesiones guardadas en memoria, transporte sin HTTPS predeterminado, empleado fijo en los movimientos, depósito no idempotente, consulta sin paginación y rendimiento sin objetivos medidos. La trazabilidad permite evaluar esas brechas sin atribuir controles inexistentes.

# Bibliografía y fuentes técnicas {.unnumbered}

Las referencias técnicas utilizadas corresponden a los archivos del proyecto identificados en la tabla 5. Se consultaron las fuentes del servidor en 04. SERVIDOR/WS_EurekaBank_SOAP_JAVA y las de los clientes en 04. CLICON/ClienteConsola, 04. CLIESC/ClienteEscritorio, 04. CLIMOV/ClienteMovil y 04. CLIWEB/ClienteWeb.

La definición de entidades y datos iniciales corresponde a los scripts de 03. BDD. Los manifiestos pom.xml proporcionan las versiones y dependencias declaradas; los WSDL incorporados en los clientes proporcionan el contrato utilizado para generar sus adaptadores.

Fecha de revisión documental: 28 de octubre de 2026. Las referencias identifican la procedencia de los requisitos; los criterios de aceptación y rendimiento deberán acompañarse de evidencia propia cuando se ejecute su evaluación.
