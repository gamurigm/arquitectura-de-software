using System.ServiceModel;

namespace ec.edu.monster.modelo
{
    [ServiceContract]
    public interface IServicioMonster
    {
        [OperationContract]
        bool ValidarAcceso(string usuario, string contrasena);

        // --- MASA ---
        [OperationContract]
        double GramosAKilogramos(double valor);
        [OperationContract]
        double KilogramosALibras(double valor);
        [OperationContract]
        double LibrasAOnzas(double valor);
        [OperationContract]
        double ToneladasAKilogramos(double valor);
        [OperationContract]
        double OnzasAGramos(double valor);

        // --- TEMPERATURA ---
        [OperationContract]
        double CelsiusAFahrenheit(double valor);
        [OperationContract]
        double FahrenheitACelsius(double valor);
        [OperationContract]
        double CelsiusAKelvin(double valor);
        [OperationContract]
        double KelvinACelsius(double valor);
        [OperationContract]
        double FahrenheitAKelvin(double valor);

        // --- LONGITUD ---
        [OperationContract]
        double MetrosAKilometros(double valor);
        [OperationContract]
        double KilometrosAMillas(double valor);
        [OperationContract]
        double MetrosAPies(double valor);
        [OperationContract]
        double PiesAPulgadas(double valor);
        [OperationContract]
        double CentimetrosAMetros(double valor);
    }
}