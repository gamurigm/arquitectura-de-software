using System;
using System.Drawing;
using System.Windows.Forms;
using CONUNI_SOAP_DOTNET_CLIESC_G09.servicio;

namespace CONUNI_SOAP_DOTNET_CLIESC_G09.vista
{
    /// <summary>
    /// Ventana principal profesional de Monsters Inc. Converter
    /// Diseño empresarial con colores oficiales y layout optimizado
    /// </summary>
    public class VentanaPrincipal : Form
    {
        // Colores oficiales de Monsters Inc.
        private static readonly Color AZUL_PRINCIPAL = Color.FromArgb(33, 169, 218);
        private static readonly Color ROJO_MONSTER = Color.FromArgb(246, 105, 113);
        private static readonly Color AMARILLO_MONSTER = Color.FromArgb(245, 216, 128);
        private static readonly Color PURPURA_MONSTER = Color.FromArgb(140, 107, 205);
        private static readonly Color AZUL_CLARO = Color.FromArgb(159, 220, 250);
        private static readonly Color BLANCO = Color.White;
        private static readonly Color GRIS_OSCURO = Color.FromArgb(60, 60, 60);
        private static readonly Color GRIS_CLARO = Color.FromArgb(240, 240, 240);

        private Panel panelHeader;
        private Panel panelNavegacion;
        private Panel panelContenido;
        private Panel panelFooter;

        private Button btnTemperatura;
        private Button btnLongitud;
        private Button btnPeso;

        private PanelTemperatura panelTemperatura;
        private PanelLongitud panelLongitud;
        private PanelPeso panelPeso;

        private ClienteConversionSoap clienteSOAP;

        public VentanaPrincipal(ClienteConversionSoap cliente)
        {
            clienteSOAP = cliente;
            InicializarComponentes();
            ConfigurarVentana();
            ConfigurarLayout();
            ConfigurarEventos();
            MostrarPanelTemperatura();
        }

        private void InicializarComponentes()
        {
            panelHeader = new Panel();
            panelHeader.BackColor = AZUL_PRINCIPAL;
            panelHeader.Height = 80; // Solo el header azul

            panelNavegacion = new Panel();
            panelNavegacion.BackColor = BLANCO; // Barra blanca separada
            panelNavegacion.Height = 70;

            panelContenido = new Panel();
            panelContenido.BackColor = BLANCO;
            panelContenido.Padding = new Padding(20);

            panelFooter = new Panel();
            panelFooter.BackColor = GRIS_OSCURO;
            panelFooter.Height = 40;

            btnTemperatura = CrearBotonNavegacion("TEMPERATURA", AZUL_PRINCIPAL);
            btnLongitud = CrearBotonNavegacion("LONGITUD", PURPURA_MONSTER);
            btnPeso = CrearBotonNavegacion("PESO", ROJO_MONSTER);

            panelTemperatura = new PanelTemperatura(clienteSOAP);
            panelLongitud = new PanelLongitud(clienteSOAP);
            panelPeso = new PanelPeso(clienteSOAP);
        }

        private Button CrearBotonNavegacion(string texto, Color color)
        {
            Button boton = new Button();
            boton.Text = texto;
            boton.BackColor = color;
            boton.ForeColor = BLANCO;
            boton.Font = new Font("Arial", 11F, FontStyle.Bold);
            boton.Size = new Size(130, 40);
            boton.FlatStyle = FlatStyle.Flat;
            boton.FlatAppearance.BorderSize = 0;
            boton.Cursor = Cursors.Hand;
            boton.Margin = new Padding(3, 0, 3, 0);

            boton.MouseEnter += (s, e) => 
            {
                if (boton.BackColor != GRIS_OSCURO)
                    boton.BackColor = OscurecerColor(color);
            };
            boton.MouseLeave += (s, e) => 
            {
                if (boton.BackColor != GRIS_OSCURO)
                    boton.BackColor = color;
            };

            return boton;
        }

        private Color OscurecerColor(Color color)
        {
            return Color.FromArgb(
                Math.Max(0, color.R - 30),
                Math.Max(0, color.G - 30),
                Math.Max(0, color.B - 30)
            );
        }

        private void ConfigurarVentana()
        {
            this.Text = "Monsters Inc. Converter - Sistema de Conversiones Empresarial";
            this.Size = new Size(1200, 800);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.MinimumSize = new Size(1000, 700);
            this.BackColor = BLANCO;
        }

        private void ConfigurarLayout()
        {
            this.DockPadding.All = 0;

            // Barra de navegación blanca (PRIMERO - con botones)
            ConfigurarNavegacion();
            panelNavegacion.Dock = DockStyle.Top;
            this.Controls.Add(panelNavegacion);

            // Header azul (SEGUNDO - debajo de los botones)
            ConfigurarHeader();
            panelHeader.Dock = DockStyle.Top;
            this.Controls.Add(panelHeader);

            // Footer (TERCERO)
            ConfigurarFooter();
            panelFooter.Dock = DockStyle.Bottom;
            this.Controls.Add(panelFooter);

            // Panel central (CUARTO - se ajusta automáticamente)
            Panel panelCentral = new Panel();
            panelCentral.Dock = DockStyle.Fill;
            panelCentral.BackColor = BLANCO;

            // Panel contenido
            Panel panelContenidoPrincipal = new Panel();
            panelContenidoPrincipal.Dock = DockStyle.Fill;
            panelContenidoPrincipal.BackColor = BLANCO;
            panelContenidoPrincipal.Padding = new Padding(0);

            Panel panelFormulario = new Panel();
            panelFormulario.Dock = DockStyle.Fill;
            panelFormulario.BackColor = BLANCO;
            panelFormulario.Padding = new Padding(0);

            panelContenidoPrincipal.Controls.Add(panelFormulario);
            panelCentral.Controls.Add(panelContenidoPrincipal);

            this.Controls.Add(panelCentral);

            panelContenido = panelFormulario;
        }

        private void ConfigurarHeader()
        {
            panelHeader.Dock = DockStyle.Top;
            panelHeader.Padding = new Padding(20, 0, 20, 0);

            Panel panelLogo = new Panel();
            panelLogo.Dock = DockStyle.Left;
            panelLogo.BackColor = AZUL_PRINCIPAL;
            panelLogo.AutoSize = true;

            Label lblEmpresa = new Label();
            lblEmpresa.Text = "MONSTERS INC.";
            lblEmpresa.Font = new Font("Segoe UI", 22F, FontStyle.Bold);
            lblEmpresa.ForeColor = BLANCO;
            lblEmpresa.AutoSize = true;
            lblEmpresa.Padding = new Padding(10, 18, 15, 18);
            lblEmpresa.TextAlign = ContentAlignment.MiddleLeft;

            panelLogo.Controls.Add(lblEmpresa);

            Label lblTitulo = new Label();
            lblTitulo.Text = "Sistema de Conversiones";
            lblTitulo.Font = new Font("Segoe UI", 17F, FontStyle.Regular);
            lblTitulo.ForeColor = AZUL_CLARO;
            lblTitulo.TextAlign = ContentAlignment.MiddleCenter;
            lblTitulo.Dock = DockStyle.Fill;

            Panel panelUsuario = new Panel();
            panelUsuario.Dock = DockStyle.Right;
            panelUsuario.BackColor = AZUL_PRINCIPAL;
            panelUsuario.Width = 300;
            panelUsuario.Padding = new Padding(10, 0, 15, 0);

            FlowLayoutPanel panelUsuarioContenido = new FlowLayoutPanel();
            panelUsuarioContenido.Dock = DockStyle.Fill;
            panelUsuarioContenido.BackColor = AZUL_PRINCIPAL;
            panelUsuarioContenido.FlowDirection = FlowDirection.LeftToRight;
            panelUsuarioContenido.AutoSize = false;
            panelUsuarioContenido.WrapContents = false;

            Label lblUsuario = new Label();
            lblUsuario.Text = "Usuario: MONSTER";
            lblUsuario.Font = new Font("Segoe UI", 11F, FontStyle.Regular);
            lblUsuario.ForeColor = BLANCO;
            lblUsuario.AutoSize = true;
            lblUsuario.Margin = new Padding(0, 20, 12, 20);
            lblUsuario.TextAlign = ContentAlignment.MiddleCenter;

            Button btnCerrarSesion = new Button();
            btnCerrarSesion.Text = "Cerrar Sesión";
            btnCerrarSesion.Font = new Font("Segoe UI", 9F, FontStyle.Bold);
            btnCerrarSesion.ForeColor = BLANCO;
            btnCerrarSesion.BackColor = ROJO_MONSTER;
            btnCerrarSesion.FlatStyle = FlatStyle.Flat;
            btnCerrarSesion.FlatAppearance.BorderSize = 0;
            btnCerrarSesion.Cursor = Cursors.Hand;
            btnCerrarSesion.Size = new Size(140, 32);
            btnCerrarSesion.Margin = new Padding(0, 16, 0, 16);
            btnCerrarSesion.AutoSize = false;
            btnCerrarSesion.Click += (s, e) => CerrarSesion();

            panelUsuarioContenido.Controls.Add(lblUsuario);
            panelUsuarioContenido.Controls.Add(btnCerrarSesion);
            panelUsuario.Controls.Add(panelUsuarioContenido);

            panelHeader.Controls.Add(panelLogo);
            panelHeader.Controls.Add(lblTitulo);
            panelHeader.Controls.Add(panelUsuario);
        }

        private void ConfigurarNavegacion()
        {
            panelNavegacion.Dock = DockStyle.Top;
            panelNavegacion.Height = 70;
            panelNavegacion.BackColor = BLANCO;

            // Panel interior con TableLayoutPanel
            TableLayoutPanel tableLayoutNav = new TableLayoutPanel();
            tableLayoutNav.Dock = DockStyle.Fill;
            tableLayoutNav.ColumnCount = 2;
            tableLayoutNav.RowCount = 1;
            tableLayoutNav.BackColor = BLANCO;
            tableLayoutNav.Padding = new Padding(30, 15, 30, 15);
            tableLayoutNav.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));
            tableLayoutNav.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100F));
            tableLayoutNav.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));

            // Label a la izquierda
            Label lblTituloNav = new Label();
            lblTituloNav.Text = "SELECCIONE EL TIPO DE CONVERSIÓN";
            lblTituloNav.Font = new Font("Arial", 14F, FontStyle.Bold);
            lblTituloNav.ForeColor = GRIS_OSCURO;
            lblTituloNav.AutoSize = true;
            lblTituloNav.TextAlign = ContentAlignment.MiddleLeft;
            lblTituloNav.Dock = DockStyle.Fill;
            lblTituloNav.Anchor = AnchorStyles.Left | AnchorStyles.Top | AnchorStyles.Bottom;

            // Panel de botones a la derecha
            Panel panelBotonesContainer = new Panel();
            panelBotonesContainer.Dock = DockStyle.Fill;
            panelBotonesContainer.BackColor = BLANCO;

            FlowLayoutPanel panelBotonesNav = new FlowLayoutPanel();
            panelBotonesNav.Anchor = AnchorStyles.Right | AnchorStyles.Top | AnchorStyles.Bottom;
            panelBotonesNav.BackColor = BLANCO;
            panelBotonesNav.FlowDirection = FlowDirection.LeftToRight;
            panelBotonesNav.AutoSize = true;
            panelBotonesNav.Location = new Point(0, 0);

            // Ajustar posición cuando cambie el tamaño
            panelBotonesContainer.Resize += (s, e) =>
            {
                int anchoTotal = 0;
                foreach (Control ctrl in panelBotonesNav.Controls)
                {
                    anchoTotal += ctrl.Width + ctrl.Margin.Left + ctrl.Margin.Right;
                }
                panelBotonesNav.Location = new Point(panelBotonesContainer.Width - anchoTotal, 0);
            };

            panelBotonesNav.Controls.Add(btnTemperatura);
            panelBotonesNav.Controls.Add(btnLongitud);
            panelBotonesNav.Controls.Add(btnPeso);

            panelBotonesContainer.Controls.Add(panelBotonesNav);

            // Ajustar posición inicial después de agregar los botones
            panelBotonesContainer.Layout += (s, e) =>
            {
                int anchoTotal = 0;
                foreach (Control ctrl in panelBotonesNav.Controls)
                {
                    anchoTotal += ctrl.Width + ctrl.Margin.Left + ctrl.Margin.Right;
                }
                panelBotonesNav.Location = new Point(panelBotonesContainer.Width - anchoTotal, 0);
            };

            tableLayoutNav.Controls.Add(lblTituloNav, 0, 0);
            tableLayoutNav.Controls.Add(panelBotonesContainer, 1, 0);

            panelNavegacion.Controls.Add(tableLayoutNav);
        }

        private void ConfigurarFooter()
        {
            Label lblFooter = new Label();
            lblFooter.Text = "Monsters Inc. Edición Monstruosa v1.0 - © 2025";
            lblFooter.Font = new Font("Arial", 11F);
            lblFooter.ForeColor = BLANCO;
            lblFooter.TextAlign = ContentAlignment.MiddleCenter;
            lblFooter.Dock = DockStyle.Fill;

            Button btnAyuda = new Button();
            btnAyuda.Text = "¿Tienes alguna duda?";
            btnAyuda.Font = new Font("Arial", 10F, FontStyle.Bold);
            btnAyuda.ForeColor = BLANCO;
            btnAyuda.BackColor = Color.Transparent;
            btnAyuda.FlatStyle = FlatStyle.Flat;
            btnAyuda.FlatAppearance.BorderSize = 0;
            btnAyuda.Cursor = Cursors.Hand;
            btnAyuda.Dock = DockStyle.Right;
            btnAyuda.Click += (s, e) => MostrarMensajeAyuda();

            panelFooter.Controls.Add(lblFooter);
            panelFooter.Controls.Add(btnAyuda);
        }

        private void ConfigurarEventos()
        {
            btnTemperatura.Click += (s, e) => MostrarPanelTemperatura();
            btnLongitud.Click += (s, e) => MostrarPanelLongitud();
            btnPeso.Click += (s, e) => MostrarPanelPeso();
        }

        private void MostrarPanelTemperatura()
        {
            CambiarPanel(panelTemperatura);
            ActualizarBotonActivo(btnTemperatura);
        }

        private void MostrarPanelLongitud()
        {
            CambiarPanel(panelLongitud);
            ActualizarBotonActivo(btnLongitud);
        }

        private void MostrarPanelPeso()
        {
            CambiarPanel(panelPeso);
            ActualizarBotonActivo(btnPeso);
        }



        private void CambiarPanel(Panel nuevoPanel)
        {
            panelContenido.Controls.Clear();
            nuevoPanel.Dock = DockStyle.Fill;
            panelContenido.Controls.Add(nuevoPanel);
            panelContenido.Refresh();
            this.Refresh();
        }

        private void ActualizarBotonActivo(Button botonActivo)
        {
            Color[] colores = { AZUL_PRINCIPAL, PURPURA_MONSTER, ROJO_MONSTER };
            Button[] botones = { btnTemperatura, btnLongitud, btnPeso };

            for (int i = 0; i < botones.Length; i++)
            {
                botones[i].BackColor = colores[i];
                botones[i].ForeColor = BLANCO;
            }

            botonActivo.BackColor = GRIS_OSCURO;
            botonActivo.ForeColor = BLANCO;
        }

        private void CerrarSesion()
        {
            if (MessageBox.Show(
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Question) == DialogResult.Yes)
            {
                this.Hide();
                VentanaLogin ventanaLogin = new VentanaLogin();
                ventanaLogin.Show();
            }
        }

        private void MostrarMensajeAyuda()
        {
            string mensaje = "¡Bienvenido al Sistema de Conversiones de Monsters Inc.!\n\n" +
                           "Esta aplicación te permite realizar conversiones entre diferentes unidades de medida:\n\n" +
                           "• TEMPERATURA: Celsius, Fahrenheit, Kelvin\n" +
                           "• LONGITUD: Metros, Pies, Pulgadas, Yardas\n" +
                           "• PESO: Kilogramos, Libras, Onzas\n\n" +
                           "Simplemente selecciona el tipo de conversión, ingresa el valor y elige las unidades " +
                           "para obtener el resultado al instante.";

            MessageBox.Show(mensaje, "Acerca del Sistema", MessageBoxButtons.OK, MessageBoxIcon.Information);
        }
    }
}

