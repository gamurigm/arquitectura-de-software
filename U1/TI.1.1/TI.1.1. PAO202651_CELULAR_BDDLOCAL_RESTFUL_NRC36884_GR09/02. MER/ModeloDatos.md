# Modelo de datos de la solución GR09

Estado: diseño físico implementado en scripts SQL; integración contra SQL Server pendiente hasta verificar instalación.

~~~mermaid
erDiagram
    CLIENTE ||--o{ CUENTA : posee
    CUENTA ||--o{ MOVIMIENTO : registra
    CLIENTE ||--o{ CREDITO : recibe
    CREDITO ||--|{ AMORTIZACION : contiene
    CELULAR ||--o{ FACTURA : aparece
    CLIENTE {
      int COD_CLIENTE PK
      varchar CEDULA UK
      varchar NOMBRE
      varchar GENERO
      date FECHA_NACIMIENTO
    }
    CUENTA {
      varchar NUM_CUENTA PK
      int COD_CLIENTE FK
      numeric SALDO
    }
    MOVIMIENTO {
      int COD_MOVIMIENTO PK
      varchar NUM_CUENTA FK
      varchar TIPO
      numeric VALOR
      date FECHA
    }
    CREDITO {
      int COD_CREDITO PK
      int COD_CLIENTE FK
      uniqueidentifier REFERENCIA_VENTA UK
      numeric MONTO
      int NUMERO_CUOTAS
      decimal TASA_ANUAL
      date FECHA
      varchar ESTADO
    }
    AMORTIZACION {
      int COD_CREDITO PK,FK
      int NUMERO_CUOTA PK
      date FECHA_PAGO
      numeric VALOR_CUOTA
      numeric INTERES_PAGADO
      numeric CAPITAL_PAGADO
      numeric SALDO
    }
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
      numeric PRECIO
      numeric DESCUENTO
      numeric TOTAL
      varchar FORMA_PAGO
      int COD_CREDITO
      varchar ESTADO
    }
~~~

BanQuitoCore contiene CLIENTE, CUENTA y MOVIMIENTO del modelo del enunciado, más CREDITO y AMORTIZACION.
ComercializadoraCelulares contiene CELULAR y FACTURA. No hay inventario ni acceso directo de clientes a las bases.
COD_CREDITO en FACTURA es referencia remota, no una clave foránea entre bases. La comercializadora debe validarla mediante REST.
Una factura registra un celular y conserva una copia del nombre y precio vigentes al vender.

