package ec.edu.monster.seguridad;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PruebaAdministradorTokens {

    @Test
    void tokenValidoAntesDeQuinceMinutosYExpiradoDespues() {
        String token = AdministradorTokens.generarToken();

        assertTrue(AdministradorTokens.validarToken(token));
        assertTrue(AdministradorTokens.validarToken(token, Instant.now().plus(Duration.ofMinutes(14))));
        assertFalse(AdministradorTokens.validarToken(token, Instant.now().plus(Duration.ofMinutes(16))));
        assertFalse(AdministradorTokens.validarToken(token));
    }
}
