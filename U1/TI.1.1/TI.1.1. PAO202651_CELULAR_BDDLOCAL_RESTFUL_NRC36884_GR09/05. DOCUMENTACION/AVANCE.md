# Avance de implementación al corte del 23 de octubre de 2026

## Hecho

- Revisado el enunciado Word, incluido modelo Core y ejemplo de amortización.
- Creada estructura GR09; GR01 se conserva únicamente como referencia de consulta.
- Guardado plan con .NET 10, SQL Server 2025 Express y porcentaje 35% elegido por el usuario.
- Implementados contratos y dominio financiero con 21 pruebas correctas.
- Implementada API bancaria: elegibilidad, monto máximo, otorgamiento transaccional y consulta de cuotas.
- Implementada referencia idempotente para reintentar otorgamientos sin duplicar créditos.
- Creados scripts SQL de ambas bases y datos de 5 clientes, 5 cuentas y 50 movimientos.
- Creado inicializador SQL y script de instalación del motor con destinos configurables en D:.
- API bancaria e inicializador compilan sin advertencias ni errores.
- Probada API en localhost: entradas inválidas y cédula vacía devuelven HTTP 400.
- Creada documentación editable de casos de uso, ER, arquitectura y manual del avance.

## Pendiente

- Completar instalación SQL Server 2025 Express. El intento devolvió -2067529714.
- Aplicar y verificar scripts contra el motor real; no se afirma que las bases ya estén creadas.
- Probar persistencia, concurrencia y reintentos de crédito contra SQL Server.
- Implementar API comercializadora, consola, WPF, MVC y MAUI Android.
- Completar interfaces, facturación integrada, diagramas finales y manual de entrega.

## Evidencia y límites

21 pruebas de negocio correctas. Incluyen 35%, edad, ventanas, redondeo, plazos, mayúsculas/minúsculas y cultura decimal.
El ciclo de pruebas observó fallos iniciales y su corrección.
La revisión independiente detectó el caso de género/movimiento en minúsculas; quedó corregido con dos pruebas.
Una prueba HTTP detectó un error de validación por cultura decimal; se corrigió y verificó.
Se corrigió una dependencia transitiva vulnerable usando System.Security.Cryptography.Pkcs 10.0.13.
La comprobación /api/salud devuelve HTTP 503 mientras SQL Server no esté disponible.
La API se detuvo después de las pruebas HTTP; no queda presentada como servicio desplegado.

El usuario autorizó commit y push del avance a las 17:00, incluso incompleto.
La tarea de publicación solo incluye este directorio. Excluye GR01 y el DOCX original sin seguimiento.

