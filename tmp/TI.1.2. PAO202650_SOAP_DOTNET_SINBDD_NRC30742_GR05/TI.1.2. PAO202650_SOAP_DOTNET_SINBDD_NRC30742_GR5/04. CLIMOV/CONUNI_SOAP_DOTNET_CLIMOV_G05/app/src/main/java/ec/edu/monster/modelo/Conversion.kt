package ec.edu.monster.modelo

/**
 * Modelo de datos para las conversiones (Data Class).
 * Representa todos los atributos y el estado de una
 * operación de conversión.
 */
data class Conversion(
    // Atributos de la Petición
    var valorOriginal: Double = 0.0,
    var unidadOriginal: String = "",
    var operacion: String = "",

    // Atributos de la Respuesta
    var valorConvertido: Double = 0.0,
    var unidadConvertida: String = "",

    // Atributos de Estado
    var exitosa: Boolean = false,
    var mensajeError: String = ""

    // (Puedes añadir más atributos si los necesitas,
    // como 'tipoConversion: String', etc.)
) {

    /**
     * Método de ayuda (helper) para obtener el resultado
     * formateado para mostrar al usuario.
     */
    fun getResultadoFormateado(): String {
        return if (exitosa) {
            // Formatea a 2 decimales
            String.format("%.2f %s = %.2f %s",
                valorOriginal, unidadOriginal, valorConvertido, unidadConvertida)
        } else {
            "ERROR: $mensajeError"
        }
    }

    /**
     * Obtiene el símbolo de la unidad original para mostrar en la UI
     */
    fun getSimboloOriginal(): String {
        return when (unidadOriginal.lowercase()) {
            "celsius" -> "°C"
            "fahrenheit" -> "°F"
            "kelvin" -> "K"
            "metros" -> "m"
            "pies" -> "ft"
            "pulgadas" -> "in"
            "kilometros" -> "km"
            "millas" -> "mi"
            "kilogramos" -> "kg"
            "libras" -> "lb"
            "gramos" -> "g"
            "onzas" -> "oz"
            "litros" -> "L"
            "galones" -> "gal"
            "mililitros" -> "mL"
            "onzasfluidas" -> "fl oz"
            "metroscuadrados" -> "m²"
            "piescuadrados" -> "ft²"
            "hectareas" -> "ha"
            "acres" -> "ac"
            else -> unidadOriginal // Devuelve el nombre si no hay símbolo
        }
    }

    /**
     * Obtiene el símbolo de la unidad convertida
     */
    fun getSimboloConvertido(): String {
        return when (unidadConvertida.lowercase()) {
            "celsius" -> "°C"
            "fahrenheit" -> "°F"
            "kelvin" -> "K"
            "metros" -> "m"
            "pies" -> "ft"
            "pulgadas" -> "in"
            "kilometros" -> "km"
            "millas" -> "mi"
            "kilogramos" -> "kg"
            "libras" -> "lb"
            "gramos" -> "g"
            "onzas" -> "oz"
            "litros" -> "L"
            "galones" -> "gal"
            "mililitros" -> "mL"
            "onzasfluidas" -> "fl oz"
            "metroscuadrados" -> "m²"
            "piescuadrados" -> "ft²"
            "hectareas" -> "ha"
            "acres" -> "ac"
            else -> unidadConvertida
        }
    }

    /**
     * Obtiene el resultado final formateado con símbolos (ej. 20 °C = 68.0 °F)
     */
    fun getResultadoConSimbolos(): String {
        return if (exitosa) {
            String.format("%.2f %s = %.2f %s",
                valorOriginal, getSimboloOriginal(),
                valorConvertido, getSimboloConvertido())
        } else {
            "ERROR: $mensajeError"
        }
    }
}