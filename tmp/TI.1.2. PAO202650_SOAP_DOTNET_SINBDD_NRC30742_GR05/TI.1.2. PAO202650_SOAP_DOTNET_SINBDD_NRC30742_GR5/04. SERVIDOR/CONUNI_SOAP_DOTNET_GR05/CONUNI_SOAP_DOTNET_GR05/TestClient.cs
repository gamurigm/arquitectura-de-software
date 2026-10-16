using System;
using System.ServiceModel;
using CONUNI_SOAP_DOTNET_GR09;

namespace CONUNI_SOAP_DOTNET_GR09
{
    /// <summary>
    /// Cliente de prueba para el servicio WSConversion
    /// Replica las pruebas del servicio Java
    /// </summary>
    public class TestClient
    {
        public static void Main(string[] args)
        {
            // Datos de prueba
            double temperaturaCelsius = 25.0;
            double metros = 10.0;
            double kilogramos = 5.0;

            try
            {
                // Crear el cliente del servicio
                WSConversionClient client = new WSConversionClient();

                // Pruebas de temperatura
                double fahrenheit = client.CelsiusAFahrenheit(temperaturaCelsius);
                double kelvin = client.CelsiusAKelvin(temperaturaCelsius);

                // Pruebas de longitud
                double pies = client.MetrosAPies(metros);
                double pulgadas = client.MetrosAPulgadas(metros);

                // Pruebas de peso
                double libras = client.KilogramosALibras(kilogramos);
                double gramos = kilogramos * 1000;
                double onzas = client.GramosAOnzas(gramos);


                // Reporte
                Console.WriteLine("=== PRUEBAS DE CONVERSIÓN ===");
                Console.WriteLine();

                Console.WriteLine("--- TEMPERATURA ---");
                Console.WriteLine($"Celsius: {temperaturaCelsius}°C");
                Console.WriteLine($"Fahrenheit: {fahrenheit:F2}°F");
                Console.WriteLine($"Kelvin: {kelvin:F2}K");
                Console.WriteLine();

                Console.WriteLine("--- LONGITUD ---");
                Console.WriteLine($"Metros: {metros} m");
                Console.WriteLine($"Pies: {pies:F2} ft");
                Console.WriteLine($"Pulgadas: {pulgadas:F2} in");
                Console.WriteLine();

                Console.WriteLine("--- PESO/MASA ---");
                Console.WriteLine($"Kilogramos: {kilogramos} kg");
                Console.WriteLine($"Libras: {libras:F2} lb");
                Console.WriteLine($"Gramos: {gramos} g");
                Console.WriteLine($"Onzas: {onzas:F2} oz");
                Console.WriteLine();

                Console.WriteLine($"Onzas fluidas: {onzasFluidas:F2} fl oz");
                Console.WriteLine();

                Console.WriteLine("=== PRUEBAS COMPLETADAS ===");

                // Cerrar el cliente
                client.Close();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
                Console.WriteLine($"Detalles: {ex.StackTrace}");
            }

            Console.WriteLine("\nPresiona cualquier tecla para salir...");
            Console.ReadKey();
        }
    }
}

