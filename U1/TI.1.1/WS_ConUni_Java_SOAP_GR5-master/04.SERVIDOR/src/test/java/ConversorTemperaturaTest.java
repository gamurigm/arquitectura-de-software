import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import ec.edu.monster.modelos.utilidades.ConversorTemperatura;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConversorTemperaturaTest {

    private final ConversorTemperatura conversor = new ConversorTemperatura();

    @ParameterizedTest
    @CsvSource({
        "0, CELSIUS, FAHRENHEIT, 32.0",
        "32, FAHRENHEIT, CELSIUS, 0.0",
        "0, CELSIUS, KELVIN, 273.15",
        "273.15, KELVIN, CELSIUS, 0.0",
        "0, CELSIUS, RANKINE, 491.67",
        "491.67, RANKINE, CELSIUS, 0.0",
        "32, FAHRENHEIT, RANKINE, 491.67",
        "491.67, RANKINE, FAHRENHEIT, 32.0",
        "273.15, KELVIN, RANKINE, 491.67",
        "491.67, RANKINE, KELVIN, 273.15"
    })
    void deberiaConvertirTemperatura(double valor,
                                     UnidadTemperatura origen,
                                     UnidadTemperatura destino,
                                     double esperado) {

        double resultado = conversor.convertir(valor, origen, destino);

        assertEquals(esperado, resultado, 0.0001);
    }
}