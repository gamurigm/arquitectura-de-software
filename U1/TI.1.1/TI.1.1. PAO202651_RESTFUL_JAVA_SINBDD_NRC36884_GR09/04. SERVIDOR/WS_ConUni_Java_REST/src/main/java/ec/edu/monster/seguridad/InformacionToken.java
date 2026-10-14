package ec.edu.monster.seguridad;

import java.time.Instant;

/**
 * Información de un token emitido y almacenado en memoria.
 */
public record InformacionToken(String valor, String usuario, Instant creadoEn, Instant expiraEn) {

    public boolean activo() {
        return Instant.now().isBefore(expiraEn);
    }

    public long segundosRestantes() {
        return Math.max(0, expiraEn.getEpochSecond() - Instant.now().getEpochSecond());
    }
}
