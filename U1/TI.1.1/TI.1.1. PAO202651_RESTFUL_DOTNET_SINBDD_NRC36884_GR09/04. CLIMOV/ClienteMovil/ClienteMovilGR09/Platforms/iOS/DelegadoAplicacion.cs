using Foundation;

namespace ClienteMovilGR09;

[Register("DelegadoAplicacion")]
public class DelegadoAplicacion : MauiUIApplicationDelegate
{
	protected override MauiApp CreateMauiApp() => InicializadorMaui.CrearAplicacionMaui();
}
