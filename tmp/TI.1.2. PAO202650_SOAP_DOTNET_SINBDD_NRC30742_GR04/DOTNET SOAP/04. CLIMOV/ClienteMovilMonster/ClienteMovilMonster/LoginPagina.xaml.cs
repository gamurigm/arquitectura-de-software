namespace ClienteMovilMonster;

public partial class LoginPagina : ContentPage
{
    public LoginPagina()
    {
        InitializeComponent();
    }

    private async void OnLoginClicked(object? sender, EventArgs e)
    {
        // Verificamos nulos antes de usar los campos
        if (string.IsNullOrEmpty(txtUser?.Text) || string.IsNullOrEmpty(txtPass?.Text))
        {
            await DisplayAlertAsync("Atención", "Llene todos los campos", "OK");
            return;
        }

        if (txtUser.Text == "Monster" && txtPass.Text == "Monster9")
        {
            await Navigation.PushAsync(new SeleccionPagina());
        }
        else
        {
            await DisplayAlertAsync("Error", "Credenciales incorrectas", "OK");
        }
    }
}