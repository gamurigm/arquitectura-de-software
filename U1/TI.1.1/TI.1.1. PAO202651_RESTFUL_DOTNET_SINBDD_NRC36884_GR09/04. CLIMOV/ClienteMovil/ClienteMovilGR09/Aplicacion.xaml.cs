using Microsoft.Extensions.DependencyInjection;

namespace ClienteMovilGR09;

public partial class Aplicacion : Application
{
	public Aplicacion()
	{
		InitializeComponent();
        UserAppTheme = AppTheme.Dark;
	}

	protected override Window CreateWindow(IActivationState? activationState)
	{
		return new Window(new NavegacionAplicacion());
	}
}
