# Modelo de datos BanQuito

La base `BanQuitoCore` es propiedad del proyecto bancario REST .NET. La comercializadora de celulares solo recibe identificadores por la API; no accede a estas tablas.

~~~mermaid
erDiagram
    CLIENTE ||--o{ CUENTA : posee
    CUENTA ||--o{ MOVIMIENTO : registra
    CLIENTE ||--o{ CREDITO : recibe
    CREDITO ||--|{ AMORTIZACION : contiene
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
~~~

La referencia de venta es única para permitir reintentos idempotentes. El índice filtrado `UX_CREDITO_ACTIVO` impide más de un crédito activo por cliente. La base `ComercializadoraCelulares` vive en el proyecto separado de la tienda y no tiene claves foráneas a `BanQuitoCore`.
