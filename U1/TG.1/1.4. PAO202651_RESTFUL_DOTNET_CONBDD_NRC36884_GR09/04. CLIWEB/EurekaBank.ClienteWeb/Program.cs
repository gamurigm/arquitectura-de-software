using EurekaBank.Cliente.Configuracion;
using EurekaBank.Cliente.Servicios;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllersWithViews();
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
    options.Cookie.SameSite = SameSiteMode.Lax;
});
builder.Services.AddSingleton(_ => new HttpClient
{
    BaseAddress = EurekaBankApiConfiguration.GetBaseAddress("http://localhost:5081/")
});
builder.Services.AddSingleton<IEurekaBankApiClient, EurekaBankApiClient>();
builder.Services.AddScoped<IEurekaBankService, EurekaBankService>();

var app = builder.Build();
app.UseStaticFiles();
app.UseRouting();
app.UseSession();
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Cuenta}/{action=Login}/{id?}");
app.Run();
