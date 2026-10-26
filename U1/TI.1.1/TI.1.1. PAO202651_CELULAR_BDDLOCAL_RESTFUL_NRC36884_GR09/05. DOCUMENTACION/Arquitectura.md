# Arquitectura de la solución

~~~mermaid
flowchart LR
    C["Consola pendiente"] --> R["API comercializadora :19102"]
    E["WPF pendiente"] --> R
    M["MAUI Android pendiente"] --> R
    W["MVC web :19103 pendiente"] --> R
    R --> V[("ComercializadoraCelulares\nbase de este proyecto")]
    R -- "HTTP / API bancaria" --> B["BanQuito.Api :19101\nproyecto independiente del banco"]
    B --> D[("BanQuitoCore\nbase del proyecto bancario")]
~~~

La comercializadora coordina catálogo y facturas. BanQuito evalúa elegibilidad, calcula y registra crédito y amortización. Cada API y cada base pertenecen a su proyecto; el enlace entre proyectos es HTTP y `COD_CREDITO` es una referencia remota.

Las interfaces de consola, WPF, MVC y MAUI siguen pendientes. El diagrama describe responsabilidades y no afirma que los clientes pendientes estén implementados.
