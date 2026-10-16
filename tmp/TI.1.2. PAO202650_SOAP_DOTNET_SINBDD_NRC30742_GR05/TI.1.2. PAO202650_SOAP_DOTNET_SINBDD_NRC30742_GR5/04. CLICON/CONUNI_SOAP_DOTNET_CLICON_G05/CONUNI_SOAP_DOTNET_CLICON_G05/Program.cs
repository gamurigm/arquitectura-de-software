using System;
using ec.edu.monster.controlador;

namespace CONUNI_SOAP_DOTNET_CLICON_G09
{
    /// <summary>
    /// Programa principal del cliente SOAP de conversiones
    /// Replicado desde Java con arquitectura MVC completa
    /// </summary>
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("=== CLIENTE SOAP MONSTERS INC. CONVERTER ===");
            Console.WriteLine("Sistema de Conversiones con Arquitectura MVC");
            Console.WriteLine("Desarrollado por: Grupo 09");
            Console.WriteLine();

            try
            {
                // Crear e iniciar el controlador principal
                var controlador = new ControladorConsola();
                controlador.Iniciar();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error fatal en la aplicación: {ex.Message}");
                Console.WriteLine($"Detalles: {ex.StackTrace}");
            }

            Console.WriteLine();
            Console.WriteLine("Presiona cualquier tecla para salir...");
            Console.ReadKey();
        }
    }
}
