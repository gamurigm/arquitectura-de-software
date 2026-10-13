package ec.edu.monster.climov.api;

/** Respuesta de los endpoints /api/conversiones/*. */
public class ConversionResponse {

    private boolean exito;
    private String magnitud;
    private double valor;
    private String unidadOrigen;
    private double resultado;
    private String unidadDestino;
    private String mensaje;

    public boolean isExito() {
        return exito;
    }

    public String getMagnitud() {
        return magnitud;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public double getResultado() {
        return resultado;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }

    public String getMensaje() {
        return mensaje;
    }
}
