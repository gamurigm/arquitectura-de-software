namespace WSConversionUnidades_DotNet_REST_GR09.Modelos.Utilidades;

public interface IConversor<TUnidad>
{
    double Convertir(double valor, TUnidad unidadOrigen, TUnidad unidadDestino);
}
