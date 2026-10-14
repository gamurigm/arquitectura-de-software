package ec.edu.monster.modelo;

/** Cuerpo de las peticiones de conversion. */
public class SolicitudConversion {

    private final double valor;
    private final String unidadOrigen;
    private final String unidadDestino;

    public SolicitudConversion(double valor, String unidadOrigen, String unidadDestino) {
        this.valor = valor;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
    }

    public double obtenerValor() {
        return valor;
    }

    public String obtenerUnidadOrigen() {
        return unidadOrigen;
    }

    public String obtenerUnidadDestino() {
        return unidadDestino;
    }
}
