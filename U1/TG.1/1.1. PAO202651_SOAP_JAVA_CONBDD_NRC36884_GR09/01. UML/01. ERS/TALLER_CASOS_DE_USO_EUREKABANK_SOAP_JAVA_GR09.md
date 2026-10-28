---
title: "Especificación de casos de uso"
subtitle: "EurekaBank SOAP Java con base de datos — Grupo 09"
author: "Grupo 09"
date: "28 de octubre de 2026"
lang: es
---

# Control del documento

| Campo | Valor |
|---|---|
| Proyecto | EurekaBank SOAP Java con base de datos |
| Grupo | 09 |
| Versión | 2.1 |
| Estado | Especificación alineada al código; aceptación formal por verificar |
| Documento relacionado | ERS_EUREKABANK_SOAP_JAVA_GR09 |

# Introducción

Este documento describe los objetivos de los usuarios y sus flujos en EurekaBank. El sistema cuenta con un servidor Java SOAP, persistencia MySQL y cuatro clientes: consola, escritorio, Android y web. Los clientes consumen las mismas operaciones; el servidor valida la sesión y concentra las reglas de negocio y las transacciones bancarias.

# Alcance y actor

El modelo contempla iniciar sesión, consultar cuentas, consultar movimientos, registrar depósitos, transferir fondos y cerrar la sesión remota. El actor principal es Usuario, quien opera cualquiera de los cuatro clientes.

Payara, SOAP y MySQL son componentes técnicos, no actores humanos. Los casos protegidos requieren un token vigente. La operación login, publicada por compatibilidad, valida credenciales sin crear token y no habilita operaciones bancarias.

# Diagrama general de casos de uso

![Ilustración 1. Diagrama de casos de uso EurekaBank, grupo 09](<../03. DIAGRAMAS UML/01_CASOS_DE_USO/GR09_CasosUso_EurekaBank.png>){width=6.6in}

## Relaciones del modelo

- El actor Usuario participa en los seis objetivos principales.
- <<include>> apunta desde el caso que requiere el comportamiento hacia el caso incluido.
- CU-01 incluye CU-07 Validar credenciales.
- CU-02 a CU-06 exigen un token vigente como precondición; el servidor verifica ese token en cada operación protegida.
- CU-03 incluye CU-08 Validar cuenta.
- CU-04 incluye CU-08 Validar cuenta, CU-09 Validar datos del depósito y CU-10 Aplicar depósito.
- CU-05 incluye CU-08 Validar cuenta. La validación de solicitud, idempotencia y transacción también se especifican en el flujo de transferencia.
- CU-06 exige token vigente, lo revoca en el servidor y elimina la copia local del cliente.

## Resumen de casos

| ID | Caso de uso | Objetivo |
|---|---|---|
| CU-01 | Iniciar sesión | Validar credenciales y obtener un token temporal. |
| CU-02 | Consultar cuentas | Presentar las cuentas activas asociadas al cliente autenticado. |
| CU-03 | Consultar movimientos | Consultar y mostrar el historial de una cuenta propia activa. |
| CU-04 | Registrar depósito | Acreditar un importe válido y registrar el movimiento en una transacción. |
| CU-05 | Transferir fondos | Debitar origen y acreditar destino de forma atómica e idempotente. |
| CU-06 | Cerrar sesión | Revocar el token y finalizar el acceso del cliente. |
| CU-07 | Validar credenciales | Verificar contraseña y asociación del usuario con un cliente. |
| CU-08 | Validar cuenta | Comprobar formato, estado activo y restricciones de titularidad. |
| CU-09 | Validar datos del depósito | Exigir importe positivo, exacto a centavos y dentro de precisión. |
| CU-10 | Aplicar depósito | Actualizar saldo y registrar movimiento en una sola transacción. |

\newpage

# Especificaciones detalladas

## CU-01 — Iniciar sesión

| Campo | Descripción |
|---|---|
| Actor principal | Usuario |
| Propósito | Obtener una sesión remota asociada a la identidad del usuario. |
| Disparador | El usuario abre un cliente e ingresa sus credenciales. |
| Precondiciones | El endpoint SOAP y la base de autenticación están disponibles; existe un código de cliente asociado al usuario. |
| Resultado exitoso | crearSesion devuelve un token opaco y el cliente muestra el menú. |

### Flujo principal

1. El cliente solicita usuario y contraseña.
2. El usuario ingresa sus credenciales.
3. El adaptador SOAP invoca crearSesion.
4. El servidor valida límites, consulta Credenciales y verifica el hash PBKDF2.
5. El servidor obtiene el código de cliente y crea una sesión temporal de 20 minutos.
6. El cliente conserva el token y muestra el menú de operaciones.

### Flujos alternativos y excepciones

- **A1 — Credenciales incorrectas o usuario sin cliente asociado:** crearSesion devuelve null; el cliente muestra el rechazo y no habilita operaciones.
- **A2 — Datos fuera de límites:** el servicio rechaza usuario vacío o de más de 30 caracteres, contraseña vacía o de más de 1024 caracteres.
- **A3 — Servicio o base de datos indisponible:** el cliente informa el fallo sin crear una sesión.
- **A4 — Operación login de compatibilidad:** devuelve 1 o -1, pero no crea token ni autoriza las operaciones protegidas.

## CU-02 — Consultar cuentas

| Campo | Descripción |
|---|---|
| Actor principal | Usuario |
| Propósito | Mostrar las cuentas activas vinculadas al cliente autenticado. |
| Disparador | El usuario abre la vista de cuentas o el menú inicial carga las cuentas. |
| Precondiciones | Existe un token vigente. |
| Resultado exitoso | El cliente muestra número, moneda y saldo de las cuentas devueltas. |

### Flujo principal

1. El cliente envía traerCuentas con el token.
2. El servidor valida la sesión y obtiene el código de cliente asociado.
3. El DAO consulta las cuentas ACTIVO de ese cliente y las ordena por código.
4. El cliente presenta la lista y permite seleccionar una cuenta para consultar movimientos u operar.

### Flujos alternativos y excepciones

- **A1 — Token inválido, ausente o caducado:** el servicio rechaza la solicitud; el cliente retorna al ingreso.
- **A2 — El cliente no tiene cuentas activas:** se devuelve una lista vacía.
- **A3 — Error de base de datos o SOAP:** el cliente informa que no pudo cargar las cuentas.

## CU-03 — Consultar movimientos

| Campo | Descripción |
|---|---|
| Actor principal | Usuario |
| Propósito | Presentar los movimientos de una cuenta activa del cliente autenticado. |
| Disparador | El usuario selecciona una cuenta y solicita su historial. |
| Precondiciones | Hay token vigente; la cuenta pertenece al cliente y está activa. |
| Resultado exitoso | El cliente muestra movimientos por número descendente, con cuenta, número, fecha, tipo, acción e importe. |

### Flujo principal

1. El usuario selecciona una cuenta.
2. El cliente invoca traerMovimientos con token y cuenta.
3. El servidor valida la sesión y las restricciones del identificador.
4. El DAO filtra por cuenta, cliente y estado ACTIVO.
5. El cliente presenta los resultados.

### Flujos alternativos y excepciones

- **A1 — No hay movimientos o la cuenta no es accesible:** se devuelve una lista vacía.
- **A2 — Cuenta vacía o de más de ocho caracteres:** el servicio no consulta y devuelve una lista vacía.
- **A3 — Token inválido o vencido:** el servicio comunica un error SOAP y el cliente vuelve al ingreso.
- **A4 — Error de base de datos o SOAP:** el cliente informa que no pudo completar la consulta.

## CU-04 — Registrar depósito

| Campo | Descripción |
|---|---|
| Actor principal | Usuario |
| Propósito | Acreditar dinero en una cuenta activa propia y registrar el movimiento. |
| Disparador | El usuario selecciona registrar depósito. |
| Precondiciones | Token vigente; cuenta del cliente en estado ACTIVO. |
| Resultado exitoso | Se actualizan saldo, contador y movimiento tipo 003 en una transacción; el servicio responde 1. |

### Flujo principal

1. El cliente solicita cuenta e importe.
2. El usuario ingresa y revisa los datos.
3. El adaptador invoca regDeposito con token, cuenta e importe.
4. El servicio valida token, cuenta e importe positivo, exacto a dos decimales y de hasta doce dígitos.
5. El DAO bloquea la cuenta del cliente y determina el siguiente número de movimiento.
6. El DAO actualiza saldo y contador, inserta el movimiento tipo 003 con empleado fijo 0001 y confirma la transacción.
7. El cliente presenta el resultado confirmado.

### Flujos alternativos y excepciones

- **A1 — Cuenta o importe inválido:** el servicio rechaza con -1 sin actualizar la cuenta.
- **A2 — Cuenta inexistente, inactiva o de otro cliente:** el DAO revierte la transacción y el servicio rechaza.
- **A3 — Error antes del commit:** el DAO ejecuta rollback; no queda saldo actualizado sin movimiento.
- **A4 — Respuesta perdida después del commit:** el depósito puede haberse aplicado. No hay clave idempotente; el operador consulta movimientos antes de decidir si repite.

## CU-05 — Transferir fondos

| Campo | Descripción |
|---|---|
| Actor principal | Usuario |
| Propósito | Transferir un importe entre dos cuentas activas. |
| Disparador | El usuario selecciona transferir e ingresa origen, destino e importe. |
| Precondiciones | Token vigente; ambas cuentas están activas; el origen pertenece al cliente autenticado. |
| Resultado exitoso | Se debita origen y se acredita destino en una transacción; se registran dos movimientos y se devuelve 1. |

### Flujo principal

1. El cliente solicita origen, destino e importe.
2. El usuario ingresa cuentas distintas de ocho dígitos e importe positivo exacto a centavos, no mayor a 9.999.999.999,99.
3. El cliente genera una clave UUID para la solicitud y la envía junto al token.
4. El servicio valida token, cuentas, importe y formato de clave.
5. El DAO busca primero una transferencia previa del mismo usuario con esa clave.
6. Si no es repetida, el DAO bloquea las cuentas por código para mantener un orden estable.
7. El DAO verifica que ambas cuentas estén activas, que tengan la misma moneda, que el origen sea del cliente autenticado y tenga saldo suficiente.
8. El DAO registra la transferencia, actualiza ambos saldos y contadores, inserta débito tipo 009 y crédito tipo 008 y confirma la transacción.
9. El cliente presenta el resultado.

### Flujos alternativos y excepciones

- **A1 — Token inválido o vencido:** el servicio rechaza la solicitud.
- **A2 — Datos inválidos, cuentas iguales o clave no UUID:** el servicio devuelve -1.
- **A3 — Cuenta inactiva, origen ajeno, moneda distinta o saldo insuficiente:** el DAO revierte y devuelve rechazo.
- **A4 — Reintento con la misma clave y los mismos datos:** el servidor reconoce la solicitud y no duplica la transferencia.
- **A5 — Reutilización de la clave con datos distintos:** el servidor rechaza la solicitud.
- **A6 — Error durante los cambios bancarios:** el DAO ejecuta rollback; no queda solo el débito o solo el crédito.
- **A7 — Respuesta perdida:** el consumidor conserva y reutiliza la misma clave y datos para resolver el resultado.

## CU-06 — Cerrar sesión

| Campo | Descripción |
|---|---|
| Actor principal | Usuario |
| Propósito | Revocar en el servidor el token del cliente. |
| Disparador | El usuario selecciona cerrar sesión o salir. |
| Precondiciones | El cliente conserva el token de sesión. |
| Resultado | El servidor revoca el token; el cliente elimina su copia y vuelve al ingreso o termina. |

### Flujo principal

1. El usuario selecciona cerrar sesión.
2. El cliente invoca cerrarSesion con el token.
3. El servidor elimina la sesión de su memoria.
4. El cliente borra el token local y cierra el menú.

### Flujos alternativos y excepciones

- **A1 — Token ya vencido o inexistente:** no existe sesión que revocar; el cliente elimina su copia local.
- **A2 — Falla de comunicación:** el cliente limpia su estado local y solicita ingresar nuevamente al próximo uso.

## Casos incluidos

| ID | Comportamiento incluido | Regla observable |
|---|---|---|
| CU-07 | Validar credenciales | Comprobar hash PBKDF2 y código de cliente asociado. |
| CU-08 | Validar cuenta | Las consultas y depósitos se limitan a cuentas ACTIVAS del cliente; el origen de transferencia debe pertenecer a este. |
| CU-09 | Validar datos del depósito | Importe positivo, exacto a centavos y con precisión máxima de doce dígitos. |
| CU-10 | Aplicar depósito | Actualizar cuenta e insertar movimiento tipo 003 y empleado 0001 de forma atómica. |

# Trazabilidad

- **CU-01 — Iniciar sesión:** RF-01 y RF-02; crearSesion, autenticación DAO y sesiones.
- **CU-02 — Consultar cuentas:** RF-02 y RF-09; traerCuentas y consulta DAO.
- **CU-03 — Consultar movimientos:** RF-02, RF-03 y RF-09; traerMovimientos y consulta DAO.
- **CU-04 — Registrar depósito:** RF-02, RF-04 a RF-07 y RF-09; regDeposito y transacción DAO.
- **CU-05 — Transferir fondos:** RF-02, RF-08 y RF-09; transferir, validación, idempotencia y transacción DAO.
- **CU-06 — Cerrar sesión:** RF-09 y RF-10; cerrarSesion y controladores de los clientes.

# Observaciones de fidelidad

- Los clientes de consola, escritorio, Android y web consumen operaciones SOAP y utilizan el token creado por crearSesion.
- login es una operación de compatibilidad: valida credenciales con 1/-1, pero no crea una sesión.
- El token se conserva en memoria del servidor, vence a los 20 minutos y se revoca al cerrar sesión; el reinicio del servidor invalida las sesiones.
- El depósito usa el empleado fijo 0001 y no implementa idempotencia.
- La transferencia es idempotente por usuario y clave UUID cuando se repiten los mismos datos; los datos diferentes se rechazan.
- Las consultas SOAP pueden devolver lista vacía para cuenta sin movimientos o no accesible; los errores de sesión y comunicación deben mostrarse por separado.
