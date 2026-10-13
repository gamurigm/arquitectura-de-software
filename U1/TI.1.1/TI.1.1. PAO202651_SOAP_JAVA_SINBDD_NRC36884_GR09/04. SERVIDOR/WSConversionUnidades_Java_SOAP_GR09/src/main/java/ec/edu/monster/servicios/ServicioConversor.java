package ec.edu.monster.servicios;

import ec.edu.monster.modelos.utilidades.IConversor;
import java.util.Objects;

public class ServicioConversor<T> {

    private final IConversor<T> conversor;

    public ServicioConversor(IConversor<T> conversor) {
        this.conversor = Objects.requireNonNull(conversor, "El conversor es obligatorio");
    }

    public double convertir(double valor, T origen, T destino) {
        return conversor.convertir(valor, origen, destino);
    }
}