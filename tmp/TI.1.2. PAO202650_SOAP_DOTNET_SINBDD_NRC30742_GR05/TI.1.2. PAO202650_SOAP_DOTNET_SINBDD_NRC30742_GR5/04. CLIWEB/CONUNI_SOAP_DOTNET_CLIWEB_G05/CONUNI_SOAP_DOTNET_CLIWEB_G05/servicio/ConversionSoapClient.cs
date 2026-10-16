using System;
using System.ServiceModel;
using CONUNI_SOAP_DOTNET_CLIWEB_G09.Models;

namespace CONUNI_SOAP_DOTNET_CLIWEB_G09.servicio
{
    [ServiceContract(Name = "WSConversion", Namespace = "http://tempuri.org/")]
    public interface IWSConversionClient
    {
        [OperationContract(Name = "login")]
        bool Login(string usuario, string contrasena);

        [OperationContract(Name = "celsiusAFahrenheit")]
        double CelsiusAFahrenheit(double celsius);

        [OperationContract(Name = "fahrenheitACelsius")]
        double FahrenheitACelsius(double fahrenheit);

        [OperationContract(Name = "celsiusAKelvin")]
        double CelsiusAKelvin(double celsius);

        [OperationContract(Name = "kelvinACelsius")]
        double KelvinACelsius(double kelvin);

        [OperationContract(Name = "fahrenheitAKelvin")]
        double FahrenheitAKelvin(double fahrenheit);

        [OperationContract(Name = "kelvinAFahrenheit")]
        double KelvinAFahrenheit(double kelvin);

        [OperationContract(Name = "metrosAPies")]
        double MetrosAPies(double metros);

        [OperationContract(Name = "piesAMetros")]
        double PiesAMetros(double pies);

        [OperationContract(Name = "metrosAPulgadas")]
        double MetrosAPulgadas(double metros);

        [OperationContract(Name = "pulgadasAMetros")]
        double PulgadasAMetros(double pulgadas);

        [OperationContract(Name = "kilometrosAMillas")]
        double KilometrosAMillas(double kilometros);

        [OperationContract(Name = "millasAKilometros")]
        double MillasAKilometros(double millas);

        [OperationContract(Name = "kilogramosALibras")]
        double KilogramosALibras(double kilogramos);

        [OperationContract(Name = "librasAKilogramos")]
        double LibrasAKilogramos(double libras);

        [OperationContract(Name = "gramosAOnzas")]
        double GramosAOnzas(double gramos);

        [OperationContract(Name = "onzasAGramos")]
        double OnzasAGramos(double onzas);
    }

    public class ConversionSoapClient : ClientBase<IWSConversionClient>, IWSConversionClient
    {
        public ConversionSoapClient(string endpoint)
            : base(CreateBinding(), new EndpointAddress(endpoint))
        {
            if (string.IsNullOrWhiteSpace(endpoint))
            {
                throw new ArgumentException("El endpoint no puede ser nulo o vacío", nameof(endpoint));
            }
        }

        private static BasicHttpBinding CreateBinding()
        {
            return new BasicHttpBinding
            {
                MaxBufferSize = int.MaxValue,
                MaxReceivedMessageSize = int.MaxValue,
                OpenTimeout = TimeSpan.FromSeconds(30),
                CloseTimeout = TimeSpan.FromSeconds(30),
                SendTimeout = TimeSpan.FromSeconds(30),
                ReceiveTimeout = TimeSpan.FromSeconds(30)
            };
        }

        public bool Login(string usuario, string contrasena) => Channel.Login(usuario, contrasena);
        public double CelsiusAFahrenheit(double celsius) => Channel.CelsiusAFahrenheit(celsius);
        public double FahrenheitACelsius(double fahrenheit) => Channel.FahrenheitACelsius(fahrenheit);
        public double CelsiusAKelvin(double celsius) => Channel.CelsiusAKelvin(celsius);
        public double KelvinACelsius(double kelvin) => Channel.KelvinACelsius(kelvin);
        public double FahrenheitAKelvin(double fahrenheit) => Channel.FahrenheitAKelvin(fahrenheit);
        public double KelvinAFahrenheit(double kelvin) => Channel.KelvinAFahrenheit(kelvin);
        public double MetrosAPies(double metros) => Channel.MetrosAPies(metros);
        public double PiesAMetros(double pies) => Channel.PiesAMetros(pies);
        public double MetrosAPulgadas(double metros) => Channel.MetrosAPulgadas(metros);
        public double PulgadasAMetros(double pulgadas) => Channel.PulgadasAMetros(pulgadas);
        public double KilometrosAMillas(double kilometros) => Channel.KilometrosAMillas(kilometros);
        public double MillasAKilometros(double millas) => Channel.MillasAKilometros(millas);
        public double KilogramosALibras(double kilogramos) => Channel.KilogramosALibras(kilogramos);
        public double LibrasAKilogramos(double libras) => Channel.LibrasAKilogramos(libras);
        public double GramosAOnzas(double gramos) => Channel.GramosAOnzas(gramos);
        public double OnzasAGramos(double onzas) => Channel.OnzasAGramos(onzas);
    }

    public class ConversionSoapService
    {
        private readonly string _endpoint;

        public ConversionSoapService(string endpoint)
        {
            _endpoint = endpoint ?? throw new ArgumentNullException(nameof(endpoint));
        }

        public async System.Threading.Tasks.Task<ConversionResponse> ConvertirAsync(ConversionRequest request)
        {
            try
            {
                return await System.Threading.Tasks.Task.Run(() =>
                {
                    using var client = new ConversionSoapClient(_endpoint);
                    double resultado = request.Categoria.ToLowerInvariant() switch
                    {
                        "temperatura" => ConvertirTemperatura(client, request),
                        "longitud" => ConvertirLongitud(client, request),
                        "peso" or "peso/masa" => ConvertirPeso(client, request),
                        _ => throw new NotSupportedException("Categoría no válida")
                    };

                    return new ConversionResponse
                    {
                        Exito = true,
                        ValorOriginal = request.Valor,
                        ValorConvertido = resultado,
                        UnidadOrigen = request.UnidadOrigen,
                        UnidadDestino = request.UnidadDestino,
                        Categoria = request.Categoria,
                        Mensaje = "Conversión exitosa",
                        Timestamp = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()
                    };
                });
            }
            catch (NotSupportedException ex)
            {
                return new ConversionResponse { Exito = false, Mensaje = ex.Message };
            }
            catch (EndpointNotFoundException)
            {
                return new ConversionResponse { Exito = false, Mensaje = "Servidor desconectado. Inicia el servidor SOAP en localhost." };
            }
            catch (CommunicationException)
            {
                return new ConversionResponse { Exito = false, Mensaje = "Servidor desconectado. No se pudo establecer conexión con el servicio SOAP." };
            }
            catch (Exception ex)
            {
                return new ConversionResponse { Exito = false, Mensaje = $"Error: {ex.Message}" };
            }
        }

        private double ConvertirTemperatura(ConversionSoapClient client, ConversionRequest request)
        {
            string from = request.UnidadOrigen.ToLowerInvariant();
            string to = request.UnidadDestino.ToLowerInvariant();

            return (from, to) switch
            {
                ("celsius", "fahrenheit") => client.CelsiusAFahrenheit(request.Valor),
                ("fahrenheit", "celsius") => client.FahrenheitACelsius(request.Valor),
                ("celsius", "kelvin") => client.CelsiusAKelvin(request.Valor),
                ("kelvin", "celsius") => client.KelvinACelsius(request.Valor),
                ("fahrenheit", "kelvin") => client.FahrenheitAKelvin(request.Valor),
                ("kelvin", "fahrenheit") => client.KelvinAFahrenheit(request.Valor),
                _ => throw new NotSupportedException($"Conversión de {from} a {to} no soportada")
            };
        }

        private double ConvertirLongitud(ConversionSoapClient client, ConversionRequest request)
        {
            string from = request.UnidadOrigen.ToLowerInvariant();
            string to = request.UnidadDestino.ToLowerInvariant();

            return (from, to) switch
            {
                ("metros", "pies") => client.MetrosAPies(request.Valor),
                ("pies", "metros") => client.PiesAMetros(request.Valor),
                ("metros", "pulgadas") => client.MetrosAPulgadas(request.Valor),
                ("pulgadas", "metros") => client.PulgadasAMetros(request.Valor),
                ("kilometros", "millas") => client.KilometrosAMillas(request.Valor),
                ("millas", "kilometros") => client.MillasAKilometros(request.Valor),
                _ => throw new NotSupportedException($"Conversión de {from} a {to} no soportada")
            };
        }

        private double ConvertirPeso(ConversionSoapClient client, ConversionRequest request)
        {
            string from = request.UnidadOrigen.ToLowerInvariant();
            string to = request.UnidadDestino.ToLowerInvariant();

            return (from, to) switch
            {
                ("kilogramos", "libras") => client.KilogramosALibras(request.Valor),
                ("libras", "kilogramos") => client.LibrasAKilogramos(request.Valor),
                ("gramos", "onzas") => client.GramosAOnzas(request.Valor),
                ("onzas", "gramos") => client.OnzasAGramos(request.Valor),
                _ => throw new NotSupportedException($"Conversión de {from} a {to} no soportada")
            };
        }
    }
}
