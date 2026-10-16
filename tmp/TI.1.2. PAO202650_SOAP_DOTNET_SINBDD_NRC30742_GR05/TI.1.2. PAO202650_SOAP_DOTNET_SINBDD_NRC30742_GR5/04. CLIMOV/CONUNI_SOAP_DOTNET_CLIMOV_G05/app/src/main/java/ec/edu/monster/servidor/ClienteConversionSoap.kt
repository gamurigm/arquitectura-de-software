package ec.edu.monster.servidor

import android.util.Log
import ec.edu.monster.modelo.Conversion
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

/**
 * CLIENTE SOAP (Modelo de Servicio)
 *
 * Este 'object' (Singleton) es el responsable de conectarse
 * al servidor WCF (.NET) y obtener los datos.
 *
 * Devuelve objetos del tipo 'Conversion' (tu modelo de datos).
 */
object ClienteConversionSoap {

    // --- Configuración Esencial ---
    private const val TAG = "ClienteSOAP"
    private const val NAMESPACE = "http://tempuri.org/"

    private const val SERVER_IP = "10.40.25.94"
    private const val SERVER_PORT = "8085"
    
    // URL del servicio SOAP para emulador Android local.
    //private const val SERVER_IP = "192.168.100.2"
    //private const val SERVER_PORT = "62533"
    private const val URL = "http://$SERVER_IP:$SERVER_PORT/Service1.svc"
    
    private const val INTERFAZ_NOMBRE = "WSConversion" // (De [ServiceContract(Name = ...)])

    // -----------------------------------------------------------------
    // --- FUNCIÓN GENÉRICA PRIVADA (El "Motor") ---
    // -----------------------------------------------------------------

    /**
     * Motor genérico para llamar a cualquier método de conversión del servicio SOAP.
     * Maneja la creación del sobre, la llamada y el parseo de la respuesta.
     *
     * @param methodName El nombre exacto del [OperationContract] (ej. "celsiusAFahrenheit").
     * @param paramName El nombre exacto del parámetro en C# (ej. "celsius").
     * @param paramValue El valor (Double) a enviar.
     * @return Un objeto 'Conversion' con todos los datos del resultado.
     */
    private suspend fun callConversionMethod(methodName: String, paramName: String, paramValue: Double): Conversion {

        // 1. Crear el objeto 'Conversion' para almacenar todo el proceso
        val conversion = Conversion(
            valorOriginal = paramValue,
            operacion = methodName,
            unidadOriginal = paramName, // Asignamos la unidad original
            unidadConvertida = determinarUnidadConvertida(methodName) // Asignamos la unidad de destino
        )

        // --- Configuración de KSOAP2 ---
        val soapAction = "$NAMESPACE$INTERFAZ_NOMBRE/$methodName"

        val request = SoapObject(NAMESPACE, methodName).apply {
            // Convertimos a String para evitar el error "Cannot serialize"
            addProperty(paramName, paramValue.toString())
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = true // ¡Crítico para WCF .NET!
            setOutputSoapObject(request)
        }

        val transport = HttpTransportSE(URL, 60000) // Timeout de 60 segundos

        // 2. Ejecutar la llamada
        try {
            Log.d(TAG, "Llamando a $methodName con valor $paramValue...")
            transport.call(soapAction, envelope)

            // 3. Procesar la Respuesta (Éxito)
            val response = envelope.response as SoapPrimitive
            val result = response.toString().toDouble()

            // Llenar el objeto 'Conversion' con el éxito
            conversion.valorConvertido = result
            conversion.exitosa = true
            Log.d(TAG, "Respuesta de $methodName: $result")

        } catch (e: Exception) {
            Log.e(TAG, "Error en la llamada a $methodName: ${e.message}", e)

            // 4. Procesar la Respuesta (Error)
            // Llenar el objeto 'Conversion' con el error
            conversion.exitosa = false
            conversion.mensajeError = e.message ?: "Error desconocido"
        }

        // 5. Devolver el objeto 'Conversion' completo
        return conversion
    }

    /**
     * Función helper para asignar la unidad de destino basado en el nombre del método
     */
    private fun determinarUnidadConvertida(methodName: String): String {
        return when {
            // Temperatura
            methodName.contains("Fahrenheit") -> "fahrenheit"
            methodName.contains("Celsius") -> "celsius"
            methodName.contains("Kelvin") -> "kelvin"
            // Longitud
            methodName.contains("Pies") -> "pies"
            methodName.contains("Metros") -> "metros"
            methodName.contains("Pulgadas") -> "pulgadas"
            methodName.contains("Millas") -> "millas"
            methodName.contains("Kilometros") -> "kilometros"
            // Peso
            methodName.contains("Libras") -> "libras"
            methodName.contains("Kilogramos") -> "kilogramos"
            methodName.contains("Onzas") -> "onzas"
            methodName.contains("Gramos") -> "gramos"


            else -> "desconocida"
        }
    }

    // -----------------------------------------------------------------
    // --- MÉTODOS PÚBLICOS (La API que usará tu App) ---
    // --- Ahora todos devuelven el objeto 'Conversion' ---
    // -----------------------------------------------------------------

    // --- Temperatura ---
    suspend fun celsiusAFahrenheit(celsius: Double) =
        callConversionMethod("celsiusAFahrenheit", "celsius", celsius)

    suspend fun fahrenheitACelsius(fahrenheit: Double) =
        callConversionMethod("fahrenheitACelsius", "fahrenheit", fahrenheit)

    suspend fun celsiusAKelvin(celsius: Double) =
        callConversionMethod("celsiusAKelvin", "celsius", celsius)

    suspend fun kelvinACelsius(kelvin: Double) =
        callConversionMethod("kelvinACelsius", "kelvin", kelvin)

    suspend fun fahrenheitAKelvin(fahrenheit: Double) =
        callConversionMethod("fahrenheitAKelvin", "fahrenheit", fahrenheit)

    suspend fun kelvinAFahrenheit(kelvin: Double) =
        callConversionMethod("kelvinAFahrenheit", "kelvin", kelvin)

    // --- Longitud ---
    suspend fun metrosAPies(metros: Double) =
        callConversionMethod("metrosAPies", "metros", metros)

    suspend fun piesAMetros(pies: Double) =
        callConversionMethod("piesAMetros", "pies", pies)

    suspend fun metrosAPulgadas(metros: Double) =
        callConversionMethod("metrosAPulgadas", "metros", metros)

    suspend fun pulgadasAMetros(pulgadas: Double) =
        callConversionMethod("pulgadasAMetros", "pulgadas", pulgadas)

    suspend fun kilometrosAMillas(kilometros: Double) =
        callConversionMethod("kilometrosAMillas", "kilometros", kilometros)

    suspend fun millasAKilometros(millas: Double) =
        callConversionMethod("millasAKilometros", "millas", millas)

    // --- Peso/Masa ---
    suspend fun kilogramosALibras(kilogramos: Double) =
        callConversionMethod("kilogramosALibras", "kilogramos", kilogramos)

    suspend fun librasAKilogramos(libras: Double) =
        callConversionMethod("librasAKilogramos", "libras", libras)

    suspend fun gramosAOnzas(gramos: Double) =
        callConversionMethod("gramosAOnzas", "gramos", gramos)

    suspend fun onzasAGramos(onzas: Double) =
        callConversionMethod("onzasAGramos", "onzas", onzas)

}
