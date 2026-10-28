using Microsoft.Data.SqlClient;

namespace EurekaBank.Api.Datos;

public interface ISqlConnectionFactory
{
    SqlConnection CreateConnection();
}
