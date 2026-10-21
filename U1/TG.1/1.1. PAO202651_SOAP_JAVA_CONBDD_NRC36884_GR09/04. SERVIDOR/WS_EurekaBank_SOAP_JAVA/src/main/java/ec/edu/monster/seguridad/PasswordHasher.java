package ec.edu.monster.seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2-sha256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String crearHash(char[] contrasena) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(contrasena, salt, ITERATIONS);
        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getUrlEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public static boolean verificar(char[] contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null) {
            return false;
        }

        String[] partes = hashGuardado.split("\\$", -1);
        if (partes.length != 4 || !PREFIX.equals(partes[0])) {
            return false;
        }

        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getUrlDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getUrlDecoder().decode(partes[3]);
            if (iteraciones != ITERATIONS || salt.length != SALT_BYTES
                    || hashEsperado.length != KEY_BITS / 8) {
                return false;
            }
            return MessageDigest.isEqual(hashEsperado, derivar(contrasena, salt, iteraciones));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(char[] contrasena, byte[] salt, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(contrasena, salt, iteraciones, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la credencial.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
