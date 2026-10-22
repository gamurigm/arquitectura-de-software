---
title: "Especificación de casos de uso"
subtitle: "EurekaBank SOAP Java con base de datos — Grupo 09"
author: "Grupo 09"
date: "21 de octubre de 2026"
lang: es
---

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Control del documento

| Campo | Valor |
|---|---|
| Proyecto | EurekaBank SOAP Java con base de datos |
| Grupo | 09 |
| Versión | 1.0 |
| Estado | Línea base documental del código del proyecto |
| Documento relacionado | `ERS_EUREKABANK_SOAP_JAVA_GR09.docx` |

# Introducción

Este documento describe los objetivos del usuario y los flujos funcionales de EurekaBank. El modelo se limita a las operaciones existentes en el servidor SOAP y a los flujos de consola y escritorio observados en el repositorio. Se conserva la organización visual de la documentación de referencia, adaptando actores, operaciones y restricciones al sistema bancario.

# Alcance y actor

El sistema de casos de uso incluye autenticación, consulta de movimientos, registro de depósitos y cierre de la sesión local del cliente. El actor principal es el **Usuario autorizado**, quien opera la solución desde el cliente de consola o el cliente de escritorio.

La base de datos, el servidor Payara y SOAP son componentes de la solución y no actores humanos. La figura representa objetivos funcionales; no afirma que cada operación de consulta o depósito tenga autorización de sesión aplicada por el servidor.

# Diagrama general de casos de uso

![Diagrama de casos de uso EurekaBank, grupo 09](<../03. DIAGRAMAS UML/01_CASOS_DE_USO/GR09_CasosUso_EurekaBank.png>){width=6.6in}

## Relaciones del modelo

- El actor Usuario autorizado participa en los cuatro objetivos principales.
- `<<include>>` apunta desde el caso principal hacia el comportamiento incluido, obligatorio en ese flujo.
- CU-01 incluye CU-05 Validar credenciales.
- CU-02 y CU-03 incluyen CU-06 Validar cuenta.
- CU-03 incluye CU-07 Validar datos del depósito y CU-08 Aplicar depósito.
- No se modelan relaciones `<<extend>>`: los rechazos, errores de conexión y resultados vacíos son flujos alternativos de los casos principales, no objetivos opcionales independientes.
- El cierre de sesión es local al cliente y no genera una operación SOAP.

## Resumen de casos

| ID | Caso de uso | Objetivo |
|---|---|---|
| CU-01 | Iniciar sesión | Validar las credenciales del usuario y permitir que el flujo del cliente continúe. |
| CU-02 | Consultar movimientos | Presentar los movimientos de una cuenta consultada. |
| CU-03 | Registrar depósito | Acreditar un importe en una cuenta activa y registrar el movimiento. |
| CU-04 | Cerrar sesión local | Salir del menú y retornar al formulario de ingreso o finalizar el cliente. |
| CU-05 | Validar credenciales | Comprobar las credenciales almacenadas para el usuario ingresado. |
| CU-06 | Validar cuenta | Comprobar que el identificador recibido no esté vacío y no exceda ocho caracteres. |
| CU-07 | Validar datos del depósito | Comprobar que el importe sea positivo, exacto a centavos y de precisión admitida. |
| CU-08 | Aplicar depósito | Actualizar saldo y contador, insertar movimiento y confirmar de forma atómica. |

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Especificaciones detalladas

## CU-01 — Iniciar sesión

| Campo | Descripción |
|---|---|
| Actor principal | Usuario autorizado |
| Propósito | Permitir que el usuario inicie el flujo del cliente con sus credenciales. |
| Disparador | El usuario abre el cliente e indica que desea ingresar. |
| Precondiciones | El servicio SOAP está publicado y la base de datos de autenticación está disponible. El usuario dispone de credenciales registradas. |
| Resultado exitoso | El cliente acepta la respuesta `1` y muestra el menú de operaciones. No se crea una sesión remota ni un token. |

### Flujo principal

1. El cliente solicita el nombre de usuario y la contraseña.
2. El usuario ingresa sus credenciales.
3. El cliente invoca la operación SOAP `login(usuario, contrasena)`.
4. El servicio valida los parámetros y consulta la credencial asociada al usuario.
5. El servidor verifica el hash PBKDF2-HMAC-SHA-256 de la contraseña.
6. El servicio responde `1` si la validación es correcta.
7. El cliente presenta el menú de consulta, depósito y salida.

### Flujos alternativos y excepciones

- **A1 — Credenciales incorrectas:** el servicio responde `-1`; el cliente informa que el usuario o la contraseña son incorrectos y no habilita el menú.
- **A2 — Datos faltantes o fuera de límites:** la validación rechaza el usuario vacío, usuario de más de 30 caracteres, contraseña vacía o contraseña de más de 1024 caracteres.
- **A3 — Servicio o base de datos indisponible:** el servicio no puede completar la operación; el cliente muestra un mensaje de comunicación o autenticación fallida y permanece en el ingreso.

### Variación por cliente

El cliente de escritorio abre la pantalla principal luego del resultado positivo. El cliente de consola autentica al usuario antes de entrar al ciclo del menú; al salir del menú finaliza su flujo.

## CU-02 — Consultar movimientos

| Campo | Descripción |
|---|---|
| Actor principal | Usuario autorizado |
| Propósito | Consultar los movimientos registrados para una cuenta. |
| Disparador | El usuario selecciona “Consultar movimientos”. |
| Precondiciones | El usuario llegó al menú del cliente y el servicio SOAP está disponible. El identificador de cuenta no está vacío y no excede ocho caracteres. |
| Resultado exitoso | El cliente muestra los movimientos recibidos, ordenados por número descendente, con cuenta, número, fecha, tipo, acción e importe. |

### Flujo principal

1. El cliente solicita el número de cuenta.
2. El usuario ingresa la cuenta.
3. El cliente invoca `traerMovimientos(cuenta)`.
4. El servicio valida el identificador y consulta la información de movimientos y sus tipos.
5. El servidor devuelve la lista ordenada por número de movimiento descendente.
6. El cliente presenta los datos de cada movimiento.

### Flujos alternativos y excepciones

- **A1 — No hay movimientos:** el servicio devuelve una lista vacía; el cliente informa que no hay movimientos para la consulta. La respuesta no permite distinguir una cuenta inexistente de una cuenta sin movimientos.
- **A2 — Cuenta vacía o de más de ocho caracteres:** el servicio no realiza la consulta y devuelve una lista vacía. La interfaz Swing además limita el ingreso a entre uno y ocho dígitos; el cliente de consola solicita una cadena y delega al servicio las validaciones comunes.
- **A3 — Error de base de datos o SOAP:** el servicio comunica el error como excepción SOAP; el cliente informa que no se pudo completar la consulta.

### Datos de salida

Cada elemento contiene cuenta, número de movimiento, fecha, descripción del tipo, acción e importe. Los tipos y acciones se obtienen de las tablas `Movimiento` y `TipoMovimiento`.

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

## CU-03 — Registrar depósito

| Campo | Descripción |
|---|---|
| Actor principal | Usuario autorizado |
| Propósito | Acreditar un importe en una cuenta activa y registrar el movimiento correspondiente. |
| Disparador | El usuario selecciona “Registrar depósito”. |
| Precondiciones | El usuario llegó al menú del cliente; la cuenta existe y está activa; el servicio y la base de datos están disponibles. |
| Resultado exitoso | El saldo y el contador de movimientos se actualizan y se registra un movimiento tipo depósito en una sola transacción. El servicio responde `1`. |

### Flujo principal

1. El cliente solicita la cuenta y el importe.
2. El usuario ingresa ambos datos.
3. El cliente valida los datos que corresponden a su interfaz y llama `regDeposito(cuenta, importe)`.
4. El servicio valida la cuenta y normaliza el importe a dos decimales sin redondear valores que excedan los centavos.
5. El DAO inicia una transacción y bloquea la fila de la cuenta activa.
6. El DAO calcula el siguiente número de movimiento, suma el importe al saldo y aumenta el contador.
7. El DAO inserta el movimiento de depósito con tipo `003` y empleado técnico `0001`.
8. El DAO confirma la transacción.
9. El servicio devuelve `1` y el cliente informa que el depósito se registró.

### Flujos alternativos y excepciones

- **A1 — Cuenta o importe inválido:** una cuenta vacía, de más de ocho caracteres, un importe nulo, cero, negativo, con más de dos decimales no reducibles exactamente o con precisión superior a doce dígitos no se procesa. El servicio responde `-1`.
- **A2 — Cuenta inexistente o inactiva:** el DAO no obtiene una cuenta activa; la transacción se revierte y el servicio rechaza la solicitud.
- **A3 — Error al actualizar saldo o insertar movimiento:** el DAO revierte la transacción. No debe quedar actualizado el saldo sin el movimiento correspondiente.
- **A4 — Resultado de red incierto:** el cliente puede no recibir la respuesta aunque el servidor haya confirmado el depósito. El cliente de escritorio indica consultar movimientos antes de repetir la operación; no se reintenta automáticamente.

### Variación por cliente

El escritorio limita la cuenta a ocho dígitos, admite importes de `0,01` a `9.999.999.999,99` y deshabilita el envío mientras espera. La consola interpreta el importe como decimal; el servicio aplica las reglas finales.

## CU-04 — Cerrar sesión local

| Campo | Descripción |
|---|---|
| Actor principal | Usuario autorizado |
| Propósito | Salir del menú para finalizar el uso local del cliente. |
| Disparador | El usuario selecciona “Cerrar sesión” o salir. |
| Precondiciones | El usuario está en el menú local del cliente. |
| Resultado | El cliente vuelve a la pantalla de ingreso o termina su ejecución. El servidor no recibe una petición de cierre de sesión. |

### Flujo principal

1. El usuario selecciona la opción de salida.
2. El cliente limpia su estado local de autenticación, cuando ese estado se mantiene en memoria.
3. El cliente de escritorio retorna a la pantalla de ingreso; el cliente de consola termina el ciclo del menú y su ejecución.

## Casos incluidos

| ID | Comportamiento incluido | Regla observable |
|---|---|---|
| CU-05 | Validar credenciales | Rechazar entradas vacías o fuera de los límites y comprobar la contraseña contra el hash almacenado. |
| CU-06 | Validar cuenta | Rechazar la cuenta vacía o con más de ocho caracteres antes de consultar o depositar. La validación de solo dígitos existe en la interfaz Swing. |
| CU-07 | Validar datos del depósito | Exigir importe positivo, exacto a dos decimales y precisión máxima de doce dígitos. |
| CU-08 | Aplicar depósito | Confirmar actualización de saldo y alta del movimiento como una sola transacción; revertir si ocurre un error. |

# Trazabilidad

- **CU-01 — Iniciar sesión:** RF-01, RF-02 y RNF-01. Operación y componentes: `login`, autenticación DAO y clientes.
- **CU-02 — Consultar movimientos:** RF-03 y RF-08. Operación y módulo: `traerMovimientos`, consulta DAO.
- **CU-03 — Registrar depósito:** RF-04, RF-05, RF-06, RF-07, RF-08, RNF-03 y RNF-04. Operación y módulo: `regDeposito`, transacción DAO.
- **CU-04 — Cerrar sesión local:** RF-09. Componentes: menú de consola y controlador Swing.

# Observaciones de fidelidad

- El resultado positivo de `login` habilita el flujo de los clientes, pero el servicio no crea una sesión, no devuelve token y no exige dicho resultado en las llamadas de consulta o depósito.
- El cierre de sesión descrito es local. No hay operación SOAP para revocar una sesión.
- La operación de depósito registra al empleado `0001` configurado en el código del servidor; no registra la identidad del operador que inició sesión.
- Los errores de consulta se representan como excepción SOAP; los errores de login y depósito se exponen como `-1`.
- No se modelan funciones de web, móvil, transferencias ni administración porque no están implementadas en el alcance revisado.
