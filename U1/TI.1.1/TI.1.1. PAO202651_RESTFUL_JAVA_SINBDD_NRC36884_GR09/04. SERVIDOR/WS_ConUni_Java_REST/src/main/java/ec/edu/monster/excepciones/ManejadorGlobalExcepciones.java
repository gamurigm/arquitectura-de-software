package ec.edu.monster.excepciones;

import ec.edu.monster.modelo.dto.RespuestaError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Manejo centralizado de errores: garantiza que el cliente siempre reciba un JSON con
 * estado, mensaje y código HTTP adecuado.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

    @ExceptionHandler(ExcepcionApi.class)
    public ResponseEntity<RespuestaError> manejarApi(ExcepcionApi ex, HttpServletRequest solicitud) {
        return construir(ex.obtenerEstadoHttp(), ex.obtenerError(), ex.getMessage(), solicitud);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest solicitud) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", detalle, solicitud);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> manejarCuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest solicitud) {
        return construir(HttpStatus.BAD_REQUEST, "Solicitud inválida",
                "El cuerpo de la solicitud no tiene el formato JSON esperado.", solicitud);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaError> manejarNoEncontrado(NoResourceFoundException ex, HttpServletRequest solicitud) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado",
                "La ruta solicitada no existe: " + solicitud.getRequestURI(), solicitud);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarGeneral(Exception ex, HttpServletRequest solicitud) {
        LOGGER.error("Error no controlado al procesar {} {}", solicitud.getMethod(), solicitud.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error interno en el servidor. Intente nuevamente más tarde.", solicitud);
    }

    private ResponseEntity<RespuestaError> construir(HttpStatus estadoHttp, String codigoError, String mensaje, HttpServletRequest solicitud) {
        RespuestaError cuerpo = new RespuestaError(false, estadoHttp.value(), codigoError, mensaje,
                solicitud.getRequestURI(), OffsetDateTime.now());
        return ResponseEntity.status(estadoHttp).body(cuerpo);
    }
}
