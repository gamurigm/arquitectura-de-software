using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace CONUNI_SOAP_DOTNET_GR09
{
    /// <summary>
    /// Interfaz del servicio de conversiones WSConversion
    /// Replicado desde Java - Adaptado para .NET WCF
    /// </summary>
    [ServiceContract(Name = "WSConversion")]
    public interface IWSConversion
    {
        // Autenticación (validación ocurre SOLO en el servidor)
        [OperationContract(Name = "login")]
        bool Login(string usuario, string contrasena);

        // Conversiones de Temperatura
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

        // Conversiones de Longitud
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

        // Conversiones de Peso/Masa
        [OperationContract(Name = "kilogramosALibras")]
        double KilogramosALibras(double kilogramos);

        [OperationContract(Name = "librasAKilogramos")]
        double LibrasAKilogramos(double libras);

        [OperationContract(Name = "gramosAOnzas")]
        double GramosAOnzas(double gramos);

        [OperationContract(Name = "onzasAGramos")]
        double OnzasAGramos(double onzas);
    }
}
