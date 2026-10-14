using Android.App;
using Android.Runtime;

namespace ClienteMovilGR09;

[Application]
public class AplicacionAndroid : MauiApplication
{
	public AplicacionAndroid(IntPtr handle, JniHandleOwnership ownership)
		: base(handle, ownership)
	{
	}

	protected override MauiApp CreateMauiApp() => InicializadorMaui.CrearAplicacionMaui();
}
