# Modelo de datos de la comercializadora

`ComercializadoraCelulares` es la única base de datos que pertenece a este proyecto.

~~~mermaid
erDiagram
    CELULAR ||--o{ FACTURA : aparece
    CELULAR {
      int ID_CELULAR PK
      nvarchar NOMBRE
      numeric PRECIO
      bit ACTIVO
    }
    FACTURA {
      int ID_FACTURA PK
      uniqueidentifier ID_OPERACION UK
      int ID_CELULAR FK
      nvarchar CEDULA
      nvarchar NOMBRE_CLIENTE
      nvarchar NOMBRE_CELULAR
      numeric PRECIO
      numeric DESCUENTO
      numeric TOTAL
      varchar FORMA_PAGO
      int COD_CLIENTE_BANCO
      int COD_CREDITO
      int NUMERO_CUOTAS
      varchar ESTADO
    }
~~~

La factura conserva nombre y precio al vender. `COD_CLIENTE_BANCO` y `COD_CREDITO` son referencias remotas devueltas por BanQuito; no son claves foráneas entre bases. `BanQuitoCore` y su modelo viven en el proyecto bancario.
