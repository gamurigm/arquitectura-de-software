using System.Net.Http;
using System.Windows;
using EurekaBank.Cliente.Configuracion;
using EurekaBank.Cliente.Controladores;
using EurekaBank.Cliente.Servicios;

namespace EurekaBank.ClienteEscritorio;

public partial class App : Application
{
    private HttpClient? _httpClient;

    protected override void OnStartup(StartupEventArgs e)
    {
        base.OnStartup(e);
        _httpClient = new HttpClient
        {
            BaseAddress = EurekaBankApiConfiguration.GetBaseAddress("http://localhost:5081/")
        };
        var servicio = new EurekaBankService(new EurekaBankApiClient(_httpClient));
        MainWindow = new MainWindow(new EurekaBankController(servicio));
        MainWindow.Show();
    }

    protected override void OnExit(ExitEventArgs e)
    {
        _httpClient?.Dispose();
        base.OnExit(e);
    }
}
