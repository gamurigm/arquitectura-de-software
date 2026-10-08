using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

// Mock para Autenticación
public class AdaptadorAutenticacionMock : IAdaptadorAutenticacion
{
    public TipoBackend Tipo { get; }

    public AdaptadorAutenticacionMock(TipoBackend tipo)
    {
        Tipo = tipo;
    }

    public async Task<string> IniciarSesionAsync(string usuario, string contrasenia)
    {
        await Task.Delay(500); // Simulamos latencia de red
        if (usuario == "Monster" && contrasenia == "Monster9")
            return $"TOKEN-FALSO-{Tipo}";

        throw new Exception("Credenciales incorrectas (Mock)");
    }

    public Task<string> CambiarContraseniaAsync(string contraseniaActual, string contraseniaNueva, string token)
    {
        throw new NotImplementedException();
    }
}

// Mock para Conversión
public class AdaptadorConversionMock : IAdaptadorConversion
{
    public TipoBackend Tipo { get; }

    public AdaptadorConversionMock(TipoBackend tipo)
    {
        Tipo = tipo;
    }

    public async Task<double> ConvertirAsync(PeticionConversion peticion, string token)
    {
        await Task.Delay(500); // Simulamos latencia de red
        // Simplemente devolvemos un número aleatorio para probar la UI
        return peticion.Valor * 2.5;
    }
}