package ec.edu.monster.conversion.service;

import ec.edu.monster.conversion.dto.ConversionRequest;
import ec.edu.monster.conversion.dto.ConversionResponse;
import ec.edu.monster.conversion.exception.UnidadInvalidaException;
import ec.edu.monster.conversion.model.Magnitud;
import org.springframework.stereotype.Service;

/**
 * Motor de conversion. Separa la logica de negocio de los controladores REST.
 * <p>
 * Conversiones lineales (longitud, masa) usan factores respecto a una unidad base.
 * Conversiones no lineales (temperatura) usan formulas hacia/desde Celsius.
 */
@Service
public class ConversionService {

    private final UnidadCatalog catalogo;

    public ConversionService(UnidadCatalog catalogo) {
        this.catalogo = catalogo;
    }

    public ConversionResponse convertir(Magnitud magnitud, ConversionRequest peticion) {
        String origen = peticion.unidadOrigen().trim().toLowerCase();
        String destino = peticion.unidadDestino().trim().toLowerCase();

        validar(magnitud, origen);
        validar(magnitud, destino);

        double resultado = switch (magnitud) {
            case LONGITUD, MASA -> lineal(magnitud, peticion.valor(), origen, destino);
            case TEMPERATURA -> temperatura(peticion.valor(), origen, destino);
        };

        double redondeado = redondear(resultado);
        String mensaje = String.format("%s %s equivalen a %s %s",
                formato(peticion.valor()), origen, formato(redondeado), destino);

        return new ConversionResponse(true, magnitud.name().toLowerCase(),
                peticion.valor(), origen, destino, redondeado, mensaje);
    }

    private double lineal(Magnitud magnitud, double valor, String origen, String destino) {
        double factorOrigen = catalogo.factores(magnitud).get(origen);
        double factorDestino = catalogo.factores(magnitud).get(destino);
        return valor * factorOrigen / factorDestino;
    }

    private double temperatura(double valor, String origen, String destino) {
        return desdeCelsius(destino, aCelsius(origen, valor));
    }

    private double aCelsius(String unidad, double valor) {
        return switch (unidad) {
            case "celsius" -> valor;
            case "fahrenheit" -> (valor - 32.0) * 5.0 / 9.0;
            case "kelvin" -> valor - 273.15;
            case "newton" -> valor * 100.0 / 33.0;
            case "reaumur" -> valor * 5.0 / 4.0;
            default -> throw new UnidadInvalidaException("Unidad de temperatura no soportada: " + unidad);
        };
    }

    private double desdeCelsius(String unidad, double celsius) {
        return switch (unidad) {
            case "celsius" -> celsius;
            case "fahrenheit" -> celsius * 9.0 / 5.0 + 32.0;
            case "kelvin" -> celsius + 273.15;
            case "newton" -> celsius * 33.0 / 100.0;
            case "reaumur" -> celsius * 4.0 / 5.0;
            default -> throw new UnidadInvalidaException("Unidad de temperatura no soportada: " + unidad);
        };
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
