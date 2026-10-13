package ec.edu.monster.conversion.servicios;

import ec.edu.monster.conversion.modelo.Magnitud;
import ec.edu.monster.conversion.modelo.UnidadLongitud;
import ec.edu.monster.conversion.modelo.UnidadMasa;
import ec.edu.monster.conversion.modelo.UnidadTemperatura;
import ec.edu.monster.conversion.modelo.utilidades.IConversor;
import ec.edu.monster.conversion.modelo.dto.ConversionRequest;
import ec.edu.monster.conversion.modelo.dto.ConversionResponse;
import ec.edu.monster.conversion.excepciones.UnidadInvalidaException;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Motor de conversion. Separa la logica de negocio de los controladores REST.
 * <p>
 * Valida solicitudes y delega cada magnitud a su conversor especializado.
 */
@Service
public class ConversionService {

    private final UnidadCatalog catalogo;
    private final IConversor<UnidadLongitud> conversorLongitud;
    private final IConversor<UnidadMasa> conversorMasa;
    private final IConversor<UnidadTemperatura> conversorTemperatura;

    public ConversionService(
            UnidadCatalog catalogo,
            IConversor<UnidadLongitud> conversorLongitud,
            IConversor<UnidadMasa> conversorMasa,
            IConversor<UnidadTemperatura> conversorTemperatura) {
        this.catalogo = catalogo;
        this.conversorLongitud = conversorLongitud;
        this.conversorMasa = conversorMasa;
        this.conversorTemperatura = conversorTemperatura;
    }

    public ConversionResponse convertir(Magnitud magnitud, ConversionRequest peticion) {
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

        double redondeado = redondear(resultado);
        String mensaje = String.format("%s %s equivalen a %s %s",
                formato(peticion.valor()), origen, formato(redondeado), destino);

        return new ConversionResponse(true, magnitud.name().toLowerCase(),
                peticion.valor(), origen, destino, redondeado, mensaje);
    }

    private void validar(Magnitud magnitud, String unidad) {
        if (!catalogo.esUnidadValida(magnitud, unidad)) {
            throw new UnidadInvalidaException("La unidad '" + unidad + "' no es valida para "
                    + magnitud.name().toLowerCase() + ". Disponibles: " + catalogo.unidades(magnitud));
        }
    }

    private double redondear(double valor) {
        return Math.round(valor * 1_000_000.0) / 1_000_000.0;
    }

    private String formato(double valor) {
        if (valor == Math.rint(valor) && !Double.isInfinite(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }
}
