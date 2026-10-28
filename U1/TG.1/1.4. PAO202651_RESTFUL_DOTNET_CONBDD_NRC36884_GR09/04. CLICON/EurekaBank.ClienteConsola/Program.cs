using EurekaBank.Cliente.Configuracion;
using EurekaBank.Cliente.Controladores;
using EurekaBank.Cliente.Servicios;
using EurekaBank.ClienteConsola.Vistas;

using var httpClient = new HttpClient
{
    BaseAddress = EurekaBankApiConfiguration.GetBaseAddress("http://localhost:5081/")
};
var servicio = new EurekaBankService(new EurekaBankApiClient(httpClient));
var controlador = new EurekaBankController(servicio);
await new MenuPrincipal(controlador).EjecutarAsync();
