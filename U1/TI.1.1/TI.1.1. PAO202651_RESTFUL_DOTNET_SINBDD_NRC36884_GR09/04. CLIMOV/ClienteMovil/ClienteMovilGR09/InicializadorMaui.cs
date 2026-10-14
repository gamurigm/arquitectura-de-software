using Microsoft.Extensions.Logging;

namespace ClienteMovilGR09;

public static class InicializadorMaui
{
	public static MauiApp CrearAplicacionMaui()
	{
		var configurador = MauiApp.CreateBuilder();
		configurador
			.UseMauiApp<Aplicacion>()
			.ConfigureFonts(fonts =>
			{
				fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
				fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
			});

#if DEBUG
		configurador.Logging.AddDebug();
#endif

		return configurador.Build();
	}
}
