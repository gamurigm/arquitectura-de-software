namespace ClienteMovilGR09.Modelos;

public sealed record LoginSolicitud(string Usuario, string Clave);

public sealed record TokenRespuesta(
    bool Exito,
    string Token,
    string Tipo,
    string Usuario,
    long ExpiraEnSegundos,
    string Mensaje);

public sealed record ConversionSolicitud(double Valor, string UnidadOrigen, string UnidadDestino);

public sealed record ConversionRespuesta(
    bool Exito,
    string Magnitud,
    double Valor,
    string UnidadOrigen,
    string UnidadDestino,
    double Resultado,
    string Mensaje);

public sealed record CatalogoRespuesta(
    bool Exito,
    Dictionary<string, List<string>> Magnitudes,
    string Mensaje);

public sealed record ErrorRespuesta(
    bool Exito,
    int Codigo,
    string Error,
    string Mensaje,
    string Ruta,
    DateTimeOffset Timestamp);

public sealed record RespuestaOperacion(bool Exito, string Mensaje);

public sealed class ErrorServicioException(int codigo, string mensaje) : InvalidOperationException(mensaje)
{
    public int Codigo { get; } = codigo;
}

public sealed class EstadoCliente
{
    public string? Token { get; set; }
    public string Usuario { get; set; } = "";
}
