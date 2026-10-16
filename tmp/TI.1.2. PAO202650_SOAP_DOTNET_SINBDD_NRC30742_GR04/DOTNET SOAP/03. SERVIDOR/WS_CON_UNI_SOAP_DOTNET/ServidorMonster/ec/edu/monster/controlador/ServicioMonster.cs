using System;

namespace ec.edu.monster.controlador // O el namespace donde tengas esta clase en tu servidor
{
    public class ServicioMonster : ec.edu.monster.modelo.IServicioMonster
    {
        public bool ValidarAcceso(string usuario, string contrasena)
        {
            if (usuario == "Monster" && contrasena == "Monster9")
                return true;
            else
                return false;
        }

        // ==========================================
        // --- MASA ---
        // ==========================================
        public double GramosAKilogramos(double valor)
        {
            return valor / 1000.0;
        }

        public double KilogramosALibras(double valor)
        {
            return valor * 2.20462;
        }

        public double LibrasAOnzas(double valor)
        {
            return valor * 16.0;
        }

        public double ToneladasAKilogramos(double valor)
        {
            return valor * 1000.0;
        }

        public double OnzasAGramos(double valor)
        {
            return valor * 28.3495;
        }

        // ==========================================
        // --- TEMPERATURA ---
        // ==========================================
        public double CelsiusAFahrenheit(double valor)
        {
            return (valor * 9.0 / 5.0) + 32.0;
        }

        public double FahrenheitACelsius(double valor)
        {
            return (valor - 32.0) * 5.0 / 9.0;
        }

        public double CelsiusAKelvin(double valor)
        {
            return valor + 273.15;
        }

        public double KelvinACelsius(double valor)
        {
            return valor - 273.15;
        }

        public double FahrenheitAKelvin(double valor)
        {
            return (valor - 32.0) * 5.0 / 9.0 + 273.15;
        }

        // ==========================================
        // --- LONGITUD ---
        // ==========================================
        public double MetrosAKilometros(double valor)
        {
            return valor / 1000.0;
        }

        public double KilometrosAMillas(double valor)
        {
            return valor / 1.60934;
        }

        public double MetrosAPies(double valor)
        {
            return valor * 3.28084;
        }

        public double PiesAPulgadas(double valor)
        {
            return valor * 12.0;
        }

        public double CentimetrosAMetros(double valor)
        {
            return valor / 100.0;
        }
    }
}