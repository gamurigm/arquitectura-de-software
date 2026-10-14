namespace WSConversionUnidades_DotNet_REST_GR09.Excepciones;

public sealed class ExcepcionApi(
    int Codigo,
    string Error,
    string Mensaje) : Exception(Mensaje)
{
    public int Codigo { get; } = Codigo;
    public string Error { get; } = Error;
}
