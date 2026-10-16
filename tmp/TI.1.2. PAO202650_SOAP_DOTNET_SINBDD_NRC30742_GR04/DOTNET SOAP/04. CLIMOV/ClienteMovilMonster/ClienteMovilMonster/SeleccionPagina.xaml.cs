namespace ClienteMovilMonster;

public partial class SeleccionPagina : ContentPage
{
    public SeleccionPagina()
    {
        InitializeComponent();
    }

    private async void OnMasaClicked(object? sender, EventArgs e)
        => await Navigation.PushAsync(new MainPage("Masa"));

    private async void OnTempClicked(object? sender, EventArgs e)
        => await Navigation.PushAsync(new MainPage("Temperatura"));

    private async void OnLongClicked(object? sender, EventArgs e)
        => await Navigation.PushAsync(new MainPage("Longitud"));
}