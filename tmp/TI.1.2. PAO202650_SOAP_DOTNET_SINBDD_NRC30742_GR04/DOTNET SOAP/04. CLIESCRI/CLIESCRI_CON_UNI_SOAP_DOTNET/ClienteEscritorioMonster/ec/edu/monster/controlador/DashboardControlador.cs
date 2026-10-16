using System;
using ClienteEscritorioMonster.ProxyEscritorioMonster;

namespace ClienteEscritorioMonster.ec.edu.monster.controlador
{
    public class DashboardControlador
    {

        public double EjecutarConversionMasa(string tipoConversion, double valor)
        {
            using (ServicioMonsterClient clienteWCF = new ServicioMonsterClient())
            {
                switch (tipoConversion)
                {
                    case "Gramos a Kilogramos": return clienteWCF.GramosAKilogramos(valor);
                    case "Kilogramos a Libras": return clienteWCF.KilogramosALibras(valor);
                    case "Libras a Onzas": return clienteWCF.LibrasAOnzas(valor);
                    case "Toneladas a Kilogramos": return clienteWCF.ToneladasAKilogramos(valor);

                    case "Onzas a Gramos": return clienteWCF.OnzasAGramos(valor);
                    default: throw new ArgumentException("Seleccione una operación válida de Masa.");
                }
            }
        }


        public double EjecutarConversionTemperatura(string tipoConversion, double valor)
        {
            using (ServicioMonsterClient clienteWCF = new ServicioMonsterClient())
            {
                switch (tipoConversion)
                {
                    case "Celsius a Fahrenheit": return clienteWCF.CelsiusAFahrenheit(valor);
                    case "Fahrenheit a Celsius": return clienteWCF.FahrenheitACelsius(valor);
                    case "Celsius a Kelvin": return clienteWCF.CelsiusAKelvin(valor);
                    case "Kelvin a Celsius": return clienteWCF.KelvinACelsius(valor);
                    case "Fahrenheit a Kelvin": return clienteWCF.FahrenheitAKelvin(valor);
                    default: throw new ArgumentException("Seleccione una operación válida de Temperatura.");
                }
            }
        }

        public double EjecutarConversionLongitud(string tipoConversion, double valor)
        {
            using (ServicioMonsterClient clienteWCF = new ServicioMonsterClient())
            {
                switch (tipoConversion)
                {
                    case "Metros a Kilometros": return clienteWCF.MetrosAKilometros(valor);

                    case "Kilometros a Millas": return clienteWCF.KilometrosAMillas(valor);
                    case "Metros a Pies": return clienteWCF.MetrosAPies(valor);
                    case "Pies a Pulgadas": return clienteWCF.PiesAPulgadas(valor);
                    case "Centimetros a Metros": return clienteWCF.CentimetrosAMetros(valor);

                    default: throw new ArgumentException("Seleccione una operación válida de Longitud.");
                }
            }
        }
    }
}