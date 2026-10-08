using SistemaConversorUI.Web.Client.Pages;
using ec.edu.monster.Components;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAntiforgery();

// 1. Servicios base de Blazor y Seguridad
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();
builder.Services.AddAntiforgery();
builder.Services.AddHttpClient();

// 2. SERVICIOS CORE 
builder.Services.AddScoped<ec.edu.monster.Servicios.EstadoAplicacion>();
builder.Services.AddScoped<ec.edu.monster.Servicios.FabricaEstrategiaBackend>();

// 3. ADAPTADORES DE AUTENTICACIÓN
// El Real (Java SOAP):
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionJavaSoap>();
// El Real (.NET SOAP):
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionDotNetSoap>();
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionDotNetRest>();

// Los Mocks (Para los demás):
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionJavaRest>();


// 4. ADAPTADORES DE CONVERSIÓN
// El Real (Java SOAP):
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionJavaSoap>();
// El Real (.NET SOAP):
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionDotNetSoap>();

builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionDotNetRest>();
// Los Mocks:
builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionJavaRest>();

// --- FIN DE LOS SERVICIOS ---
var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseWebAssemblyDebugging();
}
else
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}
app.UseStatusCodePagesWithReExecute("/not-found", createScopeForStatusCodePages: true);
app.UseHttpsRedirection();

app.UseAntiforgery();

app.MapStaticAssets();
app.MapRazorComponents<App>()
    .AddInteractiveServerRenderMode()
    .AddAdditionalAssemblies(typeof(SistemaConversorUI.Web.Client._Imports).Assembly);

app.Run();
