using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using ec.edu.monster.Conexion;
using ec.edu.monster.Controladores;
using ec.edu.monster.Repositorios;
using ec.edu.monster.Seguridad;
using ec.edu.monster.Servicios;

if (args.Length > 0 && args[0] == "--hash-password")
{
    Console.Write("Contraseña nueva: ");
    var contrasena = LeerContrasena();
    Console.WriteLine(PasswordHasher.CrearHash(contrasena));
    return;
}

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddSingleton(_ => ConexionDb.Instancia);
builder.Services.AddSingleton<SesionesActivas>();
builder.Services.AddTransient<EurekaBankRepository>();
builder.Services.AddTransient<EurekaBankService>();
builder.Services.AddTransient<ControladorEurekaSoap>();
builder.Services.AddServiceModelServices().AddServiceModelMetadata();
builder.Services.AddSingleton<IServiceBehavior, UseRequestHeadersForMetadataAddressBehavior>();
builder.WebHost.UseUrls(builder.Configuration["Urls"] ?? "http://localhost:5080");

var app = builder.Build();
app.UseServiceModel(serviceBuilder =>
{
    serviceBuilder
        .AddService<ControladorEurekaSoap>()
        .AddServiceEndpoint<ControladorEurekaSoap, IContratoEureka>(new BasicHttpBinding(), "/WSEureka");
    serviceBuilder.ConfigureServiceHostBase<ControladorEurekaSoap>(host =>
    {
        foreach (var endpoint in host.Description.Endpoints)
            if (endpoint.Contract.Name == ContratoEureka.NombreServicio)
                endpoint.Name = ContratoEureka.NombrePuerto;
    });
});
app.Services.GetRequiredService<ServiceMetadataBehavior>().HttpGetEnabled = true;
app.MapGet("/", () => Results.Text("EurekaBank SOAP disponible. Consulte /WSEureka?wsdl."));
app.Run();

static string LeerContrasena()
{
    var caracteres = new List<char>();
    while (true)
    {
        var tecla = Console.ReadKey(intercept: true);
        if (tecla.Key == ConsoleKey.Enter) break;
        if (tecla.Key == ConsoleKey.Backspace && caracteres.Count > 0) caracteres.RemoveAt(caracteres.Count - 1);
        else if (!char.IsControl(tecla.KeyChar)) caracteres.Add(tecla.KeyChar);
    }
    Console.WriteLine();
    var contenido = caracteres.ToArray();
    var valor = new string(contenido);
    Array.Clear(contenido);
    caracteres.Clear();
    return valor;
}
