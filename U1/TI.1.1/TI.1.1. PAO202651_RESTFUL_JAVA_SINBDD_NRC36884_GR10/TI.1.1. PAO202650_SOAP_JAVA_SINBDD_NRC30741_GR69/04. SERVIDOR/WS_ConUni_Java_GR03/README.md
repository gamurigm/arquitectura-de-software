# Cliente movil para WS_ConUni_Java_GR03

Este servidor expone servicios SOAP (Jakarta JAX-WS) para conversion de unidades. El flujo es:

1) Llamar a `WSLogin.login` para obtener un token.
2) Usar ese token en cualquier conversion.

## Requisitos

- JDK 17
- Maven
- Servidor de aplicaciones compatible con Jakarta EE (por ejemplo Payara o GlassFish)

## Despliegue del servidor

```bash
mvn clean package
```

Despliega el WAR generado en tu servidor de aplicaciones. Por defecto el contexto es:

```
http://localhost:8080/WS_ConUni_Java_GR03/
```

Pagina de prueba:

```
http://localhost:8080/WS_ConUni_Java_GR03/index.html
```

WSDLs:

- `http://localhost:8080/WS_ConUni_Java_GR03/WSLogin?wsdl`
- `http://localhost:8080/WS_ConUni_Java_GR03/WSLongitud?wsdl`
- `http://localhost:8080/WS_ConUni_Java_GR03/WSMasa?wsdl`
- `http://localhost:8080/WS_ConUni_Java_GR03/WSTemperatura?wsdl`

## Operaciones

- `WSLogin.login(usuario, clave) -> token`
  - Credenciales: `monster` / `monster9`

- `WSLongitud.convertirLongitud(valor, unidadOrigen, unidadDestino, token) -> double`
  - Unidades: `centimetros`, `metros`, `pies`, `yardas`, `millas`

- `WSMasa.convertirMasa(valor, unidadOrigen, unidadDestino, token) -> double`
  - Unidades: `gramos`, `kilogramos`, `onzas`, `libras`, `quintales`

- `WSTemperatura.convertirTemperatura(valor, opcionOrigen, opcionDestino, token) -> double`
  - Unidades: `fahrenheit`, `kelvin`, `celsius`, `newton`, `reaumur`

Codigos de error posibles:

- Token invalido: `-999999.401` (WSLongitud) o `-9999999.401` (WSMasa, WSTemperatura)
- Unidad invalida: `-999999.404`
- Error de calculo: `-999999.500`

## Cliente movil (Android)

Ejemplo con Kotlin y ksoap2-android.

### 1) Crear proyecto en Android Studio

Crea una app vacia y agrega permisos de red:

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

Si usas HTTP local en Android 9+, habilita cleartext en `res/xml/network_security_config.xml` y referencia en el manifest.

### 2) Dependencia

```gradle
dependencies {
    implementation "com.google.code.ksoap2-android:ksoap2-android:3.6.4"
}
```

### 3) Llamar a WSLogin

Revisa el `targetNamespace` del WSDL. Si usas el namespace por defecto de JAX-WS, suele ser:

```
http://WebServices.monster.edu.ec/
```

Codigo Kotlin:

```kotlin
private const val NAMESPACE = "http://WebServices.monster.edu.ec/"
private const val SOAP_ACTION = ""

fun login(baseUrl: String, user: String, pass: String): String {
    val url = "${baseUrl}WSLogin"
    val request = SoapObject(NAMESPACE, "login").apply {
        addProperty("usuario", user)
        addProperty("clave", pass)
    }

    val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
        dotNet = false
        setOutputSoapObject(request)
    }

    HttpTransportSE(url).call(SOAP_ACTION, envelope)
    return envelope.response as String
}
```

### 4) Llamar a una conversion

```kotlin
fun convertirLongitud(baseUrl: String, token: String): Double {
    val url = "${baseUrl}WSLongitud"
    val request = SoapObject(NAMESPACE, "convertirLongitud").apply {
        addProperty("valor", 10.0)
        addProperty("unidadOrigen", "metros")
        addProperty("unidadDestino", "pies")
        addProperty("token", token)
    }

    val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
        dotNet = false
        setOutputSoapObject(request)
    }

    HttpTransportSE(url).call(SOAP_ACTION, envelope)
    return (envelope.response as SoapPrimitive).toString().toDouble()
}
```

### 5) Base URL para emulador

- Emulador Android: `http://10.0.2.2:8080/WS_ConUni_Java_GR03/`
- Dispositivo fisico: usa la IP de tu PC en la misma red.

## Checklist rapido

- Servidor desplegado y accesible desde el movil
- WSDL accesible desde el navegador del movil
- Token valido obtenido con `WSLogin.login`
- Envio de unidades en minusculas exactas
