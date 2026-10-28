using System.Runtime.Serialization;

namespace ec.edu.monster.Modelos;

[DataContract(Name = "Cuenta", Namespace = "http://ws.monster.edu.ec/")]
public sealed record Cuenta(
    [property: DataMember(Name = "numero", Order = 1)] string Numero,
    [property: DataMember(Name = "moneda", Order = 2)] string Moneda,
    [property: DataMember(Name = "saldo", Order = 3)] decimal Saldo);
