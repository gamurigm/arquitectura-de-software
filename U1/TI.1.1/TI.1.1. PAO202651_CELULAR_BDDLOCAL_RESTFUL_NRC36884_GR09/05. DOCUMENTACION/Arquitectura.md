# Arquitectura de la solución

~~~mermaid
flowchart LR
    C["Consola"] --> R["API comercializadora :19102"]
    E["WPF"] --> R
    M["MAUI Android"] --> R
    W["MVC web :19103"] --> R
    R --> V[("ComercializadoraCelulares\nbase de este proyecto")]
    R -- "HTTP / API bancaria" --> B["BanQuito.Api :19101\nproyecto independiente del banco"]
    B --> D[("BanQuitoCore\nbase del proyecto bancario")]
~~~

La comercializadora coordina catálogo y facturas. BanQuito evalúa elegibilidad, calcula y registra crédito y amortización. Cada API y cada base pertenecen a su proyecto; el enlace entre proyectos es HTTP y `COD_CREDITO` es una referencia remota.

Los cuatro clientes consumen la API de la tienda. La verificación local del 26 de octubre de 2026 confirmó conexión con ambas bases, venta en efectivo, venta a crédito y consulta de amortización.
