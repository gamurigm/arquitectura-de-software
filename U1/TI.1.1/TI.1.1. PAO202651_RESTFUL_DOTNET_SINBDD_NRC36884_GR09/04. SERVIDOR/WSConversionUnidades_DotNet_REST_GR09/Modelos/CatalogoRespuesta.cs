namespace WSConversionUnidades_DotNet_REST_GR09.Modelos;

public sealed record CatalogoRespuesta(
    bool Exito,
    Dictionary<string, List<string>> Magnitudes,
    string Mensaje);
