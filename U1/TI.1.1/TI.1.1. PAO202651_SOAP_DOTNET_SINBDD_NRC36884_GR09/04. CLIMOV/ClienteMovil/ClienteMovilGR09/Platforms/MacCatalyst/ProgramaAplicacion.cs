using ObjCRuntime;
using UIKit;

namespace ClienteMovilGR09;

public class ProgramaAplicacion
{
	// Punto de entrada de la aplicación.
	static void Main(string[] args)
	{
		// Para usar otra clase delegada de aplicación, especifícala aquí.

		UIApplication.Main(args, null, typeof(DelegadoAplicacion));
	}
}
