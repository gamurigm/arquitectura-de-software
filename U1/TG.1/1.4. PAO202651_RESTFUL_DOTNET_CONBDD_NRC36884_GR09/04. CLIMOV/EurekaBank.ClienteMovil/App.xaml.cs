namespace EurekaBank.ClienteMovil;

public partial class App(MainPage mainPage) : Application
{
    protected override Window CreateWindow(IActivationState? activationState) => new(mainPage);
}
