package ec.edu.monster.modelo;

public enum TipoConversion {
    LONGITUD("Longitud", "longitud", new String[]{"milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"}),
    MASA("Masa", "masa", new String[]{"miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"}),
    TEMPERATURA("Temperatura", "temperatura", new String[]{"celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"});

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
