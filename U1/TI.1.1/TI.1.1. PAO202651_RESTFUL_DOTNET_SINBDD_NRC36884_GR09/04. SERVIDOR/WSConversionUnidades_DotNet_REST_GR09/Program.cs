using Microsoft.AspNetCore.Mvc;
using WSConversionUnidades_DotNet_REST_GR09.Excepciones;
using WSConversionUnidades_DotNet_REST_GR09.Modelos;
using WSConversionUnidades_DotNet_REST_GR09.Modelos.Unidades;
using WSConversionUnidades_DotNet_REST_GR09.Modelos.Utilidades;
using WSConversionUnidades_DotNet_REST_GR09.Seguridad;
using WSConversionUnidades_DotNet_REST_GR09.Servicios;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls(builder.Configuration["Servidor:Url"] ?? "http://127.0.0.1:19082");
builder.Services.AddOpenApi();
builder.Services
    .AddControllers()
    .ConfigureApiBehaviorOptions(opciones =>
    {
        opciones.InvalidModelStateResponseFactory = contexto =>
        {
            var detalle = string.Join("; ", contexto.ModelState.Values
                .SelectMany(estado => estado.Errors)
                .Select(error => string.IsNullOrWhiteSpace(error.ErrorMessage)
                    ? "El formato de la solicitud no es válido."
                    : error.ErrorMessage));

            var respuesta = new ErrorRespuesta(
                false,
                StatusCodes.Status400BadRequest,
                "Datos inválidos",
                detalle,
                contexto.HttpContext.Request.Path,
                DateTimeOffset.UtcNow);
            return new BadRequestObjectResult(respuesta);
        };
    });
builder.Services.AddSingleton<AdministradorTokens>();
builder.Services.AddSingleton<ServicioAutenticacion>();
builder.Services.AddSingleton<ServicioCatalogo>();
builder.Services.AddSingleton<IConversor<UnidadLongitud>, ConversorLongitud>();
builder.Services.AddSingleton<IConversor<UnidadMasa>, ConversorMasa>();
builder.Services.AddSingleton<IConversor<UnidadTemperatura>, ConversorTemperatura>();
builder.Services.AddSingleton<ServicioConversion>();

var app = builder.Build();

app.UseStatusCodePages(async contexto =>
{
    if (contexto.HttpContext.Response.StatusCode != StatusCodes.Status404NotFound)
    {
        return;
    }

    contexto.HttpContext.Response.ContentType = "application/json; charset=utf-8";
    await contexto.HttpContext.Response.WriteAsJsonAsync(new ErrorRespuesta(
        false,
        StatusCodes.Status404NotFound,
        "Recurso no encontrado",
        $"La ruta solicitada no existe: {contexto.HttpContext.Request.Path}",
        contexto.HttpContext.Request.Path,
        DateTimeOffset.UtcNow));
});
app.UseMiddleware<MiddlewareErrores>();
app.UseMiddleware<MiddlewareToken>();
app.MapOpenApi();
app.MapControllers();
app.Run();
