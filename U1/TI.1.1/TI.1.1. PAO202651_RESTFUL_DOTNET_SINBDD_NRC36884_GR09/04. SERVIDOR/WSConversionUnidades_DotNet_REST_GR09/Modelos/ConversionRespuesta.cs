namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record ConversionRespuesta(
    bool Exito,
    string Magnitud,
    double Valor,
    string UnidadOrigen,
    string UnidadDestino,
    double Resultado,
    string Mensaje);
