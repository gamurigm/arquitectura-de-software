namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record TokenRespuesta(
    bool Exito,
    string Token,
    string Tipo,
    string Usuario,
    long ExpiraEnSegundos,
    string Mensaje);
