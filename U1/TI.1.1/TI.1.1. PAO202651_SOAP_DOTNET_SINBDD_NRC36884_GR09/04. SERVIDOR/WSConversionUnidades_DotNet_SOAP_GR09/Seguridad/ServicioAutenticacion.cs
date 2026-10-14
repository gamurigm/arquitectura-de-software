namespace WSConversionUnidades_DotNet_SOAP_GR09.Seguridad;

public sealed class ServicioAutenticacion
{
    private const string UsuarioInicial = "Monster";
    private const string ContraseniaInicial = "Monster9";
    private readonly object _bloqueo = new();
    private readonly AdministradorTokens _tokens;
    private string _contrasenia = ContraseniaInicial;

    public ServicioAutenticacion(AdministradorTokens tokens)
    {
        _tokens = tokens;
    }

    public string IniciarSesion(string usuario, string contrasenia)
    {
        lock (_bloqueo)
        {
            if (!string.Equals(usuario, UsuarioInicial, StringComparison.Ordinal)
                || !string.Equals(contrasenia, _contrasenia, StringComparison.Ordinal))
            {
                throw new InvalidOperationException("Credenciales incorrectas");
            }
        }

        return _tokens.Generar();
    }

    public string CambiarContrasenia(string contraseniaActual, string contraseniaNueva)
    {
        lock (_bloqueo)
        {
            if (!string.Equals(contraseniaActual, _contrasenia, StringComparison.Ordinal))
            {
                throw new InvalidOperationException("La contraseña actual es incorrecta");
            }

            if (string.IsNullOrWhiteSpace(contraseniaNueva))
            {
                throw new ArgumentException("La contraseña nueva no puede estar vacía", nameof(contraseniaNueva));
            }

            _contrasenia = contraseniaNueva;
        }

        return "Contrasenia actualizada correctamente";
    }

    public bool ValidarToken(string token) => _tokens.Validar(token);
}
