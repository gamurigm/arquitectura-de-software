# 04. CLIWEB

Cliente **web** (JSP/Servlet, Jakarta EE 10) que consume la API REST mediante `HttpClient`.

## Proyecto

`Cliente_Web` - Maven, Java 21, empaquetado **WAR**. El token se guarda en la `HttpSession`.

## Compilar

```bash
cd Cliente_Web
mvn clean package
```

Genera `target/Cliente_Web.war`.

## Desplegar

Copia el WAR en un contenedor **Jakarta EE 10** (Tomcat 10+, Payara, GlassFish) y arráncalo.
Suponiendo Tomcat en el puerto 8080 y contexto `/Cliente_Web`:

```
http://localhost:8080/Cliente_Web/login
```

## Uso

1. En la pantalla de login indique servidor (por defecto `http://localhost:8080`), usuario y clave.
2. Elija magnitud, unidades de origen/destino e ingrese el valor.
3. Presione **CONVERTIR**. El resultado se muestra en pantalla.
4. **Cerrar sesión** invalida la sesión local.

## Notas

- El servidor REST debe ser accesible desde el contenedor donde se despliega el WAR.
- Si el token expira (`401`), la sesión se invalida y se redirige al login.

## Unidades

- **Longitud:** `centimetros`, `metros`, `pies`, `yardas`, `millas`
- **Masa:** `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`
- **Temperatura:** `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`
