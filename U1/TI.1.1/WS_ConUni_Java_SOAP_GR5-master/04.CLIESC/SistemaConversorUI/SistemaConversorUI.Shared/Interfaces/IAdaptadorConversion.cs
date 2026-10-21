using ec.edu.monster.Modelos;

namespace ec.edu.monster.Interfaces;

public interface IAdaptadorConversion
{
    TipoBackend Tipo { get; }
    Task<double> ConvertirAsync(PeticionConversion peticion, string token);
}