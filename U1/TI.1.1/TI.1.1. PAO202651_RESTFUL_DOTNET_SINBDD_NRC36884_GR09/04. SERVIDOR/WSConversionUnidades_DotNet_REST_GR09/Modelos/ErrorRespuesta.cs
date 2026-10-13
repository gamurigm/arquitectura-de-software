namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record ErrorRespuesta(
    bool Exito,
    int Codigo,
    string Error,
    string Mensaje,
    string Ruta,
    DateTimeOffset Timestamp);
