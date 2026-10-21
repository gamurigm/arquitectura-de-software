using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;

namespace SistemaConversorUI.Maui
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            var builder = MauiApp.CreateBuilder();
            builder
                .UseMauiApp<App>()
                .ConfigureFonts(fonts =>
                {
                    fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                });

            builder.Services.AddMauiBlazorWebView();
            // 1. Cliente HTTP
            builder.Services.AddHttpClient();

            // 2. SERVICIOS CORE
            builder.Services.AddScoped<ec.edu.monster.Servicios.EstadoAplicacion>();
            builder.Services.AddScoped<ec.edu.monster.Servicios.FabricaEstrategiaBackend>();

            // 3. ADAPTADORES (Igual que en la Web)
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionJavaSoap>();
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionDotNetSoap>();
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionDotNetRest>();
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionJavaRest>();

            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionJavaSoap>();
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionDotNetSoap>();
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionDotNetRest>();
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionJavaRest>();

#if DEBUG
            builder.Services.AddBlazorWebViewDeveloperTools();
    		builder.Logging.AddDebug();
#endif

            // --- AUTENTICACIÓN ---
            // EL REAL (Java SOAP):
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion, ec.edu.monster.Adaptadores.AdaptadorAutenticacionJavaSoap>();

            
            // LOS MOCKS (Para los que aún no construimos):
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion>(sp => new ec.edu.monster.Adaptadores.AdaptadorAutenticacionMock(ec.edu.monster.Modelos.TipoBackend.JavaRest));
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion>(sp => new ec.edu.monster.Adaptadores.AdaptadorAutenticacionMock(ec.edu.monster.Modelos.TipoBackend.DotNetSoap));
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorAutenticacion>(sp => new ec.edu.monster.Adaptadores.AdaptadorAutenticacionMock(ec.edu.monster.Modelos.TipoBackend.DotNetRest));

            // --- CONVERSIÓN ---
            // EL REAL (Java SOAP):
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion, ec.edu.monster.Adaptadores.AdaptadorConversionJavaSoap>();
            // LOS MOCKS:
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion>(sp => new ec.edu.monster.Adaptadores.AdaptadorConversionMock(ec.edu.monster.Modelos.TipoBackend.JavaRest));
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion>(sp => new ec.edu.monster.Adaptadores.AdaptadorConversionMock(ec.edu.monster.Modelos.TipoBackend.DotNetSoap));
            builder.Services.AddScoped<ec.edu.monster.Interfaces.IAdaptadorConversion>(sp => new ec.edu.monster.Adaptadores.AdaptadorConversionMock(ec.edu.monster.Modelos.TipoBackend.DotNetRest));

            return builder.Build();
        }
    }
}
