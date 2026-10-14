package ec.edu.monster.modelo;

public final class ConversionModelo {

    private final String tipo;
    private final double valor;
    private final String origen;
    private final String destino;

    public ConversionModelo(String tipo, double valor, String origen, String destino) {
        this.tipo = tipo;
        this.valor = valor;
        this.origen = origen;
        this.destino = destino;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }
}
