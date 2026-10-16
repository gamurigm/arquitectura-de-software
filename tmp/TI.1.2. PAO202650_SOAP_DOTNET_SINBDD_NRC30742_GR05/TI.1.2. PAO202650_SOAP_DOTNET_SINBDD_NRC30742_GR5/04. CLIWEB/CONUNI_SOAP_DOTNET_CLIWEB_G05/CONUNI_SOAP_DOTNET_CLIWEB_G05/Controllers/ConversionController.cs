using Microsoft.AspNetCore.Mvc;
using CONUNI_SOAP_DOTNET_CLIWEB_G09.servicio;
using CONUNI_SOAP_DOTNET_CLIWEB_G09.Models;

namespace CONUNI_SOAP_DOTNET_CLIWEB_G09.Controllers
{
    public class ConversionController : Controller
    {
        private const string SESSION_KEY_USUARIO = "Usuario";
        private readonly ConversionSoapService _soapService;
        private readonly IConfiguration _configuration;

        public ConversionController(IConfiguration configuration)
        {
            _configuration = configuration;
            var endpoint = _configuration["SoapService:Endpoint"];
            if (string.IsNullOrEmpty(endpoint))
            {
                throw new Exception("La URL del servicio SOAP no está configurada en appsettings.json");
            }
            _soapService = new ConversionSoapService(endpoint);
        }

        [HttpGet]
        public IActionResult Index()
        {
            // Verificar sesión
            if (HttpContext.Session.GetString(SESSION_KEY_USUARIO) == null)
            {
                return RedirectToAction("Index", "Login");
            }

            ViewBag.Usuario = HttpContext.Session.GetString(SESSION_KEY_USUARIO);
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Convertir([FromBody] ConversionRequest request)
        {
            try
            {
                // Verificar sesión
                if (HttpContext.Session.GetString(SESSION_KEY_USUARIO) == null)
                {
                    return Json(new ConversionResponse
                    {
                        Exito = false,
                        Mensaje = "Sesión expirada. Por favor, inicie sesión nuevamente."
                    });
                }

                var resultado = await _soapService.ConvertirAsync(request);
                
                // Asegurar que siempre retornamos una respuesta válida
                if (resultado == null)
                {
                    return Json(new ConversionResponse
                    {
                        Exito = false,
                        Mensaje = "Servidor desconectado. El servicio SOAP no está disponible. Por favor, inicia el servidor SOAP."
                    });
                }
                
                return Json(resultado);
            }
            catch (Exception ex)
            {
                // Captura cualquier excepción no capturada y retorna un mensaje amigable
                return Json(new ConversionResponse
                {
                    Exito = false,
                    Mensaje = "Servidor desconectado. El servicio SOAP no está disponible. Por favor, inicia el servidor SOAP."
                });
            }
        }
    }
}

