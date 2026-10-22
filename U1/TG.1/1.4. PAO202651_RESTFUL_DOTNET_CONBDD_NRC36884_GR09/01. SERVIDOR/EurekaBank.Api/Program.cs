using EurekaBank.Api.Datos;
using EurekaBank.Api.Seguridad;
using EurekaBank.Api.Servicios;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddSingleton<ISqlConnectionFactory>(_ =>
    new SqlServerConnectionFactory(builder.Configuration["EUREKABANK_DB_CONNECTION_STRING"]));
builder.Services.AddSingleton<ISecretHasher, Pbkdf2SecretHasher>();
builder.Services.AddSingleton<SesionesActivas>();
builder.Services.AddScoped<IEurekaBankRepository, SqlEurekaBankRepository>();
builder.Services.AddScoped<IEurekaBankService, EurekaBankService>();

var app = builder.Build();
app.MapControllers();
app.Run();

public partial class Program { }
