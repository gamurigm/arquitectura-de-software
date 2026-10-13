# 04. CLICON

Cliente de **consola** (Java SE, `java.net.http.HttpClient` + Jackson) que consume la API REST.

## Proyecto

`ClienteConsola` - Maven, Java 21. Genera un fat jar ejecutable: `target/ClienteConsola.jar`.

## Compilar

```bash
cd ClienteConsola
mvn clean package
```

## Ejecutar

El servidor REST debe estar en ejecución.

```bash
java -jar target/ClienteConsola.jar
```

## Uso

1. Indica la URL del servidor (Enter para `http://localhost:8080`).
2. Inicia sesión (Enter para `monster` / `monster9`).
3. Elige una opción del menú:
   - `1` Convertir Longitud
   - `2` Convertir Masa
   - `3` Convertir Temperatura
   - `4` Ver catálogo de unidades
   - `0` Salir
4. Ingresa valor, unidad de origen y unidad de destino.

## Unidades

- **Longitud:** `centimetros`, `metros`, `pies`, `yardas`, `millas`
- **Masa:** `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`
- **Temperatura:** `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`
