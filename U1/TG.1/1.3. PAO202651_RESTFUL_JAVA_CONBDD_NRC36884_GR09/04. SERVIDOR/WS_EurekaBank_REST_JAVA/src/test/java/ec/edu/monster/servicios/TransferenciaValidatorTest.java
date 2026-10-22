package ec.edu.monster.servicios;

import ec.edu.monster.modelo.SolicitudTransferencia;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class TransferenciaValidatorTest {
    @Test void aceptaSolicitudValida() {
        SolicitudTransferencia solicitud = solicitud("00200001", "00200002", "12.50", true);
        assertSame(solicitud, TransferenciaValidator.normalizar(solicitud));
        assertEquals("00200001", solicitud.getCuentaOrigen());
    }

    @Test void rechazaCuentasRepetidasImporteInvalidoYClaveInvalida() {
        assertNull(TransferenciaValidator.normalizar(solicitud("00200001", "00200001", "1.00", true)));
        assertNull(TransferenciaValidator.normalizar(solicitud("200001", "00200002", "1.00", true)));
        assertNull(TransferenciaValidator.normalizar(solicitud("00200001", "00200002", "0.00", true)));
        assertNull(TransferenciaValidator.normalizar(solicitud("00200001", "00200002", "1.001", true)));
        assertNull(TransferenciaValidator.normalizar(solicitud("00200001", "00200002", "1.00", false)));
    }

    private static SolicitudTransferencia solicitud(String origen, String destino, String importe, boolean claveValida) {
        SolicitudTransferencia solicitud = new SolicitudTransferencia();
        solicitud.setCuentaOrigen(origen);
        solicitud.setCuentaDestino(destino);
        solicitud.setImporte(new BigDecimal(importe));
        solicitud.setClaveOperacion(claveValida ? "3b7a0e7c-571f-4b39-a433-ef1938ff25a2" : "invalida");
        return solicitud;
    }
}
