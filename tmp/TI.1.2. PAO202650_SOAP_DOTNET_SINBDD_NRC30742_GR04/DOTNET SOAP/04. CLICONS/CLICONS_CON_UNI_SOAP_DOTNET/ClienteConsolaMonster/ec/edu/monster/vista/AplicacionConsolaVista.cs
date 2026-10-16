// Importamos el proxy que acabas de crear
using ProxyConsolaMonster;
using System;

namespace ClienteConsolaMonster.ec.edu.monster.vista
{
    class AplicacionConsolaVista
    {
        static void Main(string[] args)
        {
            Console.Title = "Sistema ec.edu.monster - Cliente de Consola SOAP";
            Console.WriteLine("==================================================");
            Console.WriteLine("    SISTEMA DE CONVERSIONES - ACCESO SOAP");
            Console.WriteLine("==================================================");

            // 1. Interfaz de texto para Login
            Console.Write("Ingrese su nombre de usuario: ");
            string nombreUsuarioIngresado = Console.ReadLine();

            Console.Write("Ingrese su clave de acceso: ");
            string claveAccesoIngresada = Console.ReadLine();

            Console.WriteLine("\nConectando con el servidor IIS...");

            // 2. Instanciamos el cliente WCF a través del proxy
            try
            {
                using (ServicioMonsterClient clienteConsolaWCF = new ServicioMonsterClient())
                {
                    // 3. Invocamos el método ValidarAcceso del servidor
                    bool esAccesoValido = clienteConsolaWCF.ValidarAcceso(nombreUsuarioIngresado, claveAccesoIngresada);

                    if (esAccesoValido)
                    {
                        Console.ForegroundColor = ConsoleColor.Green;
                        Console.WriteLine("\n[+] ACCESO CONCEDIDO. Bienvenido a la red institucional.");
                        Console.ResetColor();
                        Console.WriteLine("Presione Enter para continuar al menú principal...");
                        Console.ReadLine();

                        // --- INICIO DEL MENÚ INTERACTIVO ---
                        bool mantenerSesionActivaSistema = true;

                        while (mantenerSesionActivaSistema)
                        {
                            Console.Clear();
                            Console.WriteLine("==================================================");
                            Console.WriteLine("       SISTEMA CENTRAL DE CONVERSIONES WCF        ");
                            Console.WriteLine("==================================================");
                            Console.WriteLine("1. Operaciones de Masa");
                            Console.WriteLine("2. Operaciones de Temperatura");
                            Console.WriteLine("3. Operaciones de Longitud");
                            Console.WriteLine("4. Cerrar Sesión");
                            Console.WriteLine("==================================================");
                            Console.Write("Seleccione una categoría [1-4]: ");

                            string opcionMenuPrincipalSeleccionada = Console.ReadLine() ?? "";

                            switch (opcionMenuPrincipalSeleccionada)
                            {
                                case "1": // MASA
                                    Console.WriteLine("\n--- CONVERSIONES DE MASA ---");
                                    Console.WriteLine("1. Gramos a Kilogramos");
                                    Console.WriteLine("2. Kilogramos a Libras");
                                    Console.WriteLine("3. Libras a Onzas");
                                    Console.WriteLine("4. Toneladas a Kilogramos");
                                    Console.WriteLine("5. Onzas a Gramos");
                                    Console.Write("Seleccione la operación específica [1-5]: ");
                                    string opcionSubMenuMasa = Console.ReadLine() ?? "";

                                    Console.Write("Ingrese el valor numérico a convertir: ");
                                    // Convertimos el texto a número decimal (double)
                                    double valorIngresadoPorUsuario = Convert.ToDouble(Console.ReadLine());
                                    double resultadoCalculadoServidor = 0;

                                    // Evaluamos la opción elegida
                                    if (opcionSubMenuMasa == "1")
                                    {
                                        // Usamos .Result porque el proxy asíncrono lo requiere
                                        resultadoCalculadoServidor = clienteConsolaWCF.GramosAKilogramosAsync(valorIngresadoPorUsuario).Result;
                                        Console.WriteLine($"\n[RESULTADO DESDE IIS]: {valorIngresadoPorUsuario} g = {resultadoCalculadoServidor} kg");
                                    }
                                    else if (opcionSubMenuMasa == "2")
                                    {
                                        resultadoCalculadoServidor = clienteConsolaWCF.KilogramosALibrasAsync(valorIngresadoPorUsuario).Result;
                                        Console.WriteLine($"\n[RESULTADO DESDE IIS]: {valorIngresadoPorUsuario} kg = {resultadoCalculadoServidor} lb");
                                    }
                                    // AQUÍ AGREGAS LOS ELSE IF FALTANTES DE MASA...
                                    break;

                                case "2": // TEMPERATURA
                                    Console.WriteLine("\n--- CONVERSIONES DE TEMPERATURA ---");
                                    // AQUÍ PONES TU MENÚ DE TEMPERATURA...
                                    break;

                                case "3": // LONGITUD
                                    Console.WriteLine("\n--- CONVERSIONES DE LONGITUD ---");
                                    // AQUÍ PONES TU MENÚ DE LONGITUD...
                                    break;

                                case "4": // SALIR
                                    mantenerSesionActivaSistema = false;
                                    Console.WriteLine("\nCerrando sesión de forma segura...");
                                    break;

                                default:
                                    Console.ForegroundColor = ConsoleColor.Yellow;
                                    Console.WriteLine("\n[!] Opción no reconocida. Intente nuevamente.");
                                    Console.ResetColor();
                                    break;
                            }

                            if (mantenerSesionActivaSistema)
                            {
                                Console.WriteLine("\nPresione cualquier tecla para volver al menú principal...");
                                Console.ReadKey();
                            }
                        }
                        // --- FIN DEL MENÚ INTERACTIVO ---
                    }
                    else
                    {
                        Console.ForegroundColor = ConsoleColor.Red;
                        Console.WriteLine("\n[-] ACCESO DENEGADO. Credenciales incorrectas.");
                        Console.ResetColor();
                    }
                }
            }
            catch (Exception ex)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("\n[!] Error de conexión con el servidor SOAP: " + ex.Message);
                Console.ResetColor();
            }

            Console.WriteLine("\n==================================================");
            Console.WriteLine("Presione cualquier tecla para salir...");
            Console.ReadKey();
        }
    }
}