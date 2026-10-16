using ProxyMovilMonster;

namespace ClienteMovilMonster;

public partial class MainPage : ContentPage
{
    private string _categoria;

    public MainPage(string categoria)
    {
        InitializeComponent();
        _categoria = categoria;
        CargarOpciones();
    }

    private void CargarOpciones()
    {
        pckOperacion.Items.Clear();

        switch (_categoria)
        {
            case "Masa":
                pckOperacion.Items.Add("Gramos a Kilogramos");
                pckOperacion.Items.Add("Kilogramos a Libras");
                pckOperacion.Items.Add("Libras a Onzas");
                pckOperacion.Items.Add("Toneladas a Kilogramos");
                pckOperacion.Items.Add("Onzas a Gramos");
                break;
            case "Temperatura":
                pckOperacion.Items.Add("Celsius a Fahrenheit");
                pckOperacion.Items.Add("Fahrenheit a Celsius");
                pckOperacion.Items.Add("Celsius a Kelvin");
                pckOperacion.Items.Add("Kelvin a Celsius");
                pckOperacion.Items.Add("Fahrenheit a Kelvin");
                break;
            case "Longitud":
                pckOperacion.Items.Add("Metros a Kilometros");
                pckOperacion.Items.Add("Kilometros a Millas");
                pckOperacion.Items.Add("Metros a Pies");
                pckOperacion.Items.Add("Pies a Pulgadas");
                pckOperacion.Items.Add("Centimetros a Metros");
                break;
        }
        pckOperacion.SelectedIndex = 0;
    }

    private async void OnCalcularClicked(object? sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(txtValor?.Text)) return;

        try
        {
            double valor = Convert.ToDouble(txtValor.Text);
            string seleccion = pckOperacion.SelectedItem?.ToString() ?? "";
            double resultado = 0;

            lblResultado.Text = "Procesando...";

            string urlServicio = "http://192.168.3.13/ServicioMonster/ServicioMonster.svc";

            using (var cliente = new ServicioMonsterClient())
            {
                cliente.Endpoint.Address = new System.ServiceModel.EndpointAddress(urlServicio);
                cliente.Endpoint.Binding.SendTimeout = TimeSpan.FromSeconds(5);

                // --- MASA ---
                if (seleccion == "Gramos a Kilogramos")
                    resultado = await cliente.GramosAKilogramosAsync(valor);
                else if (seleccion == "Kilogramos a Libras")
                    resultado = await cliente.KilogramosALibrasAsync(valor);
                else if (seleccion == "Libras a Onzas")
                    resultado = await cliente.LibrasAOnzasAsync(valor);
                else if (seleccion == "Toneladas a Kilogramos")
                    resultado = await cliente.ToneladasAKilogramosAsync(valor);
                else if (seleccion == "Onzas a Gramos")
                    resultado = await cliente.OnzasAGramosAsync(valor);

                // --- TEMPERATURA ---
                else if (seleccion == "Celsius a Fahrenheit")
                    resultado = await cliente.CelsiusAFahrenheitAsync(valor);
                else if (seleccion == "Fahrenheit a Celsius")
                    resultado = await cliente.FahrenheitACelsiusAsync(valor);
                else if (seleccion == "Celsius a Kelvin")
                    resultado = await cliente.CelsiusAKelvinAsync(valor);
                else if (seleccion == "Kelvin a Celsius")
                    resultado = await cliente.KelvinACelsiusAsync(valor);
                else if (seleccion == "Fahrenheit a Kelvin")
                    resultado = await cliente.FahrenheitAKelvinAsync(valor);

                // --- LONGITUD ---
                else if (seleccion == "Metros a Kilometros")
                    resultado = await cliente.MetrosAKilometrosAsync(valor);
                else if (seleccion == "Kilometros a Millas")
                    resultado = await cliente.KilometrosAMillasAsync(valor);
                else if (seleccion == "Metros a Pies")
                    resultado = await cliente.MetrosAPiesAsync(valor);
                else if (seleccion == "Pies a Pulgadas")
                    resultado = await cliente.PiesAPulgadasAsync(valor);
                else if (seleccion == "Centimetros a Metros")
                    resultado = await cliente.CentimetrosAMetrosAsync(valor);

                lblResultado.Text = $"Res: {resultado:N4}";
                await cliente.CloseAsync();
            }
        }
        catch (Exception ex)
        {
            await DisplayAlertAsync("Error de Red", "No se pudo conectar al servidor en 192.168.3.13", "OK");
            lblResultado.Text = "Error";
        }
    }
}