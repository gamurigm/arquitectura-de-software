# Proyectos TI.1.1 PAO202651 GR09

- Todos los proyectos SOAP Java, SOAP .NET, REST Java y REST .NET del grupo GR09 siguen MVC. En cada cliente, el modelo conserva datos y estado, la vista presenta y recoge entradas, y el controlador coordina el flujo. Los servicios o adaptadores realizan la comunicación remota.
- En el servidor, los controladores publican operaciones, los servicios contienen la lógica de negocio y los modelos representan datos y reglas. Las vistas del sistema son los clientes; una API no necesita una pantalla dentro del servidor.
- En Java, usa paquetes explícitos `modelo`, `vista`, `controlador` y `servicios` según la responsabilidad real. En .NET, usa carpetas y espacios de nombres equivalentes.
- Conserva las carpetas numeradas de cada proyecto. Consulta su ERS y el contrato real del servidor antes de cambiar clientes o documentación. Usa GR05 y GR69 como ejemplos de organización, adaptando el transporte al protocolo propio de cada arquitectura.
- Mantén la documentación Word del proyecto en sus carpetas numeradas. Registra únicamente funciones que existan en el código o requisitos identificados como pendientes.
