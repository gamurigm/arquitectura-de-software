using System.Runtime.Serialization;

namespace ec.edu.monster.Modelos;

[DataContract(Name = "Movimiento", Namespace = "http://ws.monster.edu.ec/")]
public sealed class Movimiento
{
    [DataMember(Name = "accion", Order = 1)] public string Accion { get; init; } = "";
    [DataMember(Name = "cuenta", Order = 2)] public string Cuenta { get; init; } = "";
    [DataMember(Name = "fecha", Order = 3)] public DateTime Fecha { get; init; }
    [DataMember(Name = "importe", Order = 4)] public decimal Importe { get; init; }
    [DataMember(Name = "nromov", Order = 5)] public int Nromov { get; init; }
    [DataMember(Name = "tipo", Order = 6)] public string Tipo { get; init; } = "";
}
