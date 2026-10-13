using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using WSConversionUnidades_DotNet_SOAP_GR09.Controladores;
using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Unidades;
using WSConversionUnidades_DotNet_SOAP_GR09.Modelos.Utilidades;
using WSConversionUnidades_DotNet_SOAP_GR09.Seguridad;
using WSConversionUnidades_DotNet_SOAP_GR09.Servicios;

const string rutaServicio = "/04.SERVIDOR/WSConversionUnidades_DotNet_SOAP_GR09";

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<AdministradorTokens>();
builder.Services.AddSingleton<ServicioAutenticacion>();
builder.Services.AddSingleton<IConversor<UnidadLongitud>, ConversorLongitud>();
builder.Services.AddSingleton<IConversor<UnidadMasa>, ConversorMasa>();
builder.Services.AddSingleton<IConversor<UnidadTemperatura>, ConversorTemperatura>();
builder.Services.AddSingleton<ServicioConversor>();
builder.Services.AddTransient<ConversorSoapController>();
builder.Services.AddServiceModelServices().AddServiceModelMetadata();
builder.Services.AddSingleton<IServiceBehavior, UseRequestHeadersForMetadataAddressBehavior>();

var app = builder.Build();

app.UseServiceModel(serviceBuilder =>
{
    serviceBuilder
        .AddService<ConversorSoapController>()
        .AddServiceEndpoint<ConversorSoapController, IContratoConversorSoap>(
            new BasicHttpBinding(), rutaServicio);

    serviceBuilder.ConfigureServiceHostBase<ConversorSoapController>(serviceHost =>
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

app.Services.GetRequiredService<ServiceMetadataBehavior>().HttpGetEnabled = true;

app.Run();
