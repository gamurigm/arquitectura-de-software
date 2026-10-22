using Android.App;
using Android.Content.PM;
using Microsoft.Maui;

namespace EurekaBank.ClienteMovil;

[Activity(
    Theme = "@style/MainTheme",
    MainLauncher = true,
    Exported = true,
    ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation |
                           ConfigChanges.UiMode | ConfigChanges.ScreenLayout |
                           ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public sealed class MainActivity : MauiAppCompatActivity
{
}
