# Cliente de escritorio EurekaBank SOAP Java GR09

## Objetivo

Implementar el siguiente cliente en `04. CLIESC/ClienteEscritorio`, siguiendo MVC y la estructura del ejemplo `EUREKABANK_SOAP_JAVA_ANDRANGO_CALAPAQUI_QUILUMBAQUIN/02. CLIESC/WSEurekaClienteEscritorio`.

El cliente utilizará Swing y las operaciones del servidor EurekaBank GR09: iniciar sesión, consultar movimientos y registrar depósitos. La conexión con MySQL seguirá siendo responsabilidad del servidor.

## Estructura

Se conservará la distribución `src` del ejemplo y del cliente de consola:

| Paquete | Responsabilidad |
| --- | --- |
| `wseurekaclienteescritorio` | Punto de entrada y composición de la aplicación. |
| `ec.edu.monster.modelo` | Datos de movimientos, con importes `BigDecimal`. |
| `ec.edu.monster.vista` | Pantallas Swing: `LoginVista`, `MenuVista`, `MovimientosVista` y `DepositosVista`. |
| `ec.edu.monster.controlador` | Eventos, validación de entradas, navegación y coordinación de operaciones. |
| `ec.edu.monster.service` | Adaptación del contrato SOAP a los datos del cliente. |
| `ec.edu.monster.ws` | Clases del contrato generadas por Maven a partir del WSDL local. |

Las vistas expondrán entradas, eventos y presentación de resultados. El controlador coordinará el servicio; las vistas no abrirán conexiones JDBC ni crearán puertos SOAP.

## Comportamiento

1. La aplicación abrirá el login. Solo un resultado SOAP de autenticación exitosa permitirá acceder al menú.
2. El menú permitirá abrir la consulta de movimientos, registrar un depósito y cerrar sesión.
3. La consulta exigirá una cuenta y mostrará número, cuenta, fecha, tipo, acción e importe. Una respuesta sin registros tendrá un mensaje explícito.
4. El depósito exigirá cuenta e importe positivo, con un máximo de dos decimales. Los cálculos y el transporte usarán `BigDecimal`.
5. Se deshabilitará la acción mientras exista una petición pendiente para impedir envíos duplicados. Un error de comunicación no provocará un reintento automático del depósito.
6. Las peticiones SOAP se ejecutarán fuera del hilo de eventos de Swing. La actualización de pantallas volverá a ese hilo.
7. Los errores de conexión o del servicio se mostrarán sin cerrar la aplicación ni informar éxito cuando el resultado sea incierto.

## Integración y ejecución

- Usar el mismo contrato WSDL y las versiones de Metro y Java que el cliente de consola GR09.
- Incluir el WSDL en los recursos del cliente para generar y cargar el contrato sin depender del servidor durante la compilación.
- Admitir `EUREKABANK_SOAP_URL`, con el endpoint local del servidor GR09 como valor predeterminado.
- Proporcionar `pom.xml` y `run.ps1`, manteniendo las dependencias y cachés configurables en `D:`.
- Usar exclusivamente Payara Server Community 7 para el servidor. El cliente Swing se ejecutará como aplicación Java.
- No guardar contraseñas ni incorporar credenciales de base de datos al cliente.

## Verificación

- Compilar y empaquetar el cliente con Java 25.
- Verificar autenticación rechazada, cuenta vacía e importes inválidos antes de enviar operaciones.
- Comprobar la precisión decimal y el manejo de errores con pruebas sin modificar cuentas reales.
- Abrir las cuatro pantallas y comprobar navegación, legibilidad y respuesta de la interfaz.
- Probar autenticación y consulta contra el servidor local cuando esté disponible; distinguir esas comprobaciones de las realizadas con respuestas controladas.

## Alcance

Este trabajo cubre únicamente el cliente de escritorio SOAP Java GR09. Los clientes móvil, web y .NET quedan fuera. No se cambiarán las operaciones del servidor ni los valores de las credenciales existentes.
