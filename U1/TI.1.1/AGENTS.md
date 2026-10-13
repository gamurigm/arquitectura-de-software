# Proyectos TI.1.1 PAO202651 GR09

- Todos los clientes GR09 separan MVC en el código: `modelo`, `vista` y `controlador`; `servicios` contiene adaptadores y comunicación remota. En .NET, conserva las convenciones del framework cuando correspondan, como `Models`, `Views` y `Controllers` en ASP.NET Core.
- En cada servidor GR09, los controladores publican operaciones, los servicios coordinan la lógica y los modelos representan datos. Mantén paquetes/carpetas equivalentes a `controlador`, `modelo` y `servicios`, más `seguridad`, `configuracion` o `excepciones` cuando existan. Una API no necesita una vista dentro del servidor.
- Todo servidor de conversión implementa `IConversor<TUnidad>` con una operación equivalente a `Convertir/convertir(valor, unidadOrigen, unidadDestino)`. Los conversores separados de longitud, masa y temperatura implementan esa interfaz; el servicio valida, selecciona y delega. No concentres las fórmulas en controladores ni en un servicio monolítico.
- Conserva puntos de entrada y directorios nativos de cada plataforma. Ordena vistas, modelos, controladores y servicios en las carpetas MVC del proyecto; no mezcles código de interfaz con llamadas SOAP/REST.
- Consulta ERS, contrato y manuales Word del proyecto para fijar nombres y responsabilidades. GR05 y GR69 solo son ejemplos internos de organización: nunca los menciones como referencias en documentos GR09.
- Mantén la documentación Word del proyecto en sus carpetas numeradas. Registra únicamente funciones que existan en el código o requisitos identificados como pendientes.
