using Microsoft.Data.SqlClient;

namespace EurekaBank.Api.Datos;

public sealed class SqlServerConnectionFactory(string? connectionString) : ISqlConnectionFactory
{
    public SqlConnection CreateConnection()
    {
        if (string.IsNullOrWhiteSpace(connectionString))
        {
            throw new InvalidOperationException(
                "Configure EUREKABANK_DB_CONNECTION_STRING con una cadena local de SQL Server.");
        }

        return new SqlConnection(connectionString);
    }
}
