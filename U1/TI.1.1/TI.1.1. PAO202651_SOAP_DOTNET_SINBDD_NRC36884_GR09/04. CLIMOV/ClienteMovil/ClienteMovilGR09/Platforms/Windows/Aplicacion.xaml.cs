using Microsoft.UI.Xaml;

// Para conocer WinUI y la estructura del proyecto,
// consulta las plantillas del proyecto: http://aka.ms/winui-project-info.

namespace ClienteMovilGR09.WinUI;

/// <summary>
/// Complementa el comportamiento predeterminado de Application para esta aplicación.
/// </summary>
public partial class Aplicacion : MauiWinUIApplication
{
	/// <summary>
	/// Inicializa el objeto único de la aplicación. Esta es la primera línea de código propio
	/// ejecutado y equivale a main() o WinMain().
	/// </summary>
	public Aplicacion()
	{
		this.InitializeComponent();
	}

	protected override MauiApp CreateMauiApp() => InicializadorMaui.CrearAplicacionMaui();
}
