using System;
using System.Drawing;
using System.Windows.Forms;
using ClienteEscritorioMonster.ProxyEscritorioMonster;

namespace ClienteEscritorioMonster.ec.edu.monster.vista
{
    public partial class LoginVista : Form
    {
        public LoginVista()
        {
            InitializeComponent();


            this.BackColor = Color.FromArgb(174, 231, 248);
            btnValidarCredencialesAccesoWCF.BackColor = Color.FromArgb(171, 241, 188);
        }

        private void btnValidarCredencialesAccesoWCF_Click(object sender, EventArgs e)
        {
            string cadenaUsuarioCapturada = txtNombreUsuarioInstitucionalIngresado.Text;
            string cadenaClaveCapturada = txtClaveAccesoSeguridadIngresada.Text;

            if (string.IsNullOrWhiteSpace(cadenaUsuarioCapturada) || string.IsNullOrWhiteSpace(cadenaClaveCapturada))
            {
                MessageBox.Show("Por favor, ingrese sus credenciales completas.", "Campos Vacíos", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            try
            {
                using (ServicioMonsterClient objetoClienteEscritorioWCF = new ServicioMonsterClient())
                {
                    bool estadoValidacionExitosaServidor = objetoClienteEscritorioWCF.ValidarAcceso(cadenaUsuarioCapturada, cadenaClaveCapturada);

                    if (estadoValidacionExitosaServidor)
                    {
                        MessageBox.Show("¡Acceso concedido!", "Ingreso Exitoso", MessageBoxButtons.OK, MessageBoxIcon.Information);

                        DashboardVista formularioDashboard = new DashboardVista();
                        formularioDashboard.Show();
                        this.Hide();
                    }
                    else
                    {
                        MessageBox.Show("Credenciales incorrectas. Verifique e intente nuevamente.", "Acceso Denegado", MessageBoxButtons.OK, MessageBoxIcon.Error);

                        txtClaveAccesoSeguridadIngresada.Clear();
                        txtNombreUsuarioInstitucionalIngresado.Focus();
                    }
                }
            }
            catch (Exception excepcionServidorWCF)
            {
                MessageBox.Show("Fallo de comunicación con el IIS: " + excepcionServidorWCF.Message, "Error Crítico de Red", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
        }

        private void label1_Click(object sender, EventArgs e)
        {
        }

        private void btnValidarCredencialesAccesoWCF_Load(object sender, EventArgs e)
        {
        }
    }
}