# 04. CLIESC

Cliente de **escritorio** (Java Swing, `java.net.http.HttpClient` + Jackson) que consume la API REST.

## Proyecto

`ClienteEscritorio` - Maven, Java 21. Genera un fat jar ejecutable: `target/ClienteEscritorio.jar`.

## Compilar

```bash
cd ClienteEscritorio
mvn clean package
```

## Ejecutar

El servidor REST debe estar en ejecución.

```bash
java -jar target/ClienteEscritorio.jar
```

## Uso

1. En la ventana de login indique servidor, usuario y clave (por defecto `monster` / `monster9`).
2. En la ventana principal seleccione la **magnitud** (Longitud, Masa o Temperatura).
3. Elija **unidad de origen** y **unidad de destino** (se cargan desde `/api/catalogo`).
4. Ingrese el **valor** y presione **CONVERTIR**.
5. El botón **Cerrar sesión** vuelve al login.

Si el servidor responde `401` (token expirado), la aplicación regresa al login automáticamente.

## Unidades

- **Longitud:** `centimetros`, `metros`, `pies`, `yardas`, `millas`
- **Masa:** `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`
- **Temperatura:** `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`
