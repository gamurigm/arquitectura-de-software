package ec.edu.monster.conversion.modelo.utilidades;

public interface IConversor<TUnidad> {
    double convertir(double valor, TUnidad unidadOrigen, TUnidad unidadDestino);
}
