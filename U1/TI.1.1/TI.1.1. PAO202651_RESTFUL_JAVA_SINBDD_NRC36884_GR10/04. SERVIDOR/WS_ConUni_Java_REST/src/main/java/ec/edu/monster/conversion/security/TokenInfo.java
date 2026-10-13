package ec.edu.monster.conversion.security;

import java.time.Instant;

/**
 * Informacion de un token emitido y almacenado en memoria.
 */
public record TokenInfo(String valor, String usuario, Instant creadoEn, Instant expiraEn) {

    public boolean activo() {
        return Instant.now().isBefore(expiraEn);
    }

    public long segundosRestantes() {
        return Math.max(0, expiraEn.getEpochSecond() - Instant.now().getEpochSecond());
    }
}
