package ec.edu.monster.servicios;

import ec.edu.monster.modelo.SolicitudTransferencia;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.regex.Pattern;

public final class TransferenciaValidator {
    private static final Pattern CUENTA = Pattern.compile("^[0-9]{8}$");
    private static final BigDecimal MAXIMO = new BigDecimal("9999999999.99");

    private TransferenciaValidator() { }

    public static SolicitudTransferencia normalizar(SolicitudTransferencia solicitud) {
        if (solicitud == null || solicitud.getImporte() == null || solicitud.getClaveOperacion() == null
                || solicitud.getImporte().signum() <= 0 || solicitud.getImporte().compareTo(MAXIMO) > 0) return null;
        try {
            solicitud.setImporte(solicitud.getImporte().setScale(2, RoundingMode.UNNECESSARY));
            UUID.fromString(solicitud.getClaveOperacion());
        } catch (ArithmeticException | IllegalArgumentException e) {
            return null;
        }
        String origen = solicitud.getCuentaOrigen() == null ? "" : solicitud.getCuentaOrigen().strip();
        String destino = solicitud.getCuentaDestino() == null ? "" : solicitud.getCuentaDestino().strip();
        if (!CUENTA.matcher(origen).matches() || !CUENTA.matcher(destino).matches() || origen.equals(destino)) return null;
        solicitud.setCuentaOrigen(origen);
        solicitud.setCuentaDestino(destino);
        return solicitud;
    }
}
