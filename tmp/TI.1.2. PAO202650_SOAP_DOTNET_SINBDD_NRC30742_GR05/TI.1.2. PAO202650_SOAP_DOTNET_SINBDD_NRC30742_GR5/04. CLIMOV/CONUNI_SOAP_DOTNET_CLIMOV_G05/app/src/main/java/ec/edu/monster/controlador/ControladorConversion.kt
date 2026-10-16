package ec.edu.monster.controlador

import ec.edu.monster.modelo.Conversion
import ec.edu.monster.servidor.ClienteConversionSoap // Importa tu Modelo de Servicio

/**
 * CONTROLADOR (MVC)
 * Maneja la lógica de negocio. Es el intermediario entre la Vista (Activity)
 * y el Modelo (ClienteConversionSoap).
 */
class ControladorConversion {

    // NOTA: No necesitamos 'new' porque ClienteConversionSoap es un 'object' (Singleton)
    private val clienteSOAP = ClienteConversionSoap

    /**
     * Valida si un valor es numérico válido
     */
    fun validarValor(valor: String): Double? {
        return try {
            valor.trim().toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Delega la llamada de conversión al Modelo (ClienteConversionSoap)
     * basado en el tipo de operación.
     */
    suspend fun convertirTemperatura(operacion: String, valor: Double): Conversion {
        // Llama al Modelo (ClienteSOAP) según la operación
        return when (operacion) {
            "celsiusAFahrenheit" -> clienteSOAP.celsiusAFahrenheit(valor)
            "fahrenheitACelsius" -> clienteSOAP.fahrenheitACelsius(valor)
            "celsiusAKelvin" -> clienteSOAP.celsiusAKelvin(valor)
            "kelvinACelsius" -> clienteSOAP.kelvinACelsius(valor)
            "fahrenheitAKelvin" -> clienteSOAP.fahrenheitAKelvin(valor)
            "kelvinAFahrenheit" -> clienteSOAP.kelvinAFahrenheit(valor)
            else -> crearErrorConversion("Operación de temperatura no válida: $operacion")
        }
    }

    suspend fun convertirLongitud(operacion: String, valor: Double): Conversion {
        return when (operacion) {
            "metrosAPies" -> clienteSOAP.metrosAPies(valor)
            "piesAMetros" -> clienteSOAP.piesAMetros(valor)
            "metrosAPulgadas" -> clienteSOAP.metrosAPulgadas(valor)
            "pulgadasAMetros" -> clienteSOAP.pulgadasAMetros(valor)
            "kilometrosAMillas" -> clienteSOAP.kilometrosAMillas(valor)
            "millasAKilometros" -> clienteSOAP.millasAKilometros(valor)
            else -> crearErrorConversion("Operación de longitud no válida: $operacion")
        }
    }

    suspend fun convertirPeso(operacion: String, valor: Double): Conversion {
        return when (operacion) {
            "kilogramosALibras" -> clienteSOAP.kilogramosALibras(valor)
            "librasAKilogramos" -> clienteSOAP.librasAKilogramos(valor)
            "gramosAOnzas" -> clienteSOAP.gramosAOnzas(valor)
            "onzasAGramos" -> clienteSOAP.onzasAGramos(valor)
            else -> {
                val error = Conversion()
                error.exitosa = false
                error.mensajeError = "Operación de peso no válida: $operacion"
                error
            }
        }
    }


    /**
     * Obtiene la lista de operaciones de temperatura para la Vista (ej. un Spinner)
     */
    fun getOperacionesTemperatura(): List<Pair<String, String>> {
        return listOf(
            "celsiusAFahrenheit" to "Celsius -> Fahrenheit",
            "fahrenheitACelsius" to "Fahrenheit -> Celsius",
            "celsiusAKelvin" to "Celsius -> Kelvin",
            "kelvinACelsius" to "Kelvin -> Celsius",
            "fahrenheitAKelvin" to "Fahrenheit -> Kelvin",
            "kelvinAFahrenheit" to "Kelvin -> Fahrenheit"
        )
    }
    fun getOperacionesLongitud(): List<Pair<String, String>> {
        return listOf(
            "metrosAPies" to "Metros -> Pies",
            "piesAMetros" to "Pies -> Metros",
            "metrosAPulgadas" to "Metros -> Pulgadas",
            "pulgadasAMetros" to "Pulgadas -> Metros",
            "kilometrosAMillas" to "Kilómetros -> Millas",
            "millasAKilometros" to "Millas -> Kilómetros"
        )
    }
    fun getOperacionesPeso(): List<Pair<String, String>> {
        return listOf(
            "kilogramosALibras" to "Kilogramos -> Libras",
            "librasAKilogramos" to "Libras -> Kilogramos",
            "gramosAOnzas" to "Gramos -> Onzas",
            "onzasAGramos" to "Onzas -> Gramos"
        )
    }

    /**
     * Helper para crear un objeto de error
     */
    private fun crearErrorConversion(mensaje: String): Conversion {
        return Conversion(
            exitosa = false,
            mensajeError = mensaje
        )
    }
}