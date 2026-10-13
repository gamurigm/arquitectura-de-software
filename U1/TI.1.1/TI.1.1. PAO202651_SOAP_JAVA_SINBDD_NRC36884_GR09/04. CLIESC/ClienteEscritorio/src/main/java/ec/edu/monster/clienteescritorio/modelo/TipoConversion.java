package ec.edu.monster.clienteescritorio.modelo;

public enum TipoConversion {
    LONGITUD("Longitud", "longitud", new String[]{"milimetro", "centimetro", "metro", "kilometro", "yarda"}),
    MASA("Masa", "masa", new String[]{"miligramo", "gramo", "kilogramo", "tonelada", "onza"}),
    TEMPERATURA("Temperatura", "temperatura", new String[]{"celsius", "fahrenheit", "kelvin", "rankine"});

    private final String nombreVisible;
    private final String valorServicio;
    private final String[] unidades;

    TipoConversion(String nombreVisible, String valorServicio, String[] unidades) {
        this.nombreVisible = nombreVisible;
        this.valorServicio = valorServicio;
        this.unidades = unidades;
    }

    public String obtenerNombreVisible() {
        return nombreVisible;
    }

    public String obtenerValorServicio() {
        return valorServicio;
    }

    public String[] obtenerUnidades() {
        return unidades.clone();
    }

    @Override
    public String toString() {
        return nombreVisible;
    }
}
