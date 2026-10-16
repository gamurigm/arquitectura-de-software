using System;
using System.Drawing;
using System.Windows.Forms;
using ClienteEscritorioMonster.ec.edu.monster.controlador;

namespace ClienteEscritorioMonster.ec.edu.monster.vista
{
    public partial class DashboardVista : Form
    {
        // El puente hacia el IIS
        private DashboardControlador controladorSistema;

        public DashboardVista()
        {
            InitializeComponent();

            // 1. Colores institucionales
            this.BackColor = Color.FromArgb(174, 231, 248); // Celeste claro
            btnEjecutarConversionMasaWCF.BackColor = Color.FromArgb(171, 241, 188);
            btnEjecutarConversionTempWCF.BackColor = Color.FromArgb(171, 241, 188);
            btnEjecutarConversionLongWCF.BackColor = Color.FromArgb(171, 241, 188);

            // 2. Instanciamos el controlador
            controladorSistema = new DashboardControlador();

            // 3. Llenamos los ComboBox automáticamente al abrir la pantalla
            CargarListasDesplegables();
        }

        private void CargarListasDesplegables()
        {
            // Opciones de Masa
            cmbListaOperacionesMasaSeleccionada.Items.AddRange(new string[] {
                "Gramos a Kilogramos",
                "Kilogramos a Libras",
                "Libras a Onzas",
                "Toneladas a Kilogramos",
                "Onzas a Gramos"
            });
            cmbListaOperacionesMasaSeleccionada.SelectedIndex = 0; // Selecciona el primero por defecto

            // Opciones de Temperatura
            cmbListaOperacionesTempSeleccionada.Items.AddRange(new string[] {
                "Celsius a Fahrenheit",
                "Fahrenheit a Celsius",
                "Celsius a Kelvin",
                "Kelvin a Celsius",
                "Fahrenheit a Kelvin"
            });
            cmbListaOperacionesTempSeleccionada.SelectedIndex = 0;

            // Opciones de Longitud
            cmbListaOperacionesLongSeleccionada.Items.AddRange(new string[] {
                "Metros a Kilometros",
                "Kilometros a Millas",
                "Metros a Pies",
                "Pies a Pulgadas",
                "Centimetros a Metros"
            });
            cmbListaOperacionesLongSeleccionada.SelectedIndex = 0;
        }

        // ==============================================
        // EVENTO: BOTÓN MASA
        // ==============================================
        private void btnEjecutarConversionMasaWCF_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtValorNumericoIngresadoMasa.Text)) return;

            try
            {
                double valor = Convert.ToDouble(txtValorNumericoIngresadoMasa.Text);
                string opcion = cmbListaOperacionesMasaSeleccionada.Text;

                double resultado = controladorSistema.EjecutarConversionMasa(opcion, valor);
                lblResultadoCalculoMasaSistema.Text = $"{valor} -> {resultado}";
            }
            catch (FormatException) { MessageBox.Show("Ingrese solo números.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning); }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error WCF", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        // ==============================================
        // EVENTO: BOTÓN TEMPERATURA
        // ==============================================
        private void btnEjecutarConversionTempWCF_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtValorNumericoIngresadoTemp.Text)) return;

            try
            {
                double valor = Convert.ToDouble(txtValorNumericoIngresadoTemp.Text);
                string opcion = cmbListaOperacionesTempSeleccionada.Text;

                double resultado = controladorSistema.EjecutarConversionTemperatura(opcion, valor);
                lblResultadoCalculoTempSistema.Text = $"{valor} -> {resultado}";
            }
            catch (FormatException) { MessageBox.Show("Ingrese solo números.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning); }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error WCF", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        // ==============================================
        // EVENTO: BOTÓN LONGITUD
        // ==============================================
        private void btnEjecutarConversionLongWCF_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtValorNumericoIngresadoLong.Text)) return;

            try
            {
                double valor = Convert.ToDouble(txtValorNumericoIngresadoLong.Text);
                string opcion = cmbListaOperacionesLongSeleccionada.Text;

                double resultado = controladorSistema.EjecutarConversionLongitud(opcion, valor);
                lblResultadoCalculoLongSistema.Text = $"{valor} -> {resultado}";
            }
            catch (FormatException) { MessageBox.Show("Ingrese solo números.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning); }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error WCF", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        // ==============================================
        // CERRAR APLICACIÓN
        // ==============================================
        protected override void OnFormClosed(FormClosedEventArgs e)
        {
            base.OnFormClosed(e);
            Application.Exit();
        }

        private void cmbListaOperacionesTempSeleccionada_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void cmbListaOperacionesLongSeleccionada_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void tabPage2_Click(object sender, EventArgs e)
        {

        }

        private void cmbListaOperacionesMasaSeleccionada_SelectedIndexChanged(object sender, EventArgs e)
        {

        }
    }
}