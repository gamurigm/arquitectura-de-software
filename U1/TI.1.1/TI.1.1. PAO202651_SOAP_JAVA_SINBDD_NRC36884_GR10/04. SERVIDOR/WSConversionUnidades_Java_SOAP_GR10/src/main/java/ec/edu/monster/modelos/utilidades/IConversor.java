package ec.edu.monster.modelos.utilidades;

public interface IConversor<T> {
    double convertir(double valor, T unidadInicial, T unidadFinal);
}