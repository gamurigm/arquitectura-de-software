using System;
using System.Windows.Forms;
using CONUNI_SOAP_DOTNET_CLIESC_G09.vista;

namespace CONUNI_SOAP_DOTNET_CLIESC_G09
{
    internal static class Program
    {
        /// <summary>
        /// Punto de entrada principal para la aplicación.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new VentanaLogin());
        }
    }
}
