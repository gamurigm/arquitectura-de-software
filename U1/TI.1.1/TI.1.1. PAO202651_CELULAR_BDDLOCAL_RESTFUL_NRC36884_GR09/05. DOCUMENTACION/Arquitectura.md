# Arquitectura de la solución

~~~mermaid
flowchart LR
    C["Consola .NET pendiente"] --> R["API comercializadora 19102 pendiente"]
    E["WPF pendiente"] --> R
    M["MAUI Android pendiente"] --> R
    W["MVC web 19103 pendiente"] --> R
    R --> V[("SQL ComercializadoraCelulares: script creado")]
    R --> B["API BanQuito 19101 implementada; integración pendiente"]
    B --> D[("SQL BanQuitoCore: script creado")]
    B --> N["Dominio financiero: 21 pruebas correctas"]
~~~

El Core solo expone servicios de crédito. La comercializadora coordina catálogo, facturas y llamadas al banco.
Todas las interfaces usarán la misma API comercializadora para conservar reglas y datos.
Las dos APIs se desplegarán por separado. Los clientes no recibirán credenciales SQL.
La API bancaria permanece en localhost; el acceso de Android se resuelve a través de la API comercializadora.

La estructura identifica expresamente qué está pendiente. No prueba despliegue o ejecución de los componentes todavía no implementados.

