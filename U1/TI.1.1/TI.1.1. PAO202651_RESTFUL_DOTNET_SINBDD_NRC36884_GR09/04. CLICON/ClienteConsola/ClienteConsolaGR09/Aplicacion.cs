using ClienteConsolaGR09.Controladores;
using ClienteConsolaGR09.Servicios;
using ClienteConsolaGR09.Vistas;

var controlador = new ControladorConsola(new ClienteRest(), new VistaConsola());
await controlador.EjecutarAsync();
