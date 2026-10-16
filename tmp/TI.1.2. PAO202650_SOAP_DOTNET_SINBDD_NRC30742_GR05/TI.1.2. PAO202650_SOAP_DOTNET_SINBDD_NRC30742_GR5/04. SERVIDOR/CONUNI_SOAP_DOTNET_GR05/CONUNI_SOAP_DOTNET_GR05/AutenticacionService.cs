using System;

namespace CONUNI_SOAP_DOTNET_GR09
{
    /// <summary>
    /// Servicio de autenticación centralizado en el servidor.
    /// Las credenciales residen ÚNICAMENTE aquí, en el servidor.
    /// Los clientes deben invocar la operación Login del WebService.
    /// </summary>
    public class AutenticacionService
    {
        // Credenciales almacenadas SOLO en el servidor
        private const string UsuarioValido    = "MONSTER";
        private const string ContrasenaValida = "MONSTER9";

        /// <summary>
        /// Valida las credenciales de un usuario.
        /// </summary>
        /// <param name="usuario">Nombre de usuario enviado por el cliente</param>
        /// <param name="contrasena">Contraseña enviada por el cliente</param>
        /// <returns><c>true</c> si las credenciales son correctas</returns>
        public bool ValidarCredenciales(string usuario, string contrasena)
        {
            if (string.IsNullOrEmpty(usuario) || string.IsNullOrEmpty(contrasena))
                return false;

            return UsuarioValido.Equals(usuario.Trim().ToUpper())
                && ContrasenaValida.Equals(contrasena.Trim());
        }
    }
}
