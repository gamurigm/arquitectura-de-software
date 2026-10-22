using Microsoft.Data.SqlClient;

namespace ec.edu.monster.Conexion;

public sealed class ConexionDb
{
    private static readonly Lazy<ConexionDb> InstanciaLazy = new(() => new ConexionDb());
    private readonly string _cadenaConexion;

    private ConexionDb()
    {
        _cadenaConexion = Environment.GetEnvironmentVariable("EUREKABANK_DB_CONNECTION_STRING")
            ?? throw new InvalidOperationException("Defina EUREKABANK_DB_CONNECTION_STRING antes de iniciar el servidor.");
        if (string.IsNullOrWhiteSpace(_cadenaConexion))
            throw new InvalidOperationException("EUREKABANK_DB_CONNECTION_STRING no puede estar vacía.");
    }

    public static ConexionDb Instancia => InstanciaLazy.Value;

    public SqlConnection CrearConexion() => new(_cadenaConexion);
}
