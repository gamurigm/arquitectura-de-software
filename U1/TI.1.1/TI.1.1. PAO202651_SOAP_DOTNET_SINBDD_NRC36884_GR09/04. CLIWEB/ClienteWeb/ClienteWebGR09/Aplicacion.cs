using ClienteWebGR09.Controladores;

var configurador = WebApplication.CreateBuilder(args);

// Registra los servicios de la aplicación.
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
    opciones.IdleTimeout = TimeSpan.FromMinutes(15);
    opciones.Cookie.HttpOnly = true;
    opciones.Cookie.IsEssential = true;
});
configurador.Services.AddSingleton<ClienteWebGR09.Servicios.ClienteSOAP>();

var aplicacion = configurador.Build();

// Configura el flujo de solicitudes HTTP.
if (!aplicacion.Environment.IsDevelopment())
{
    aplicacion.UseExceptionHandler("/Inicio/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    aplicacion.UseHsts();
}

aplicacion.UseStaticFiles();
aplicacion.UseRouting();
aplicacion.UseSession();
aplicacion.UseAuthorization();

aplicacion.MapControllerRoute(
    name: "default",
    pattern: "{controller=Inicio}/{action=Inicio}/{id?}");

aplicacion.Run();
