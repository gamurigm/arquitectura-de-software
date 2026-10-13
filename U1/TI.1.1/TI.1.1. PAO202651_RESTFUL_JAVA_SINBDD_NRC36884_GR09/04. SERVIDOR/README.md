# 04. SERVIDOR

Servidor REST del sistema **Conversión de Unidades Monster**.

## Proyecto

`WS_ConUni_Java_REST` - Aplicación **Spring Boot 3.5.x** con Java 21, Spring Web y Maven.
**Sin base de datos** (estado en memoria).

Ver `WS_ConUni_Java_REST/README.md` para el detalle técnico y de endpoints.

## Compilar y ejecutar

```bash
cd WS_ConUni_Java_REST
mvn clean package
java -jar target/WS_ConUni_Java_REST.jar
```

El servicio queda disponible en `http://localhost:8080`.

## Pruebas

```bash
mvn test
```

Incluye pruebas de integración de la API y pruebas unitarias del servicio de conversión.
