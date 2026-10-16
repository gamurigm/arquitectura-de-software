using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using CONUNI_SOAP_DOTNET_CLIWEB_G09.servicio;
using System.Threading.Tasks;

namespace CONUNI_SOAP_DOTNET_CLIWEB_G09.Controllers
{
    /// <summary>
    /// Controlador de login de la aplicación web cliente SOAP.
    /// <para>
    /// Las credenciales NO se almacenan en el cliente. La validación
    /// se delega al servidor SOAP a través de la operación Login.
    /// La URL del servidor se llama vía el cliente WCF configurado.
    /// </para>
    /// </summary>
    public class LoginController : Controller
    {
        private const string SESSION_KEY_USUARIO = "Usuario";
        private readonly string _soapEndpoint;

        public LoginController(IConfiguration configuration)
        {
            _soapEndpoint = configuration["SoapService:Endpoint"] ?? "http://localhost:62533/Service1.svc";
        }

        [HttpGet]
        public IActionResult Index()
        {
            // Si ya está logueado, redirigir al panel principal
            if (HttpContext.Session.GetString(SESSION_KEY_USUARIO) != null)
            {
                return RedirectToAction("Index", "Conversion");
            }
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Index(string username, string password)
        {
            bool autenticado = ValidarCredencialesEnServidor(username, password);

            if (autenticado)
            {
                HttpContext.Session.SetString(SESSION_KEY_USUARIO, username?.ToUpper() ?? "");
                return RedirectToAction("Index", "Conversion");
            }

            ViewBag.Error = "Credenciales incorrectas";
            return View();
        }

        [HttpPost]
        public IActionResult Logout()
        {
            HttpContext.Session.Clear();
            return RedirectToAction("Index");
        }

        /// <summary>
        /// Llama al servidor SOAP vía HTTP POST para validar las credenciales.
        /// El cliente NUNCA almacena ni conoce las credenciales correctas.
        /// </summary>
        private bool ValidarCredencialesEnServidor(string usuario, string contrasena)
        {
            try
            {
                using var client = new ConversionSoapClient(_soapEndpoint);
                return client.Login(usuario, contrasena);
            }
            catch
            {
                return false;
            }
        }
    }
}
