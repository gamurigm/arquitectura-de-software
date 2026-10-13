package ec.edu.monster.climov.api;

/** Cuerpo de las peticiones de conversion. */
public class ConversionRequest {

    private final double valor;
    private final String unidadOrigen;
    private final String unidadDestino;

    public ConversionRequest(double valor, String unidadOrigen, String unidadDestino) {
        this.valor = valor;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }
}
