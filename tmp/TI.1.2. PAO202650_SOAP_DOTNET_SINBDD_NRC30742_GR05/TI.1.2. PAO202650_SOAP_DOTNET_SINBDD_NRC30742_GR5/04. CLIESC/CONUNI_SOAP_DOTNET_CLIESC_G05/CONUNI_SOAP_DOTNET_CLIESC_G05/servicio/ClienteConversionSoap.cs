using System;
using System.ServiceModel;
using System.ServiceModel.Channels;
using CONUNI_SOAP_DOTNET_CLIESC_G09.modelo;

namespace CONUNI_SOAP_DOTNET_CLIESC_G09.servicio
{
    /// <summary>
    /// Cliente para consumir el servicio SOAP WCF de conversión
    /// </summary>
    public class ClienteConversionSoap
    {
        private const string URL_SERVICIO = "http://10.40.25.94:8085/Service1.svc";
        private const string NAMESPACE = "http://tempuri.org/";
        private const string INTERFAZ_NOMBRE = "WSConversion";

        private ChannelFactory<IWSConversion> _factory;
        private IWSConversion _proxy;

        /// <summary>
        /// Obtiene un mensaje de error amigable basado en el tipo de excepción
        /// </summary>
        private string ObtenerMensajeErrorAmigable(Exception ex)
        {
            // Detectar errores de conexión
            if (ex is EndpointNotFoundException || 
                ex is CommunicationException ||
                ex.Message.Contains("No había ningún extremo") ||
                ex.Message.Contains("listening") ||
                ex.Message.Contains("no endpoint") ||
                ex.Message.Contains("could not be reached") ||
                ex.Message.Contains("no se pudo conectar"))
            {
                return "El servicio no está respondiendo. Por favor, espera un momento y vuelve a intentar.";
            }

            // Detectar errores de timeout
            if (ex is TimeoutException || ex.Message.Contains("timeout") || ex.Message.Contains("tiempo de espera"))
            {
                return "El servicio tardó demasiado en responder. Por favor, intenta de nuevo.";
            }

            // Otros errores - devolver mensaje genérico más amigable
            return "Ocurrió un error al procesar la solicitud. Por favor, verifica que el servicio esté disponible.";
        }

        public ClienteConversionSoap(string urlServicio = URL_SERVICIO)
        {
            try
            {
                // Crear el binding básico HTTP
                BasicHttpBinding binding = new BasicHttpBinding();
                binding.MaxReceivedMessageSize = 2147483647;
                binding.ReaderQuotas.MaxStringContentLength = 2147483647;

                // Crear el endpoint
                EndpointAddress endpoint = new EndpointAddress(urlServicio);

                // Crear el ChannelFactory
                _factory = new ChannelFactory<IWSConversion>(binding, endpoint);
                _proxy = _factory.CreateChannel();
            }
            catch (Exception ex)
            {
                throw new Exception($"Error al conectar con el servicio SOAP: {ex.Message}", ex);
            }
        }

        public bool Login(string usuario, string contrasena)
        {
            return _proxy.Login(usuario, contrasena);
        }

        // ========== TEMPERATURA ==========

        public ConversionResponse CelsiusAFahrenheit(double celsius)
        {
            try
            {
                double resultado = _proxy.CelsiusAFahrenheit(celsius);
                return new ConversionResponse
                {
                    ValorOriginal = celsius,
                    ValorConvertido = resultado,
                    UnidadOrigen = "celsius",
                    UnidadDestino = "fahrenheit",
                    Categoria = "temperatura",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse FahrenheitACelsius(double fahrenheit)
        {
            try
            {
                double resultado = _proxy.FahrenheitACelsius(fahrenheit);
                return new ConversionResponse
                {
                    ValorOriginal = fahrenheit,
                    ValorConvertido = resultado,
                    UnidadOrigen = "fahrenheit",
                    UnidadDestino = "celsius",
                    Categoria = "temperatura",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse CelsiusAKelvin(double celsius)
        {
            try
            {
                double resultado = _proxy.CelsiusAKelvin(celsius);
                return new ConversionResponse
                {
                    ValorOriginal = celsius,
                    ValorConvertido = resultado,
                    UnidadOrigen = "celsius",
                    UnidadDestino = "kelvin",
                    Categoria = "temperatura",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse KelvinACelsius(double kelvin)
        {
            try
            {
                double resultado = _proxy.KelvinACelsius(kelvin);
                return new ConversionResponse
                {
                    ValorOriginal = kelvin,
                    ValorConvertido = resultado,
                    UnidadOrigen = "kelvin",
                    UnidadDestino = "celsius",
                    Categoria = "temperatura",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse FahrenheitAKelvin(double fahrenheit)
        {
            try
            {
                double resultado = _proxy.FahrenheitAKelvin(fahrenheit);
                return new ConversionResponse
                {
                    ValorOriginal = fahrenheit,
                    ValorConvertido = resultado,
                    UnidadOrigen = "fahrenheit",
                    UnidadDestino = "kelvin",
                    Categoria = "temperatura",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse KelvinAFahrenheit(double kelvin)
        {
            try
            {
                double resultado = _proxy.KelvinAFahrenheit(kelvin);
                return new ConversionResponse
                {
                    ValorOriginal = kelvin,
                    ValorConvertido = resultado,
                    UnidadOrigen = "kelvin",
                    UnidadDestino = "fahrenheit",
                    Categoria = "temperatura",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        // ========== LONGITUD ==========

        public ConversionResponse MetrosAPies(double metros)
        {
            try
            {
                double resultado = _proxy.MetrosAPies(metros);
                return new ConversionResponse
                {
                    ValorOriginal = metros,
                    ValorConvertido = resultado,
                    UnidadOrigen = "metros",
                    UnidadDestino = "pies",
                    Categoria = "longitud",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse PiesAMetros(double pies)
        {
            try
            {
                double resultado = _proxy.PiesAMetros(pies);
                return new ConversionResponse
                {
                    ValorOriginal = pies,
                    ValorConvertido = resultado,
                    UnidadOrigen = "pies",
                    UnidadDestino = "metros",
                    Categoria = "longitud",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse MetrosAPulgadas(double metros)
        {
            try
            {
                double resultado = _proxy.MetrosAPulgadas(metros);
                return new ConversionResponse
                {
                    ValorOriginal = metros,
                    ValorConvertido = resultado,
                    UnidadOrigen = "metros",
                    UnidadDestino = "pulgadas",
                    Categoria = "longitud",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse PulgadasAMetros(double pulgadas)
        {
            try
            {
                double resultado = _proxy.PulgadasAMetros(pulgadas);
                return new ConversionResponse
                {
                    ValorOriginal = pulgadas,
                    ValorConvertido = resultado,
                    UnidadOrigen = "pulgadas",
                    UnidadDestino = "metros",
                    Categoria = "longitud",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse KilometrosAMillas(double kilometros)
        {
            try
            {
                double resultado = _proxy.KilometrosAMillas(kilometros);
                return new ConversionResponse
                {
                    ValorOriginal = kilometros,
                    ValorConvertido = resultado,
                    UnidadOrigen = "kilometros",
                    UnidadDestino = "millas",
                    Categoria = "longitud",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse MillasAKilometros(double millas)
        {
            try
            {
                double resultado = _proxy.MillasAKilometros(millas);
                return new ConversionResponse
                {
                    ValorOriginal = millas,
                    ValorConvertido = resultado,
                    UnidadOrigen = "millas",
                    UnidadDestino = "kilometros",
                    Categoria = "longitud",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        // ========== PESO ==========

        public ConversionResponse KilogramosALibras(double kilogramos)
        {
            try
            {
                double resultado = _proxy.KilogramosALibras(kilogramos);
                return new ConversionResponse
                {
                    ValorOriginal = kilogramos,
                    ValorConvertido = resultado,
                    UnidadOrigen = "kilogramos",
                    UnidadDestino = "libras",
                    Categoria = "peso",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse LibrasAKilogramos(double libras)
        {
            try
            {
                double resultado = _proxy.LibrasAKilogramos(libras);
                return new ConversionResponse
                {
                    ValorOriginal = libras,
                    ValorConvertido = resultado,
                    UnidadOrigen = "libras",
                    UnidadDestino = "kilogramos",
                    Categoria = "peso",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse GramosAOnzas(double gramos)
        {
            try
            {
                double resultado = _proxy.GramosAOnzas(gramos);
                return new ConversionResponse
                {
                    ValorOriginal = gramos,
                    ValorConvertido = resultado,
                    UnidadOrigen = "gramos",
                    UnidadDestino = "onzas",
                    Categoria = "peso",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public ConversionResponse OnzasAGramos(double onzas)
        {
            try
            {
                double resultado = _proxy.OnzasAGramos(onzas);
                return new ConversionResponse
                {
                    ValorOriginal = onzas,
                    ValorConvertido = resultado,
                    UnidadOrigen = "onzas",
                    UnidadDestino = "gramos",
                    Categoria = "peso",
                    Exito = true,
                    Mensaje = "Conversión exitosa"
                };
            }
            catch (Exception ex)
            {
                return new ConversionResponse
                {
                    Exito = false,
                    Mensaje = ObtenerMensajeErrorAmigable(ex)
                };
            }
        }

        public void Dispose()
        {
            try
            {
                if (_factory != null)
                {
                    if (_proxy != null && _proxy is ICommunicationObject)
                    {
                        try
                        {
                            ((ICommunicationObject)_proxy).Close();
                        }
                        catch
                        {
                            ((ICommunicationObject)_proxy).Abort();
                        }
                    }
                    _factory.Close();
                }
            }
            catch { }
        }
    }
}
