using Microsoft.Extensions.Logging;
#if ANDROID
using Microsoft.Maui.Handlers;
#endif

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

#if ANDROID
		EntryHandler.Mapper.AppendToMapping("CampoSinSubrayado", (handler, _) =>
			handler.PlatformView.BackgroundTintList = Android.Content.Res.ColorStateList.ValueOf(Android.Graphics.Color.Transparent));
#endif

#if DEBUG
		configurador.Logging.AddDebug();
#endif

		return configurador.Build();
	}
}
