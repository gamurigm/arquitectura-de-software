using ClienteWebGR09.Servicios;

using ClienteWebGR09.Controladores;

var configurador = WebApplication.CreateBuilder(args);
var urlApi = Environment.GetEnvironmentVariable("CONVERSOR_API_URL")
    ?? configurador.Configuration["Api:Url"]
    ?? "http://127.0.0.1:19082/";

configurador.Services.AddControllersWithViews(opciones =>
    opciones.Conventions.Add(new ConvencionNombreControlador()))
    .AddRazorOptions(opciones =>
    {
        opciones.ViewLocationFormats.Clear();
        opciones.ViewLocationFormats.Add("/Vistas/{1}/{0}.cshtml");
        opciones.ViewLocationFormats.Add("/Vistas/Compartidas/{0}.cshtml");
        opciones.ViewLocationFormats.Add("/Vistas/{0}.cshtml");
    });
configurador.Services.AddDistributedMemoryCache();
configurador.Services.AddSession(opciones =>
{
    opciones.IdleTimeout = TimeSpan.FromMinutes(10);
    opciones.Cookie.HttpOnly = true;
    opciones.Cookie.IsEssential = true;
    opciones.Cookie.Name = ".ConversorGR09.Sesion";
});
configurador.Services.AddHttpClient<ClienteRest>(clienteHttp =>
{
    clienteHttp.BaseAddress = new Uri(urlApi);
    clienteHttp.Timeout = TimeSpan.FromSeconds(15);
});

var aplicacion = configurador.Build();
if (!aplicacion.Environment.IsDevelopment())
{
    aplicacion.UseExceptionHandler("/Inicio/Error");
}

aplicacion.UseStaticFiles();
aplicacion.UseRouting();
aplicacion.UseSession();
aplicacion.UseAuthorization();
aplicacion.MapControllerRoute(
    name: "default",
    pattern: "{controller=Inicio}/{action=Inicio}/{id?}");
aplicacion.Run();
