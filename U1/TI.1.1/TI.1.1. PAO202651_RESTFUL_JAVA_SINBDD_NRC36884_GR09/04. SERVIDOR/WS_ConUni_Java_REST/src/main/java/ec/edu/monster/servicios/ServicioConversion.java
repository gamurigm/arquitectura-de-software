package ec.edu.monster.servicios;

import ec.edu.monster.modelo.Magnitud;
import ec.edu.monster.modelo.UnidadLongitud;
import ec.edu.monster.modelo.UnidadMasa;
import ec.edu.monster.modelo.UnidadTemperatura;
import ec.edu.monster.modelo.utilidades.IConversor;
import ec.edu.monster.modelo.dto.SolicitudConversion;
import ec.edu.monster.modelo.dto.RespuestaConversion;
import ec.edu.monster.excepciones.ExcepcionUnidadInvalida;
import ec.edu.monster.excepciones.ExcepcionApi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Motor de conversion. Separa la logica de negocio de los controladores REST.
 * <p>
 * Valida solicitudes y delega cada magnitud a su conversor especializado.
 */
@Service
public class ServicioConversion {

    private final CatalogoUnidades catalogo;
    private final IConversor<UnidadLongitud> conversorLongitud;
    private final IConversor<UnidadMasa> conversorMasa;
    private final IConversor<UnidadTemperatura> conversorTemperatura;

    public ServicioConversion(
            CatalogoUnidades catalogo,
            IConversor<UnidadLongitud> conversorLongitud,
            IConversor<UnidadMasa> conversorMasa,
            IConversor<UnidadTemperatura> conversorTemperatura) {
        this.catalogo = catalogo;
        this.conversorLongitud = conversorLongitud;
        this.conversorMasa = conversorMasa;
        this.conversorTemperatura = conversorTemperatura;
    }

    public RespuestaConversion convertir(Magnitud magnitud, SolicitudConversion peticion) {
        if (magnitud == null || peticion == null || peticion.valor() == null
                || !Double.isFinite(peticion.valor())) {
            throw new ExcepcionApi(HttpStatus.BAD_REQUEST, "Datos invalidos",
                    "El valor debe ser un número finito y la magnitud es obligatoria");
        }
        if (peticion.unidadOrigen() == null || peticion.unidadOrigen().isBlank()
                || peticion.unidadDestino() == null || peticion.unidadDestino().isBlank()) {
            throw new ExcepcionApi(HttpStatus.BAD_REQUEST, "Datos invalidos",
                    "Las unidades de origen y destino son obligatorias");
        }
        if (magnitud == Magnitud.MASA && peticion.valor() < 0) {
            throw new ExcepcionApi(HttpStatus.BAD_REQUEST, "Datos invalidos", "La masa no puede ser negativa");
        }
        String origen = peticion.unidadOrigen().trim().toLowerCase(Locale.ROOT);
        String destino = peticion.unidadDestino().trim().toLowerCase(Locale.ROOT);

        validar(magnitud, origen);
        validar(magnitud, destino);

        double resultado = switch (magnitud) {
            case LONGITUD -> conversorLongitud.convertir(peticion.valor(),
                    UnidadLongitud.valueOf(origen.toUpperCase(Locale.ROOT)),
                    UnidadLongitud.valueOf(destino.toUpperCase(Locale.ROOT)));
            case MASA -> conversorMasa.convertir(peticion.valor(),
                    UnidadMasa.valueOf(origen.toUpperCase(Locale.ROOT)),
                    UnidadMasa.valueOf(destino.toUpperCase(Locale.ROOT)));
            case TEMPERATURA -> conversorTemperatura.convertir(peticion.valor(),
                    UnidadTemperatura.valueOf(origen.toUpperCase(Locale.ROOT)),
                    UnidadTemperatura.valueOf(destino.toUpperCase(Locale.ROOT)));
        };
        if (!Double.isFinite(resultado)) {
            throw new ExcepcionApi(HttpStatus.BAD_REQUEST, "Datos invalidos",
                    "El resultado no se puede representar como un número finito");
        }

        double redondeado = redondear(resultado);
        String mensaje = String.format("%s %s equivalen a %s %s",
                formato(peticion.valor()), origen, formato(redondeado), destino);

        return new RespuestaConversion(true, magnitud.name().toLowerCase(),
                peticion.valor(), origen, destino, redondeado, mensaje);
    }

    private void validar(Magnitud magnitud, String unidad) {
        if (!catalogo.esUnidadValida(magnitud, unidad)) {
            throw new ExcepcionUnidadInvalida("La unidad '" + unidad + "' no es valida para "
                    + magnitud.name().toLowerCase() + ". Disponibles: " + catalogo.unidades(magnitud));
        }
    }

    private double redondear(double valor) {
        if (Math.abs(valor) >= Long.MAX_VALUE / 1_000_000.0) {
            return valor;
        }
        return Math.round(valor * 1_000_000.0) / 1_000_000.0;
    }

    private String formato(double valor) {
        if (valor == Math.rint(valor) && Double.isFinite(valor) && Math.abs(valor) <= Long.MAX_VALUE) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }
}
