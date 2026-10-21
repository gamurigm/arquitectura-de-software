import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.ConversorLongitud;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConversorLongitudTest {

    private final ConversorLongitud conversor = new ConversorLongitud();

    @ParameterizedTest
    @CsvSource({
        "1000, METRO, KILOMETRO, 1.0",
        "1, KILOMETRO, METRO, 1000.0",
        "100, CENTIMETRO, METRO, 1.0"
    })
    void deberiaConvertirCorrectamente(double valor,
                                       UnidadLongitud origen,
                                       UnidadLongitud destino,
                                       double esperado) {

        double resultado = conversor.convertir(valor, origen, destino);

        assertEquals(esperado, resultado, 0.0001);
    }
}