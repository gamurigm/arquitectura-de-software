# 04. CLIMOV

Cliente **móvil Android** (Java + Retrofit + Gson) que consume la API REST.

## Proyecto

`ClienteMovil` - Proyecto Gradle (Kotlin DSL). `minSdk 24`, `targetSdk 35`, Java 17.

## Abrir y ejecutar

1. Abre la carpeta `ClienteMovil` con **Android Studio**.
2. Espera la sincronización de Gradle.
3. Ejecuta en un emulador o dispositivo.

## URL del servidor

- **Emulador Android:** `http://10.0.2.2:8080` (apunta al `localhost` del PC). Es la URL por defecto.
- **Dispositivo físico:** usa la IP del PC en la misma red, p. ej. `http://192.168.1.10:8080`.

La app habilita `usesCleartextTraffic` para permitir HTTP local.

## Uso

1. **Login:** servidor, usuario y clave (por defecto `monster` / `monster9`). El token se guarda en
   `SharedPreferences`.
2. **Conversión:** elija magnitud, unidades de origen/destino, ingrese el valor y presione **CONVERTIR**.
3. **Cerrar sesión** limpia la sesión y vuelve al login.

## Estructura

- `api/` - modelos, `ApiService` (Retrofit) y `ApiClient`.
- `LoginActivity` - pantalla de login.
- `ConversionActivity` - pantalla de conversiones.
- `TokenStore` - persistencia de la sesión.

## Unidades

- **Longitud:** `centimetros`, `metros`, `pies`, `yardas`, `millas`
- **Masa:** `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`
- **Temperatura:** `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`
