package ec.edu.monster.modelo;

/** Respuesta de los endpoints /api/conversiones/*. */
public class RespuestaConversion {

    private boolean exito;
    private String magnitud;
    private double valor;
    private String unidadOrigen;
    private double resultado;
    private String unidadDestino;
    private String mensaje;

    public boolean esExitoso() {
        return exito;
    }

    public String obtenerMagnitud() {
        return magnitud;
    }

    public double obtenerValor() {
        return valor;
    }

    public String obtenerUnidadOrigen() {
        return unidadOrigen;
    }

    public double obtenerResultado() {
        return resultado;
    }

    public String obtenerUnidadDestino() {
        return unidadDestino;
    }

    public String obtenerMensaje() {
        return mensaje;
    }
}
