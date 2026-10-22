using EurekaBank.Cliente.Configuracion;
using EurekaBank.Cliente.Controladores;
using EurekaBank.Cliente.Servicios;

namespace EurekaBank.ClienteMovil;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder.UseMauiApp<App>();
        builder.Services.AddSingleton(new HttpClient
        {
            BaseAddress = EurekaBankApiConfiguration.GetBaseAddress("http://10.0.2.2:5081/")
        });
        builder.Services.AddSingleton<IEurekaBankApiClient, EurekaBankApiClient>();
        builder.Services.AddSingleton<IEurekaBankService, EurekaBankService>();
        builder.Services.AddSingleton<EurekaBankController>();
        builder.Services.AddSingleton<MainPage>();
        return builder.Build();
    }
}
