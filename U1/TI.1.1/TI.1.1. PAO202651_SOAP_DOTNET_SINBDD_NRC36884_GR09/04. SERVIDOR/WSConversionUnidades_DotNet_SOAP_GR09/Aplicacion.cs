using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using WSConversionUnidades_DotNet_SOAP_GR09.Controladores;
using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Unidades;
using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Utilidades;
using WSConversionUnidades_DotNet_SOAP_GR09.Seguridad;
using WSConversionUnidades_DotNet_SOAP_GR09.Servicios;

const string rutaServicio = "/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09";

var configurador = WebApplication.CreateBuilder(args);

configurador.Services.AddSingleton<AdministradorTokens>();
configurador.Services.AddSingleton<ServicioAutenticacion>();
configurador.Services.AddSingleton<IConversor<UnidadLongitud>, ConversorLongitud>();
configurador.Services.AddSingleton<IConversor<UnidadMasa>, ConversorMasa>();
configurador.Services.AddSingleton<IConversor<UnidadTemperatura>, ConversorTemperatura>();
configurador.Services.AddSingleton<ServicioConversor>();
configurador.Services.AddTransient<ControladorConversorSoap>();
configurador.Services.AddServiceModelServices().AddServiceModelMetadata();
configurador.Services.AddSingleton<IServiceBehavior, UseRequestHeadersForMetadataAddressBehavior>();

var aplicacion = configurador.Build();

aplicacion.UseServiceModel(serviceBuilder =>
{
    serviceBuilder
        .AddService<ControladorConversorSoap>()
        .AddServiceEndpoint<ControladorConversorSoap, IContratoConversorSoap>(
            new BasicHttpBinding(), rutaServicio);

    serviceBuilder.ConfigureServiceHostBase<ControladorConversorSoap>(serviceHost =>
    {
        foreach (var endpoint in serviceHost.Description.Endpoints)
        {
            if (endpoint.Contract.Name == ContratoSoap.NombreServicio)
            {
                endpoint.Name = ContratoSoap.NombrePuerto;
            }
        }
    });
});

aplicacion.Services.GetRequiredService<ServiceMetadataBehavior>().HttpGetEnabled = true;

aplicacion.Run();
