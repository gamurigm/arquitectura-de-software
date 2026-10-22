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

EurekaBank necesita ofrecer a sus operadores un acceso común a la consulta de movimientos y al registro de depósitos, conservando los datos de las cuentas en una base de datos central. El sistema del grupo 09 aborda ese caso mediante un servidor Java con servicios SOAP y dos clientes: consola y escritorio. Ambos utilizan el mismo contrato de operaciones; el servidor concentra el acceso a MySQL y la transacción que modifica el saldo.

La situación de partida exige evitar que cada cliente implemente su propia actualización bancaria. Cuando un operador registra un depósito, el sistema debe comprobar la cuenta y el importe, aumentar el saldo y registrar el movimiento como una operación indivisible. Una consulta posterior debe permitir revisar la operación mediante el número de movimiento, la fecha, el tipo, la acción y el importe.

La presente especificación establece la línea base del producto existente. El estado «documentado» significa que el comportamiento se identificó en el código; no representa una aprobación formal del cliente ni un resultado de pruebas de aceptación. Las mejoras que requieren trabajo adicional se identifican expresamente como pendientes.

| Dato | Valor |
|---|---|
| Proyecto | EurekaBank SOAP Java con base de datos |
| Asignatura y grupo | Arquitectura de software, grupo 09 |
| Versión del ERS | 2.0 |
| Fecha de la línea base | 21 de octubre de 2026 |
| Producto documentado | Servidor SOAP, cliente de consola, cliente de escritorio y persistencia MySQL |
| Estado | Especificación alineada al código del repositorio |

: Tabla 1. Control de la línea base documental

# Introducción

La especificación de requerimientos de software describe los servicios que EurekaBank proporciona al operador, las reglas de los depósitos, la información que intercambia y las condiciones técnicas necesarias para ejecutarse. Su finalidad es ofrecer una descripción verificable y común para quienes desarrollan, mantienen, revisan y utilizan la solución.

La arquitectura distribuye la interacción en los clientes y concentra las reglas de servicio y la persistencia en el servidor. Los mensajes SOAP transportan las solicitudes y respuestas; las operaciones JDBC permiten consultar credenciales y movimientos, bloquear una cuenta durante una actualización e insertar el registro del depósito.

El documento se basa en las firmas SOAP, la implementación Java, los manifiestos Maven y los scripts de creación y carga de la base de datos. Mantiene los nombres de las operaciones publicados por el sistema. No atribuye funcionalidades a carpetas vacías ni considera que las tablas disponibles en MySQL impliquen, por sí mismas, servicios expuestos.

# Propósito

El propósito del ERS es especificar de forma ordenada las funciones bancarias de EurekaBank y proporcionar criterios observables para revisar su cumplimiento. También establece las restricciones de diseño, las interfaces externas y los puntos que deben resolverse antes de ampliar la solución.

El documento está dirigido al grupo de desarrollo, al docente que revisa el proyecto, a quienes administran el entorno de ejecución y a los operadores que necesitan comprender el alcance funcional. Para el desarrollo sirve como base de mantenimiento; para la revisión permite relacionar objetivos, requisitos, casos de uso y componentes; para la operación aclara qué resultados puede confirmar el servicio.

Los cambios futuros deberán conservar la trazabilidad de los requisitos. Una modificación de las firmas SOAP, las reglas del depósito, los datos de respuesta o la política de autenticación requiere actualizar esta especificación y revisar su efecto sobre ambos clientes.

# Alcance

El producto permite validar las credenciales de un usuario, consultar movimientos por cuenta, solicitar un depósito y cerrar el flujo local del cliente. La autenticación consulta la tabla Credenciales. La consulta bancaria recupera movimientos y su clasificación. El depósito modifica la cuenta y crea un movimiento dentro de una transacción JDBC.

El alcance comprende el servidor desplegado como WAR en Payara Server Community 7, el acceso a MySQL mediante JDBC, la aplicación Java de consola y la aplicación Java Swing de escritorio. Los clientes envían solicitudes al servicio y presentan sus resultados; el acceso directo a la base de datos corresponde al servidor.

| Incluido en la línea base | Resultado esperado |
|---|---|
| Inicio de sesión | Aceptar o rechazar las credenciales recibidas |
| Menú del cliente | Permitir consultar movimientos, depositar y salir |
| Consulta de movimientos | Presentar los movimientos filtrados por cuenta |
| Depósito | Validar datos y cuenta activa; confirmar saldo y movimiento juntos |
| Manejo de rechazo y comunicación | Informar el resultado sin presentar una operación incierta como confirmada |
| Cierre local | Finalizar el acceso al menú y, en escritorio, regresar al inicio de sesión |

: Tabla 2. Alcance funcional de EurekaBank

Quedan fuera de esta versión las transferencias, retiros, pagos, reversos, apertura y cierre de cuentas, administración de clientes, empleados o usuarios y gestión de permisos por operación. Las carpetas móvil y web no contienen clientes funcionales en esta línea base. La presencia de tablas Usuario, Permiso o LOGSESSION no acredita que existan servicios para administrarlas.

El servidor no emite tokens ni vincula las operaciones de movimientos y depósitos con una sesión autenticada. El control del menú en los clientes es local. Esta limitación debe considerarse al evaluar la seguridad y al definir el alcance de una versión posterior.

# Definiciones, acrónimos y abreviaturas

## Definiciones

| Término | Definición en el sistema |
|---|---|
| Cuenta activa | Registro de Cuenta cuyo estado es ACTIVO y que puede recibir depósitos |
| Depósito | Operación que suma un importe positivo al saldo y crea un movimiento |
| Movimiento | Registro bancario identificado por cuenta y número de movimiento |
| Operador | Persona que utiliza un cliente para ejecutar las funciones disponibles |
| Transacción | Unidad de trabajo de base de datos que confirma o revierte sus cambios |
| Confirmación | Resultado 1 de regDeposito después de la confirmación de la transacción |
| Resultado incierto | Situación en la que el cliente no recibe confirmación y no puede determinar si el depósito se aplicó |
| Sesión local | Estado del cliente que habilita el menú después del inicio de sesión |
| Contrato de servicio | Definición de operaciones, parámetros y respuestas publicada mediante WSDL |
| Línea base | Conjunto de requisitos documentados para una versión determinada |

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
| F6 | EurekaControlador.java y vistas del cliente de escritorio | Validaciones, estado local e interacción Swing |
| F7 | MenuPrincipal.java y controladores del cliente de consola | Inicio de sesión, menú y presentación en terminal |
| F8 | Crea_BD.sql y Carga_Datos.sql, carpeta 03. BDD | Entidades, tipos, restricciones y datos iniciales |
| F9 | pom.xml del servidor y de ambos clientes | Versiones de compilación, dependencias y empaquetado |
| F10 | WSEureka.wsdl y su esquema en los clientes | Enlace con el contrato y generación de adaptadores |

: Tabla 5. Fuentes de requisitos

# Visión general del documento

Las primeras secciones presentan el caso de estudio, el propósito, el alcance y el vocabulario. La descripción general explica la arquitectura, las funciones del producto, los usuarios, el entorno y las dependencias. La especificación desarrolla las interfaces y los requisitos funcionales y no funcionales.

Cada requisito conserva un identificador estable y señala su importancia, estado, objetivo o criterio de aceptación y fuente. Las reglas de negocio precisan las condiciones que deben respetar las operaciones bancarias. La sección de rendimiento define qué debe medirse y qué valores siguen pendientes de acordar, sin presentar estimaciones como mediciones.

La trazabilidad relaciona los requisitos con los cuatro casos de uso principales y los componentes del código. Las recomendaciones y conclusiones identifican las prioridades de evolución y los límites de la versión documentada.

# Descripción general

## Perspectiva del producto

EurekaBank es una solución cliente-servidor con un servicio central SOAP. El cliente presenta formularios o menús, envía parámetros y muestra respuestas. El servicio aplica las validaciones y delega las consultas y actualizaciones al DAO. MySQL conserva las cuentas, los movimientos y las credenciales.

La separación evita que los clientes necesiten conocer sentencias SQL o credenciales de MySQL. El contrato SOAP constituye el punto común entre las interfaces de usuario y la lógica del servidor. Una modificación interna de persistencia no debería alterar a los consumidores mientras se conserve dicho contrato.

**Recorrido de una solicitud:** operador → cliente de consola o escritorio → SOAP/XML → WSEureka → EurekaService → EurekaBankDao → JDBC → MySQL.

| Componente | Responsabilidad |
|---|---|
| Vista del cliente | Capturar entradas y presentar el resultado |
| Controlador del cliente | Coordinar la interacción y las validaciones locales |
| Adaptador SOAP | Invocar el contrato remoto y transformar sus respuestas |
| WSEureka | Publicar login, traerMovimientos y regDeposito |
| EurekaService | Validar entradas y coordinar la operación solicitada |
| EurekaBankDao | Ejecutar SQL parametrizado y la transacción del depósito |
| MySQL | Almacenar registros y aplicar restricciones relacionales |

: Tabla 6. Responsabilidades de la arquitectura

![Ilustración 1. Casos de uso de EurekaBank generados en PowerDesigner](<../03. DIAGRAMAS UML/01_CASOS_DE_USO/GR09_CasosUso_EurekaBank.png>){width=14cm}

El modelo representa un usuario autorizado, cuatro objetivos principales y las funciones incluidas de validación y persistencia. El cierre de sesión representado corresponde al cliente. La asociación del actor con los casos de uso no implica que el servidor compruebe una sesión en cada invocación.

## Funciones del producto

| Función | Entrada | Resultado |
|---|---|---|
| Validar credenciales | Usuario y contraseña | 1 cuando se acepta; -1 cuando se rechaza o se produce un error |
| Consultar movimientos | Identificador de cuenta | Lista ordenada por número descendente; lista vacía cuando no hay datos |
| Registrar depósito | Cuenta e importe decimal | 1 al confirmar; -1 al rechazar o fallar |
| Cerrar sesión local | Acción de salida del operador | Menú cerrado o regreso al inicio de sesión |

: Tabla 7. Funciones del producto

En una consulta, el sistema no modifica el saldo. En un depósito confirmado se actualiza la cuenta y se crea el movimiento de tipo 003. La consulta posterior permite revisar ese registro. Si la cuenta no existe, la consulta vacía no permite distinguir esa situación de una cuenta existente sin movimientos.

## Condiciones del entorno

El servidor y MySQL deben estar disponibles y comunicados. El equipo que ejecuta el cliente necesita un entorno Java compatible, acceso al endpoint y una terminal o un escritorio gráfico, según el cliente seleccionado. Una instalación local puede utilizar localhost; una instalación distribuida debe configurar direcciones accesibles desde cada equipo.

El compilador de los módulos está configurado para Java 25. El servidor utiliza Jakarta EE 11 y se empaqueta como WAR para Payara 7. Los clientes usan Metro 4.0.4 para consumir SOAP. El servidor declara MySQL Connector/J 9.7.0 en Maven; esa dependencia corresponde al proyecto, independientemente de otros controladores disponibles en el equipo.

| Elemento | Condición requerida |
|---|---|
| Compilación | JDK compatible con release 25 y Maven |
| Contenedor del servidor | Payara Server Community 7 |
| Persistencia | MySQL con esquema eurekabank_gr09 y tablas cargadas |
| Conectividad | Cliente a SOAP; servidor a MySQL |
| Escritorio | Entorno gráfico capaz de mostrar ventanas Swing |
| Consola | Terminal y entrada de texto; lectura protegida si System.console está disponible |

: Tabla 8. Condiciones del entorno

## Características de los usuarios

El usuario funcional es el operador que consulta una cuenta o registra un depósito. Debe conocer el identificador de la cuenta, interpretar el historial y comprobar el resultado antes de repetir una solicitud bancaria. La aplicación no define una jerarquía de cajeros, supervisores y administradores.

El responsable técnico configura Payara, MySQL y las variables de entorno. Ese trabajo pertenece a la administración del entorno; no existe una pantalla de administración dentro del alcance funcional.

| Participante | Conocimientos y responsabilidad |
|---|---|
| Operador | Manejo básico de consola o ventanas; selección de cuenta e importe |
| Responsable técnico | Despliegue Java, configuración SOAP y administración del acceso a MySQL |
| Equipo de desarrollo | Contrato SOAP, reglas del dominio, transacciones JDBC y mantenimiento de clientes |

: Tabla 9. Características de los participantes

## Interfaces externas

Las interfaces externas se concentran en la interacción humana, el servicio SOAP y la base de datos. El sistema no integra pasarelas de pago, notificaciones, servicios de correo o transferencias a otras entidades.

| Interfaz | Descripción del intercambio |
|---|---|
| Operador con consola | Entrada por teclado y salida tabular de movimientos |
| Operador con escritorio | Formularios de credenciales, consulta, depósito y mensajes |
| Cliente con servidor | Solicitudes y respuestas SOAP descritas por WSDL |
| Servidor con MySQL | Consultas parametrizadas y transacciones por JDBC |

: Tabla 10. Resumen de interfaces externas

## Restricciones

El servidor conserva las tres operaciones públicas actuales. Los clientes dependen de sus nombres, parámetros y tipos de respuesta. La persistencia bancaria requiere MySQL y los objetos definidos en los scripts; no se documenta una implementación alternativa en memoria o mediante otro motor.

El depósito usa el empleado 0001 definido en el servicio y el tipo de movimiento 003 definido en el DAO. El usuario del inicio de sesión no se envía como empleado del depósito. La interpretación de trazabilidad de ese código debe respetar esta limitación.

La comunicación predeterminada utiliza HTTP local y la URL JDBC predeterminada desactiva SSL. La línea base no acredita cifrado en tránsito. El servidor no ofrece idempotencia ni un identificador de solicitud para evitar la repetición de depósitos.

## Suposiciones y dependencias

Se supone que el esquema está creado, que las credenciales tienen el formato admitido por PasswordHasher y que existen los registros relacionados necesarios para insertar movimientos. La cuenta debe estar activa y los códigos del empleado y del tipo de movimiento deben ser válidos en la base de datos.

La operación depende de MySQL, de su controlador JDBC, de las variables de conexión y de la conectividad. Si falta una dependencia, el servicio debe comunicar el fallo conforme a su contrato y conservar la atomicidad de las actualizaciones.

Una ampliación a clientes web o móvil requiere implementación y revisión de su contrato de interacción. La existencia de sus carpetas no se considera evidencia de una aplicación disponible.

# Especificación de requerimientos

## Requisitos comunes de las interfaces

### Interfaces de usuario

La consola debe solicitar credenciales antes de mostrar el menú. Una autenticación rechazada finaliza ese flujo; el operador puede iniciar otra ejecución. Cuando hay una consola del sistema, se utiliza lectura de contraseña protegida; cuando no existe, se emplea entrada estándar y no se acredita ocultamiento de caracteres.

El escritorio debe presentar una ventana de inicio de sesión y, después de una aceptación, un menú con consulta, depósito y salida. Los formularios de consulta y depósito deben mostrar mensajes de validación o comunicación. Mientras una tarea se procesa, deben deshabilitar los controles de envío que podrían duplicarla.

| Pantalla o vista | Información y acciones |
|---|---|
| Inicio de sesión | Usuario, contraseña, acción de ingreso y mensaje de rechazo |
| Menú | Usuario local y acceso a consulta, depósito y cierre |
| Consulta | Cuenta, acción de búsqueda y tabla de movimientos |
| Depósito | Cuenta, importe y mensaje de confirmación o rechazo |
| Terminal | Menú equivalente y resultados tabulares |

: Tabla 11. Requisitos de interacción de usuario

### Interfaces de hardware

El sistema no controla dispositivos bancarios, lectores de tarjetas, impresoras de comprobantes o cajeros automáticos. Utiliza teclado y pantalla; el cliente de escritorio también permite interacción con un dispositivo apuntador.

No se han establecido mínimos medidos de memoria, procesador, resolución o almacenamiento para una carga bancaria determinada. El dimensionamiento debe considerar Java, Payara, MySQL, los clientes y el volumen de datos. No corresponde presentar una cifra de hardware como requisito validado sin realizar esa evaluación.

### Interfaces de software

| Componente | Especificación declarada |
|---|---|
| Servidor | Java 25, Jakarta EE 11, JAX-WS y paquete WAR |
| Contenedor | Payara Server Community 7 |
| Clientes | Java 25, Maven y Metro 4.0.4 |
| Escritorio | Java Swing y SwingWorker |
| Base de datos | MySQL; esquema eurekabank_gr09 |
| Acceso a datos | JDBC y MySQL Connector/J 9.7.0 declarado en Maven |
| Contraseñas | PBKDF2-HMAC-SHA-256 con formato versionado por prefijo |

: Tabla 12. Interfaces de software

Los adaptadores de los clientes se generan a partir de su WSDL incorporado. Un cambio de contrato exige regenerar o revisar los adaptadores y comprobar los consumidores. El entorno de desarrollo puede variar mientras respete los manifiestos y las convenciones del repositorio; no se establece un IDE obligatorio.

### Interfaces de comunicación

El namespace publicado es http://ws.monster.edu.ec/. El WSDL local predeterminado es http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka?wsdl. El cliente de escritorio admite configuración del endpoint mediante EUREKABANK_SOAP_URL.

| Operación SOAP | Parámetros | Respuesta y errores |
|---|---|---|
| login | usuario: texto; contrasena: texto | Entero 1 o -1; los errores internos también devuelven -1 |
| traerMovimientos | cuenta: texto | Lista de Movimiento; error SOAP cuando falla la consulta |
| regDeposito | cuenta: texto; importe: decimal | Entero 1 al confirmar o -1 al rechazar o fallar |

: Tabla 13. Contrato de operaciones SOAP

La contraseña forma parte de la solicitud de inicio de sesión. No se transmite un token en las operaciones posteriores. Un error de transporte no equivale a un resultado funcional de rechazo; esta distinción es especialmente importante en el depósito.

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
| RF-01 | Validar credenciales | Alta | CU-01 |
| RF-02 | Habilitar el menú local | Alta | CU-01 |
| RF-03 | Consultar y presentar movimientos | Alta | CU-02 |
| RF-04 | Validar los datos del depósito | Alta | CU-03 |
| RF-05 | Verificar que la cuenta esté activa | Alta | CU-03 |
| RF-06 | Confirmar saldo y movimiento de forma atómica | Alta | CU-03 |
| RF-07 | Revertir el depósito ante error | Alta | CU-03 |
| RF-08 | Informar resultados y fallos | Media | CU-01, CU-02, CU-03 |
| RF-09 | Cerrar el acceso local del cliente | Media | CU-04 |

: Tabla 16. Catálogo de requisitos funcionales

### Especificación detallada

Las fichas separan los resultados requeridos y sus condiciones de aceptación. La importancia expresa el efecto funcional: un requisito alto afecta al acceso, la consulta o la integridad del depósito. El estado documentado no acredita una aceptación formal ni una ejecución de pruebas.

#### RF-01 Validar credenciales {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-01 |
| Descripción | El sistema deberá recibir usuario y contraseña y consultar la credencial almacenada. Deberá devolver 1 cuando la verificación sea satisfactoria y -1 cuando los datos se rechacen o se produzca un error en login. |
| Objetivo | OBJ-002 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | El operador introduce las credenciales en un cliente conectado al servicio. |
| Entradas y validaciones | Usuario no nulo, no blanco y de hasta 30 caracteres recibidos; contraseña no nula, no vacía y de hasta 1024 caracteres. El servicio elimina espacios exteriores del usuario antes de consultar. |
| Procesamiento y salida | El servidor verifica el hash PBKDF2 almacenado; los clientes interpretan el entero recibido. |
| Criterio de aceptación | Una credencial válida devuelve 1. Un usuario desconocido, contraseña incorrecta o entrada fuera de límites devuelve -1. No se entrega un token de sesión. |
| Trazabilidad | CU-01; F1, F2, F3 y F4. |
| Comentarios | El mismo -1 representa rechazo y error interno; el consumidor no puede distinguirlos mediante ese valor. |

: Tabla 17. Ficha de RF-01 validar credenciales

#### RF-02 Habilitar el menú local {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-02 |
| Descripción | Después de una autenticación aceptada, el cliente deberá presentar las funciones de consulta de movimientos, depósito y salida. |
| Objetivo | OBJ-002 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | RF-01 devuelve 1 en el cliente. |
| Entradas y validaciones | Estado local de autenticación y selección de una opción del menú. |
| Procesamiento y salida | El escritorio abre el menú y conserva su estado autenticado en el controlador. La consola entra en su bucle de opciones. |
| Criterio de aceptación | Con aceptación se accede al menú. Con rechazo no se entra. En escritorio una operación del controlador sin estado autenticado produce una indicación de iniciar sesión. |
| Trazabilidad | CU-01; F6 y F7. |
| Comentarios | Esta condición controla el cliente. El servidor no valida ese estado al recibir movimientos o depósitos. |

: Tabla 18. Ficha de RF-02 habilitar el menú local

#### RF-03 Consultar y presentar movimientos {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-03 |
| Descripción | El sistema deberá recuperar los movimientos de una cuenta y presentarlos ordenados por número de movimiento descendente, sin modificar datos bancarios. |
| Objetivo | OBJ-003 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | El operador accede al flujo local de consulta. |
| Entradas y validaciones | Cuenta no nula, no blanca y de hasta ocho caracteres después de retirar espacios exteriores. El escritorio exige de uno a ocho dígitos. |
| Procesamiento y salida | La respuesta contiene cuenta, número, fecha, tipo, acción e importe. La consulta relaciona Movimiento con TipoMovimiento. |
| Criterio de aceptación | Los registros devueltos pertenecen a la cuenta solicitada y mantienen orden descendente. Cuando no hay registros se presenta lista vacía. Un fallo SQL se comunica como error SOAP. |
| Trazabilidad | CU-02; F1, F2, F3, F6 y F7. |
| Comentarios | Una lista vacía no distingue cuenta inexistente de cuenta sin movimientos. No hay paginación en el contrato. |

: Tabla 19. Ficha de RF-03 consultar y presentar movimientos

#### RF-04 Validar los datos del depósito {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-04 |
| Descripción | El sistema deberá rechazar una solicitud de depósito con cuenta vacía o demasiado larga, importe ausente, importe no positivo o valor que no pueda representarse exactamente con dos decimales y precisión máxima de doce dígitos. |
| Objetivo | OBJ-004 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | El operador abre el flujo de depósito y captura la cuenta y el importe. |
| Entradas y validaciones | Cuenta de hasta ocho caracteres; importe decimal. El escritorio exige cuenta numérica y admite coma o punto como separador de entrada. |
| Procesamiento y salida | El servidor normaliza el importe a escala dos sin redondear valores que perderían precisión. |
| Criterio de aceptación | Un importe 25,50 se admite como entrada monetaria válida. Cero, valores negativos o 25,501 se rechazan. El límite del importe normalizado es 9.999.999.999,99. |
| Trazabilidad | CU-03; F2 y F6. |
| Comentarios | Un valor con decimales adicionales iguales a cero puede convertirse exactamente a escala dos en el servidor; el formulario de escritorio limita la escritura a dos decimales. |

: Tabla 20. Ficha de RF-04 validar los datos del depósito

#### RF-05 Verificar que la cuenta esté activa {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-05 |
| Descripción | Antes de modificar un saldo, el servidor deberá localizar la cuenta con estado ACTIVO y bloquear su fila durante la transacción. |
| Objetivo | OBJ-004 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | Los parámetros del depósito superaron RF-04. |
| Entradas y validaciones | Identificador de cuenta; estado de Cuenta en MySQL. |
| Procesamiento y salida | El DAO ejecuta la búsqueda de la cuenta activa con FOR UPDATE. Una cuenta no encontrada o no activa provoca error y rechazo. |
| Criterio de aceptación | Una cuenta existente y ACTIVO puede continuar. Una cuenta inexistente, ANULADO o CANCELADO no genera un depósito confirmado ni un nuevo movimiento. |
| Trazabilidad | CU-03; F3 y F8. |
| Comentarios | La consulta del depósito no permite al cliente cambiar el estado de la cuenta. |

: Tabla 21. Ficha de RF-05 verificar que la cuenta esté activa

#### RF-06 Confirmar saldo y movimiento de forma atómica {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-06 |
| Descripción | El servidor deberá sumar el importe al saldo, incrementar el contador de movimientos e insertar el movimiento de depósito dentro de una única transacción. |
| Objetivo | OBJ-004 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | RF-04 y RF-05 se cumplen y las relaciones requeridas existen en MySQL. |
| Entradas y validaciones | Cuenta, importe, saldo anterior, empleado 0001 y tipo de movimiento 003. |
| Procesamiento y salida | El siguiente número se obtiene como máximo existente más uno por cuenta. El movimiento usa la fecha de MySQL. La transacción se confirma después de completar las escrituras. |
| Criterio de aceptación | Después de un depósito confirmado, saldo nuevo = saldo anterior + importe; el contador aumenta en uno y existe un movimiento por ese importe. regDeposito devuelve 1 después de commit. |
| Trazabilidad | CU-03; F1, F2, F3 y F8. |
| Comentarios | El contrato no recibe empleado ni moneda. El importe se interpreta en la cuenta existente; no se realiza conversión monetaria. |

: Tabla 22. Ficha de RF-06 confirmar saldo y movimiento de forma atómica

#### RF-07 Revertir el depósito ante error {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-07 |
| Descripción | Ante un error SQL o de ejecución antes de confirmar, el servidor deberá intentar revertir la transacción y comunicar que el depósito no se confirmó. |
| Objetivo | OBJ-004 |
| Importancia | Alta |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | La transacción del depósito se inició. |
| Entradas y validaciones | Excepciones durante bloqueo, actualización del saldo, generación o inserción del movimiento. |
| Procesamiento y salida | El DAO ejecuta rollback y propaga el error; la operación SOAP transforma el fallo en -1. |
| Criterio de aceptación | Si falla la inserción del movimiento, no deberá permanecer una actualización parcial del saldo o del contador. Las restricciones de precisión o integridad que fallen deben impedir la confirmación. |
| Trazabilidad | CU-03; F1 y F3. |
| Comentarios | Si también falla rollback, el error adicional se conserva en la excepción. La atención de un fallo de infraestructura requiere revisión del estado real de la base. |

: Tabla 23. Ficha de RF-07 revertir el depósito ante error

#### RF-08 Informar resultados y fallos {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-08 |
| Descripción | El cliente deberá presentar un mensaje de aceptación, ausencia de datos, rechazo o fallo de comunicación que corresponda al resultado de la operación. |
| Objetivo | OBJ-005 |
| Importancia | Media |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | Se ha enviado una solicitud o se ha detectado una entrada inválida. |
| Entradas y validaciones | Enteros de resultado, lista de movimientos, errores SOAP y validaciones locales. |
| Procesamiento y salida | El escritorio muestra Procesando durante la tarea y un mensaje final. La consola presenta el resultado por salida de texto. |
| Criterio de aceptación | El valor 1 de depósito produce confirmación. El escritorio recomienda consultar movimientos antes de repetir un depósito rechazado o de resultado incierto. Un error de consulta no se presenta como una lista obtenida correctamente. |
| Trazabilidad | CU-01, CU-02 y CU-03; F6 y F7. |
| Comentarios | El mensaje de consola para un depósito fallido es genérico. Ningún cliente dispone de un identificador remoto que permita deduplicar una repetición. |

: Tabla 24. Ficha de RF-08 informar resultados y fallos

#### RF-09 Cerrar el acceso local del cliente {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RF-09 |
| Descripción | El cliente deberá ofrecer una opción para terminar el flujo local del operador. |
| Objetivo | OBJ-002 |
| Importancia | Media |
| Estado | Documentado en código; aceptación formal por verificar |
| Estabilidad | Sujeto a cambios controlados del contrato y reglas bancarias |
| Precondición | El menú está abierto después de una autenticación aceptada. |
| Entradas y validaciones | Acción Cerrar sesión o salida del menú. |
| Procesamiento y salida | El escritorio elimina el estado local, cierra el menú y vuelve a mostrar el inicio de sesión. La consola abandona el menú y finaliza su flujo. |
| Criterio de aceptación | Después de cerrar en escritorio, el controlador exige nuevamente iniciar sesión para consultar o depositar. La consola deja de presentar opciones de operación. |
| Trazabilidad | CU-04; F6 y F7. |
| Comentarios | No existe operación SOAP de cierre de sesión ni invalidación remota de token. |

: Tabla 25. Ficha de RF-09 cerrar el acceso local del cliente

### Reglas de negocio

| ID | Regla |
|---|---|
| RN-01 | Solo se registra el depósito cuando la cuenta existe y está ACTIVO |
| RN-02 | El importe es positivo y representable exactamente en escala dos |
| RN-03 | Saldo, contador e inserción del movimiento se confirman juntos |
| RN-04 | El movimiento de depósito utiliza el tipo 003 y empleado 0001 |
| RN-05 | La identidad del usuario de login no se propaga como empleado del movimiento |
| RN-06 | El número de movimiento se determina por cuenta bajo el bloqueo del depósito |
| RN-07 | Una confirmación depende de commit; la pérdida de respuesta puede dejar un resultado incierto |
| RN-08 | Una consulta recupera todos los movimientos asociados; no altera saldo ni estado |

: Tabla 26. Reglas de negocio del servicio

La consulta no garantiza una hora de ejecución bancaria: el depósito inserta CURRENT_DATE y el DAO recupera la fecha como fecha SQL. El formato que muestre un cliente no debe interpretarse como evidencia de que el servicio conserva una marca temporal de precisión superior.

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
| Criterio de revisión | Consola y escritorio utilizan adaptadores generados del contrato incorporado. Un cambio incompatible debe revisarse en ambos consumidores. |
| Fuente y observaciones | F1, F9 y F10. No se documenta una API REST alternativa. |

: Tabla 33. Ficha de RNF-06 interoperabilidad soap

#### RNF-07 Tratamiento de fallos {.unnumbered}

| Campo | Especificación |
|---|---|
| Identificador | RNF-07 |
| Descripción | El servidor deberá registrar los errores internos y comunicar a los clientes los resultados establecidos por cada operación sin presentar un depósito fallido como confirmado. |
| Importancia | Alta |
| Estado | Documentado |
| Criterio de revisión | login y regDeposito devuelven -1 ante las excepciones tratadas; traerMovimientos genera error SOAP. El cliente presenta mensajes y el depósito conserva la regla transaccional. |
| Fuente y observaciones | F1, F3, F6 y F7. Los códigos funcionales actuales no distinguen todas las causas del rechazo. |

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
| Descripción | Para una exposición fuera del entorno controlado se deberá definir protección de transporte y autorización en las operaciones bancarias. La versión actual no demuestra ambos controles. |
| Importancia | Alta |
| Estado | Pendiente de definición e implementación |
| Criterio de revisión | Antes de esa exposición debe existir un criterio acordado para cifrado, autenticación de cada invocación y permisos, acompañado de evidencia de su cumplimiento. |
| Fuente y observaciones | F1 y F5. HTTP local, URL JDBC con SSL desactivado y ausencia de token son límites de la línea base; no deben describirse como controles ya implementados. |

: Tabla 36. Ficha de RNF-09 confidencialidad y autorización del servicio

## Otros requerimientos

### Restricciones de diseño

La capa SOAP debe mantenerse como fachada del contrato, el servicio como lugar de validación y el DAO como responsable de persistencia. Una nueva operación no debe permitir a los clientes escribir directamente en la base. Las consultas deben conservar el uso de parámetros preparados para los valores del usuario.

La política de sesión requiere una decisión de diseño explícita. Agregar una pantalla de inicio de sesión no resuelve la autorización remota; una futura solución debe identificar cada solicitud bancaria y definir su relación con el usuario. También debe decidirse cómo sustituir el empleado fijo del depósito y cómo registrar la identidad efectiva del operador.

Una futura idempotencia debe diseñarse en el servicio y la persistencia. Deshabilitar un botón en Swing reduce duplicaciones en una ventana, pero no impide repeticiones por reintentos, ejecución en otro cliente o pérdida de respuesta.

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
| Seguridad | Verificación PBKDF2 y configuración externa de credenciales; autorización y transporte pendientes |
| Disponibilidad | Depende de Payara, MySQL y red; no existe un compromiso cuantificado |
| Observabilidad | Registro de errores en el servidor; sin evidencia de un sistema central de métricas o auditoría de sesiones |

: Tabla 37. Atributos de calidad y mecanismos asociados

# Requisitos de rendimiento

La línea base no define un tiempo máximo de respuesta, una cantidad de usuarios simultáneos o un tamaño de historial garantizado. Este documento no presenta valores medidos ni compromisos de disponibilidad. El requisito de rendimiento consiste en preservar el comportamiento funcional bajo una carga definida y registrar las condiciones necesarias para fijar umbrales.

La consulta devuelve toda la lista de movimientos de una cuenta y no ofrece paginación. Su tiempo y consumo de memoria dependen del tamaño del historial y de la serialización SOAP. Un crecimiento importante de datos debe acompañarse de una evaluación del contrato y de la presentación de resultados.

El depósito bloquea la fila de la cuenta hasta confirmar o revertir. La concurrencia sobre una misma cuenta puede aumentar la espera. La derivación de contraseña también consume recursos de CPU debido a las 600 000 iteraciones. Estos mecanismos deben incluirse en el dimensionamiento; no deben eliminarse para aparentar menor latencia.

| ID | Escenario que debe evaluarse | Evidencia requerida |
|---|---|---|
| REND-01 | Inicio de sesión con credencial válida e inválida | Duración, carga de CPU y respuesta obtenida |
| REND-02 | Consulta con historial vacío, habitual y extenso | Cantidad de registros, tamaño de mensaje, duración y memoria |
| REND-03 | Depósitos sobre cuentas distintas | Resultados, latencia y consistencia final |
| REND-04 | Depósitos concurrentes sobre la misma cuenta | Espera por bloqueo, confirmaciones y saldo/movimientos finales |
| REND-05 | Fallo de red o base durante una operación | Tiempo observado hasta el mensaje y estado real de la transacción |

: Tabla 38. Escenarios para evaluar el rendimiento

Los volúmenes, concurrencia, duración y umbrales de aceptación se deben acordar antes de ejecutar una evaluación. El informe debe registrar versiones, recursos, ubicación de los componentes y cantidad de datos. La ausencia de una medición no permite afirmar disponibilidad continua ni capacidad de producción.

## Requisitos de interfaces externas

### Interfaces de usuario

La consulta debe presentar un resultado legible con las seis propiedades de cada movimiento. Un historial grande puede afectar al tiempo de carga; el cliente debe conservar la distinción entre espera, lista vacía y error. En escritorio, el procesamiento remoto debe continuar fuera del hilo de eventos.

En depósito, el mensaje de confirmación solo se presenta después del resultado positivo. Si se pierde la respuesta, el operador debe revisar movimientos antes de repetir. La documentación de operación debe explicar que la consulta puede ayudar a revisar el resultado, pero que sin identificador de solicitud no siempre permite atribuir inequívocamente un movimiento a un intento.

### Interfaces de hardware

La evaluación debe identificar si servidor, base y cliente comparten equipo o están distribuidos. Deben registrarse CPU, memoria, almacenamiento y condiciones de red. Comparar resultados de entornos distintos exige conservar esa información.

La interfaz humana utiliza pantalla y teclado; el escritorio admite ratón. No se presupone pantalla táctil ni periféricos financieros. Un cambio de dispositivo necesita revisar legibilidad, captura de datos y operación de los formularios.

### Interfaces de software

Los módulos deben utilizar versiones compatibles con sus manifiestos. El servidor requiere Payara 7 y MySQL; los consumidores requieren los adaptadores SOAP correspondientes. Una diferencia entre el WSDL incorporado y el contrato desplegado puede ocasionar fallos y debe revisarse antes de atribuir un problema al rendimiento.

La consulta JDBC y el ciclo de vida de las conexiones forman parte del recorrido de una solicitud. La clase de conexión crea conexiones con DriverManager; el código revisado no acredita un pool JDBC gestionado por la aplicación. No se debe declarar su existencia como optimización implementada.

### Interfaces de comunicación

La evaluación debe considerar el recorrido cliente-servidor y servidor-base. No basta medir la interacción local de un formulario. El tamaño del XML, la lista completa de movimientos y los fallos de conexión pueden afectar al tiempo total.

La política de tiempos de espera y reintentos debe establecerse de forma coherente con las operaciones bancarias. Un reintento automático de depósito requiere resolver antes la idempotencia; la versión actual no ofrece ese mecanismo.

# Trazabilidad y criterios de aceptación {.unnumbered}

| Caso de uso | Objetivo | Requisitos asociados | Componentes |
|---|---|---|---|
| CU-01 Iniciar sesión | OBJ-002 | RF-01, RF-02; RNF-01, RNF-02, RNF-07 | Clientes, WSEureka, EurekaService y DAO |
| CU-02 Consultar movimientos | OBJ-003 | RF-03, RF-08; RNF-05, RNF-06, RNF-07 | Vistas, adaptadores SOAP y consulta del DAO |
| CU-03 Registrar depósito | OBJ-004, OBJ-005 | RF-04 a RF-08; RNF-03, RNF-04, RNF-07 | Servicio, DAO y tablas Cuenta/Movimiento |
| CU-04 Cerrar sesión local | OBJ-002 | RF-09 | Controlador y menú del cliente |

: Tabla 39. Matriz de trazabilidad funcional

La revisión de aceptación debe comprobar cada criterio de las fichas y conservar evidencia del estado inicial, las entradas y el resultado. Para un depósito se debe contrastar el saldo anterior y posterior, el contador y el movimiento registrado. Una pantalla de éxito por sí sola no acredita la integridad de la operación.

| Criterio | Resultado esperado |
|---|---|
| Credencial válida o inválida | 1 o -1 conforme a la verificación; acceso local coherente |
| Cuenta consultada con datos | Movimientos de esa cuenta en orden descendente |
| Consulta sin registros | Lista vacía sin modificación de saldo |
| Importe inválido | Rechazo sin depósito confirmado |
| Cuenta no activa | Rechazo sin actualización bancaria |
| Depósito válido | Saldo y contador actualizados, un movimiento y confirmación |
| Error antes de commit | Reversión de las escrituras de la transacción |
| Pérdida de respuesta | Mensaje de resultado incierto y revisión previa a cualquier repetición |
| Cierre local | Finalización del menú o regreso al ingreso en escritorio |

: Tabla 40. Criterios generales de aceptación

# Recomendaciones {.unnumbered}

La prioridad de evolución es vincular las operaciones bancarias a una identidad autenticada en el servidor y definir el transporte protegido para el entorno de destino. El diseño debe incluir permisos por operación y la identidad real del empleado que registra el depósito, en lugar de atribuir todos los movimientos al código fijo.

También se recomienda incorporar un identificador de solicitud para resolver reintentos seguros, diferenciar errores de negocio y de infraestructura mediante resultados documentados y definir una política de consulta para historiales grandes. Estas funciones requieren cambios de contrato, implementación y revisión de los clientes; no forman parte de los servicios ya documentados.

El entorno operativo debe acordar respaldos, recuperación y seguimiento de errores. Antes de establecer objetivos de capacidad o disponibilidad deben ejecutarse evaluaciones con datos y concurrencia representativos. Las decisiones y resultados deberán incorporarse a una nueva versión del ERS.

# Conclusiones {.unnumbered}

EurekaBank centraliza tres operaciones SOAP y permite utilizarlas desde consola y escritorio. La separación entre clientes, fachada SOAP, servicio y DAO organiza la interacción y concentra la persistencia en el servidor.

El depósito es la operación de mayor impacto sobre la integridad: valida el importe, comprueba una cuenta activa, bloquea su fila y confirma de forma conjunta el saldo y el movimiento. La especificación detalla sus precondiciones, respuestas y criterios de revisión para evitar interpretar una salida del cliente como evidencia suficiente de consistencia.

La línea base conserva límites que deben ser visibles en cualquier evaluación: autenticación sin token remoto, empleado fijo, ausencia de idempotencia, consulta sin paginación y objetivos de rendimiento todavía sin cuantificar. La trazabilidad permite ampliar estas capacidades sin atribuir a la versión actual controles o funciones que todavía no implementa.

# Bibliografía y fuentes técnicas {.unnumbered}

Las referencias técnicas utilizadas corresponden a los archivos del proyecto identificados en la tabla 5. Se consultaron las fuentes del servidor en 04. SERVIDOR/WS_EurekaBank_SOAP_JAVA, las del cliente de consola en 04. CLICON/ClienteConsola y las del cliente de escritorio en 04. CLIESC/ClienteEscritorio.

La definición de entidades y datos iniciales corresponde a los scripts de 03. BDD. Los manifiestos pom.xml proporcionan las versiones y dependencias declaradas; los WSDL incorporados en los clientes proporcionan el contrato utilizado para generar sus adaptadores.

Fecha de revisión documental: 21 de octubre de 2026. Las referencias identifican la procedencia de los requisitos; los criterios de aceptación y rendimiento deberán acompañarse de evidencia propia cuando se ejecute su evaluación.
