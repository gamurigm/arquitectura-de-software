using System;
using ClienteWebMonster.ProxyWebMonster;

namespace ClienteWebMonster.ec.edu.monster.vista
{
    public partial class LoginVista : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            lblMensajeError.Text = "";
        }

        protected void btnValidarCredencialesAccesoWCF_Click(object sender, EventArgs e)
        {
            string usuario = txtNombreUsuarioInstitucionalIngresado.Text;
            string clave = txtClaveAccesoSeguridadIngresada.Text;

            if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(clave))
            {
                lblMensajeError.Text = "Por favor, ingrese sus credenciales.";
                return;
            }

            try
            {
                using (ServicioMonsterClient clienteWebWCF = new ServicioMonsterClient())
                {
                    bool accesoValido = clienteWebWCF.ValidarAcceso(usuario, clave);

                    if (accesoValido)
                    {
                        Response.Redirect("DashboardVista.aspx");
                    }
                    else
                    {
                        lblMensajeError.Text = "Credenciales incorrectas.";
                    }
                }
            }
            catch (Exception ex)
            {
                lblMensajeError.Text = "Fallo de conexión al IIS: " + ex.Message;
            }
        }
    }
}