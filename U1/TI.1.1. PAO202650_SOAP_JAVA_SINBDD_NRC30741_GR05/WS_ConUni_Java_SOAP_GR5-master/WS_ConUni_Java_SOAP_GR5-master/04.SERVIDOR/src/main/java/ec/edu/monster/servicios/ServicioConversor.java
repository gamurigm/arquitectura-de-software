package ec.edu.monster.servicios;

import ec.edu.monster.modelos.utilidades.IConversor;

public class ServicioConversor<T> {

    private final IConversor<T> conversor;

    public ServicioConversor(IConversor<T> conversor) {
        this.conversor = conversor;
    }

    public double convertir(double valor, T origen, T destino) {
        if (conversor == null) {
            throw new IllegalStateException("No se ha inyectado un conversor");
        }
        return conversor.convertir(valor, origen, destino);
    }
}