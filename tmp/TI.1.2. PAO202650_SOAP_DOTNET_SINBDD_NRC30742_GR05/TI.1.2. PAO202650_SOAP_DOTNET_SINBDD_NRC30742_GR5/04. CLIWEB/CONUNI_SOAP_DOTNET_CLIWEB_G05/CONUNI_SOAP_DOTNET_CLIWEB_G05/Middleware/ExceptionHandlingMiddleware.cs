using System.Net;
using System.Text.Json;
using CONUNI_SOAP_DOTNET_CLIWEB_G09.Models;

namespace CONUNI_SOAP_DOTNET_CLIWEB_G09.Middleware
{
    public class ExceptionHandlingMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<ExceptionHandlingMiddleware> _logger;

        public ExceptionHandlingMiddleware(RequestDelegate next, ILogger<ExceptionHandlingMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Excepción no manejada capturada");
                await HandleExceptionAsync(context, ex);
            }
        }

        private static Task HandleExceptionAsync(HttpContext context, Exception exception)
        {
            context.Response.ContentType = "application/json";
            
            // Si es una excepción de SOAP, retornar mensaje amigable
            if (exception is System.ServiceModel.CommunicationException ||
                exception is System.ServiceModel.EndpointNotFoundException ||
                exception.InnerException is System.ServiceModel.CommunicationException ||
                exception.InnerException is System.ServiceModel.EndpointNotFoundException ||
                exception.InnerException is System.Net.Sockets.SocketException ||
                exception.InnerException is System.Net.Http.HttpRequestException)
            {
                context.Response.StatusCode = (int)HttpStatusCode.OK;
                var response = new ConversionResponse
                {
                    Exito = false,
                    Mensaje = "Servidor desconectado. El servicio SOAP no está disponible. Por favor, inicia el servidor SOAP."
                };
                
                var jsonResponse = JsonSerializer.Serialize(response);
                return context.Response.WriteAsync(jsonResponse);
            }

            // Para otras excepciones, retornar error genérico
            context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
            var errorResponse = new ConversionResponse
            {
                Exito = false,
                Mensaje = "Ocurrió un error inesperado. Por favor, intenta nuevamente."
            };
            
            var jsonError = JsonSerializer.Serialize(errorResponse);
            return context.Response.WriteAsync(jsonError);
        }
    }
}


