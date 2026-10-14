using Microsoft.AspNetCore.Mvc.ApplicationModels;

namespace ClienteWebGR09.Controladores;

public sealed class ConvencionNombreControlador : IControllerModelConvention
{
    public void Apply(ControllerModel controlador)
    {
        const string prefijo = "Controlador";
        var nombre = controlador.ControllerType.Name;
        if (nombre.StartsWith(prefijo, StringComparison.Ordinal))
        {
            controlador.ControllerName = nombre[prefijo.Length..];
        }
    }
}