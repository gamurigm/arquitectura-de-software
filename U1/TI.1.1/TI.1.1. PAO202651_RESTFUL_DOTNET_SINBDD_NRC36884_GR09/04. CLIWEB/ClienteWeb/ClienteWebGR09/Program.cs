using ClienteWebGR09.Services;

var builder = WebApplication.CreateBuilder(args);
var apiUrl = builder.Configuration["Api:Url"] ?? "http://127.0.0.1:19082/";

builder.Services.AddControllersWithViews();
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(opciones =>
{
    opciones.IdleTimeout = TimeSpan.FromMinutes(10);
    opciones.Cookie.HttpOnly = true;
    opciones.Cookie.IsEssential = true;
    opciones.Cookie.Name = ".ConversorGR09.Sesion";
});
builder.Services.AddHttpClient<ClienteRest>(http =>
{
    http.BaseAddress = new Uri(apiUrl);
    http.Timeout = TimeSpan.FromSeconds(15);
});

var app = builder.Build();
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
}

app.UseStaticFiles();
app.UseRouting();
app.UseSession();
app.UseAuthorization();
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");
app.Run();
