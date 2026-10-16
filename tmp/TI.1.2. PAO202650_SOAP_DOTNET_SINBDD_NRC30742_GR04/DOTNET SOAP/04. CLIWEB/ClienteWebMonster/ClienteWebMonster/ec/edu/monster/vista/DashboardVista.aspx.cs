using System;
using System.Web.UI.WebControls;
using ClienteWebMonster.ec.edu.monster.controlador;

namespace ClienteWebMonster.ec.edu.monster.vista
{
    public partial class DashboardVista : System.Web.UI.Page
    {
        private DashboardControlador controladorSistema = new DashboardControlador();

        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                CargarListasDesplegables();
            }
        }

        private void CargarListasDesplegables()
        {
            cmbListaOperacionesMasaSeleccionada.Items.Add("Gramos a Kilogramos");
            cmbListaOperacionesMasaSeleccionada.Items.Add("Kilogramos a Libras");
            cmbListaOperacionesMasaSeleccionada.Items.Add("Libras a Onzas");
            cmbListaOperacionesMasaSeleccionada.Items.Add("Toneladas a Kilogramos");
            cmbListaOperacionesMasaSeleccionada.Items.Add("Onzas a Gramos");

            cmbListaOperacionesTempSeleccionada.Items.Add("Celsius a Fahrenheit");
            cmbListaOperacionesTempSeleccionada.Items.Add("Fahrenheit a Celsius");
            cmbListaOperacionesTempSeleccionada.Items.Add("Celsius a Kelvin");
            cmbListaOperacionesTempSeleccionada.Items.Add("Kelvin a Celsius");
            cmbListaOperacionesTempSeleccionada.Items.Add("Fahrenheit a Kelvin");

            cmbListaOperacionesLongSeleccionada.Items.Add("Metros a Kilometros");
            cmbListaOperacionesLongSeleccionada.Items.Add("Kilometros a Millas");
            cmbListaOperacionesLongSeleccionada.Items.Add("Metros a Pies");
            cmbListaOperacionesLongSeleccionada.Items.Add("Pies a Pulgadas");
            cmbListaOperacionesLongSeleccionada.Items.Add("Centimetros a Metros");
        }


        protected void CambiarPestana(object sender, EventArgs e)
        {
            Button btn = (Button)sender;
            int indice = Convert.ToInt32(btn.CommandArgument);
            mvwPestanas.ActiveViewIndex = indice;

            btnTabMasa.CssClass = "boton-pestana";
            btnTabTemp.CssClass = "boton-pestana";
            btnTabLong.CssClass = "boton-pestana";

            if (indice == 0) btnTabMasa.CssClass = "boton-pestana activa";
            if (indice == 1) btnTabTemp.CssClass = "boton-pestana activa";
            if (indice == 2) btnTabLong.CssClass = "boton-pestana activa";
        }


        protected void btnEjecutarConversionMasaWCF_Click(object sender, EventArgs e)
        {
            try
            {
                double valor = Convert.ToDouble(txtValorNumericoIngresadoMasa.Text);
                string opcion = cmbListaOperacionesMasaSeleccionada.SelectedValue;
                double resultado = controladorSistema.EjecutarConversionMasa(opcion, valor);
                lblResultadoCalculoMasaSistema.Text = $"{valor} -> {resultado}";
            }
            catch (Exception ex) { lblResultadoCalculoMasaSistema.Text = "Error: " + ex.Message; }
        }

        protected void btnEjecutarConversionTempWCF_Click(object sender, EventArgs e)
        {
            try
            {
                double valor = Convert.ToDouble(txtValorNumericoIngresadoTemp.Text);
                string opcion = cmbListaOperacionesTempSeleccionada.SelectedValue;
                double resultado = controladorSistema.EjecutarConversionTemperatura(opcion, valor);
                lblResultadoCalculoTempSistema.Text = $"{valor} -> {resultado}";
            }
            catch (Exception ex) { lblResultadoCalculoTempSistema.Text = "Error: " + ex.Message; }
        }

        protected void btnEjecutarConversionLongWCF_Click(object sender, EventArgs e)
        {
            try
            {
                double valor = Convert.ToDouble(txtValorNumericoIngresadoLong.Text);
                string opcion = cmbListaOperacionesLongSeleccionada.SelectedValue;
                double resultado = controladorSistema.EjecutarConversionLongitud(opcion, valor);
                lblResultadoCalculoLongSistema.Text = $"{valor} -> {resultado}";
            }
            catch (Exception ex) { lblResultadoCalculoLongSistema.Text = "Error: " + ex.Message; }
        }
    }
}