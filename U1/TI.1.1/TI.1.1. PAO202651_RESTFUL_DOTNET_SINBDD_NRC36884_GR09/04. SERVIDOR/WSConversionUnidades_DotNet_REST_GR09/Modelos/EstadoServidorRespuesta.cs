namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record EstadoServidorRespuesta(
    bool Exito,
    string Aplicacion,
    string Estado,
    DateTimeOffset Timestamp);
