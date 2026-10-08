import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import ec.edu.monster.modelos.utilidades.ConversorMasa;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConversorMasaTest {

    private final ConversorMasa conversor = new ConversorMasa();

    @ParameterizedTest
    @CsvSource({
        "1000, GRAMO, KILOGRAMO, 1.0",
        "1, KILOGRAMO, GRAMO, 1000.0",
        "500, GRAMO, KILOGRAMO, 0.5"
    })
    void deberiaConvertirCorrectamente(double valor,
                                       UnidadMasa origen,
                                       UnidadMasa destino,
                                       double esperado) {

        double resultado = conversor.convertir(valor, origen, destino);

        assertEquals(esperado, resultado, 0.0001);
    }
}