using System;
using System.Drawing;
using System.Net.Http;
using System.Text;
using System.Windows.Forms;
using CONUNI_SOAP_DOTNET_CLIESC_G09.servicio;

namespace CONUNI_SOAP_DOTNET_CLIESC_G09.vista
{
    /// <summary>
    /// Ventana de login profesional para Monsters Inc. Converter
    /// Diseño empresarial con colores oficiales de Monsters Inc.
    /// </summary>
    public class VentanaLogin : Form
    {
        private static readonly Color AZUL_PRINCIPAL = Color.FromArgb(33, 169, 218);
        private static readonly Color ROJO_MONSTER = Color.FromArgb(246, 105, 113);
        private static readonly Color AMARILLO_MONSTER = Color.FromArgb(245, 216, 128);
        private static readonly Color PURPURA_MONSTER = Color.FromArgb(140, 107, 205);
        private static readonly Color AZUL_CLARO = Color.FromArgb(159, 220, 250);
        private static readonly Color BLANCO = Color.White;
        private static readonly Color GRIS_OSCURO = Color.FromArgb(60, 60, 60);
        private static readonly Color GRIS_CLARO = Color.FromArgb(240, 240, 240);
        private static readonly Color AZUL_MEDIO = Color.FromArgb(33, 169, 218); // Para el panel izquierdo

        private const int MAX_INTENTOS = 3;

        private Panel panelPrincipal;
        private Panel panelIzquierdo;
        private Panel panelDerecho;

        // Panel izquierdo - elementos
        private PictureBox pictureBoxLogo;
        private Label lblMonstersInc;
        private Label lblSistemaConversiones;
        private Label lblEnterprise;

        // Panel derecho - formulario
        private Label lblTituloLogin;
        private Label lblUsuario;
        private Label lblContrasena;
        private TextBox txtUsuario;
        private TextBox txtContrasena;
        private Button btnIngresar;
        private Button btnSalir;
        private Label lblMensaje;
        private Label lblIntento;

        private int intentos = 0;
        private bool loginExitoso = false;

        public VentanaLogin()
        {
            InicializarComponentes();
            ConfigurarVentana();
            ConfigurarLayout();
            ConfigurarEventos();
        }

        private void InicializarComponentes()
        {
            panelPrincipal = new Panel();
            panelPrincipal.BackColor = BLANCO;
            panelPrincipal.Dock = DockStyle.Fill;

            // Panel izquierdo - Azul medio
            panelIzquierdo = new Panel();
            panelIzquierdo.BackColor = AZUL_MEDIO;
            panelIzquierdo.Width = 500; // Aprox 40% del ancho

            // PictureBox para el logo
            pictureBoxLogo = new PictureBox();
            pictureBoxLogo.Size = new Size(200, 200);
            pictureBoxLogo.SizeMode = PictureBoxSizeMode.Zoom;
            pictureBoxLogo.BackColor = Color.Transparent;
            pictureBoxLogo.Visible = true;
            
            // Cargar la imagen - intentar múltiples rutas
            bool imagenCargada = false;
            string[] rutas = {
                System.IO.Path.Combine(Application.StartupPath, "logo.png"), // Directo en bin/Debug/
                System.IO.Path.Combine(Application.StartupPath, "Resources", "logo.png"), // bin/Debug/Resources/
                System.IO.Path.Combine(System.IO.Directory.GetParent(Application.StartupPath).FullName, "Resources", "logo.png"), // Proyecto/Resources/
                System.IO.Path.Combine(System.IO.Directory.GetParent(Application.StartupPath).Parent.FullName, "Resources", "logo.png"), // Una carpeta arriba
                System.IO.Path.Combine(Application.StartupPath, "..", "..", "Resources", "logo.png") // Relativa
            };
            
            foreach (string ruta in rutas)
            {
                try
                {
                    string rutaCompleta = System.IO.Path.GetFullPath(ruta);
                    if (System.IO.File.Exists(rutaCompleta))
                    {
                        pictureBoxLogo.Image = Image.FromFile(rutaCompleta);
                        imagenCargada = true;
                        break;
                    }
                }
                catch { }
            }
            
            // Si no se cargó la imagen, mostrar texto temporal
            if (!imagenCargada)
            {
                Label lblLogoIcono = new Label();
                lblLogoIcono.Text = "LOGO";
                lblLogoIcono.Font = new Font("Arial", 24F, FontStyle.Bold);
                lblLogoIcono.ForeColor = AZUL_CLARO;
                lblLogoIcono.TextAlign = ContentAlignment.MiddleCenter;
                lblLogoIcono.Dock = DockStyle.Fill;
                lblLogoIcono.BackColor = Color.Transparent;
                pictureBoxLogo.Controls.Add(lblLogoIcono);
            }

            lblMonstersInc = new Label();
            lblMonstersInc.Text = "MONSTERS INC.";
            lblMonstersInc.Font = new Font("Arial", 30F, FontStyle.Bold);
            lblMonstersInc.ForeColor = BLANCO;
            lblMonstersInc.TextAlign = ContentAlignment.MiddleCenter;
            lblMonstersInc.AutoSize = false;
            lblMonstersInc.Size = new Size(400, 50);

            lblSistemaConversiones = new Label();
            lblSistemaConversiones.Text = "Sistema de Conversiones";
            lblSistemaConversiones.Font = new Font("Arial", 16F, FontStyle.Regular);
            lblSistemaConversiones.ForeColor = BLANCO;
            lblSistemaConversiones.TextAlign = ContentAlignment.MiddleCenter;
            lblSistemaConversiones.AutoSize = false;
            lblSistemaConversiones.Size = new Size(400, 30);

            lblEnterprise = new Label();
            lblEnterprise.Text = "Edición Monstruosa v1.0";
            lblEnterprise.Font = new Font("Arial", 12F, FontStyle.Italic);
            lblEnterprise.ForeColor = Color.FromArgb(200, 200, 200); // Gris claro
            lblEnterprise.TextAlign = ContentAlignment.MiddleCenter;
            lblEnterprise.AutoSize = false;
            lblEnterprise.Size = new Size(400, 25);

            // Panel derecho - Gris claro
            panelDerecho = new Panel();
            panelDerecho.BackColor = GRIS_CLARO;
            panelDerecho.Dock = DockStyle.Fill;

            // Formulario de login
            lblTituloLogin = new Label();
            lblTituloLogin.Text = "LOGUÉATE";
            lblTituloLogin.Font = new Font("Arial", 20F, FontStyle.Bold);
            lblTituloLogin.ForeColor = AZUL_PRINCIPAL;
            lblTituloLogin.TextAlign = ContentAlignment.MiddleCenter;
            lblTituloLogin.AutoSize = false;
            lblTituloLogin.Size = new Size(400, 50);

            // Ancho fijo para los labels para alinear los TextBoxes
            const int ANCHO_LABEL = 120; // Ancho suficiente para "Contraseña:"
            
            lblUsuario = new Label();
            lblUsuario.Text = "Usuario:";
            lblUsuario.Font = new Font("Arial", 10F, FontStyle.Bold);
            lblUsuario.ForeColor = GRIS_OSCURO;
            lblUsuario.AutoSize = false;
            lblUsuario.Size = new Size(ANCHO_LABEL, 40);
            lblUsuario.TextAlign = ContentAlignment.MiddleLeft;

            txtUsuario = new TextBox();
            txtUsuario.Font = new Font("Arial", 14F);
            txtUsuario.BorderStyle = BorderStyle.FixedSingle;
            txtUsuario.Size = new Size(280, 40);
            txtUsuario.BackColor = BLANCO;

            lblContrasena = new Label();
            lblContrasena.Text = "Contraseña:";
            lblContrasena.Font = new Font("Arial", 10F, FontStyle.Bold);
            lblContrasena.ForeColor = GRIS_OSCURO;
            lblContrasena.AutoSize = false;
            lblContrasena.Size = new Size(ANCHO_LABEL, 40);
            lblContrasena.TextAlign = ContentAlignment.MiddleLeft;

            txtContrasena = new TextBox();
            txtContrasena.Font = new Font("Arial", 14F);
            txtContrasena.BorderStyle = BorderStyle.FixedSingle;
            txtContrasena.Size = new Size(280, 40);
            txtContrasena.BackColor = BLANCO;
            txtContrasena.UseSystemPasswordChar = true;

            btnIngresar = new Button();
            btnIngresar.Text = "ACCEDER";
            btnIngresar.BackColor = AZUL_PRINCIPAL;
            btnIngresar.ForeColor = BLANCO;
            btnIngresar.Font = new Font("Arial", 10F, FontStyle.Bold);
            btnIngresar.Size = new Size(170, 50);
            btnIngresar.FlatStyle = FlatStyle.Flat;
            btnIngresar.Cursor = Cursors.Hand;

            btnSalir = new Button();
            btnSalir.Text = "SALIR";
            btnSalir.BackColor = ROJO_MONSTER;
            btnSalir.ForeColor = BLANCO;
            btnSalir.Font = new Font("Arial", 10F, FontStyle.Bold);
            btnSalir.Size = new Size(170, 50);
            btnSalir.FlatStyle = FlatStyle.Flat;
            btnSalir.Cursor = Cursors.Hand;

            lblMensaje = new Label();
            lblMensaje.Text = "Ingrese sus credenciales para acceder";
            lblMensaje.Font = new Font("Arial", 12F);
            lblMensaje.ForeColor = GRIS_OSCURO;
            lblMensaje.TextAlign = ContentAlignment.MiddleCenter;
            lblMensaje.AutoSize = false;
            lblMensaje.Size = new Size(400, 25);

            lblIntento = new Label();
            lblIntento.Text = "";
            lblIntento.Font = new Font("Arial", 11F, FontStyle.Bold);
            lblIntento.TextAlign = ContentAlignment.MiddleCenter;
            lblIntento.AutoSize = false;
            lblIntento.Size = new Size(400, 25);
        }

        private void ConfigurarVentana()
        {
            this.Text = "Monsters Inc. Conversiones";
            this.Size = new Size(1200, 700);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.BackColor = BLANCO;
            this.FormBorderStyle = FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
        }

        private void ConfigurarLayout()
        {
            // Panel principal
            this.Controls.Add(panelPrincipal);
            
            // Panel izquierdo - Azul medio
            panelIzquierdo.Dock = DockStyle.Left;
            panelIzquierdo.Width = 500;
            
            // Layout del panel izquierdo
            TableLayoutPanel layoutIzquierdo = new TableLayoutPanel();
            layoutIzquierdo.Dock = DockStyle.Fill;
            layoutIzquierdo.RowCount = 4;
            layoutIzquierdo.ColumnCount = 1;
            layoutIzquierdo.Padding = new Padding(50, 100, 50, 50);
            
            // Logo/Imagen centrado
            Panel panelLogoWrapper = new Panel();
            panelLogoWrapper.AutoSize = false;
            panelLogoWrapper.Height = 220;
            panelLogoWrapper.Dock = DockStyle.Fill;
            panelLogoWrapper.BackColor = AZUL_MEDIO;
            
            pictureBoxLogo.Size = new Size(200, 200);
            pictureBoxLogo.Anchor = AnchorStyles.None;
            pictureBoxLogo.Visible = true;
            panelLogoWrapper.Controls.Add(pictureBoxLogo);
            
            // Centrar el logo horizontal y verticalmente en el panel
            panelLogoWrapper.Resize += (s, e) => {
                if (panelLogoWrapper.Width > 0 && panelLogoWrapper.Height > 0)
                {
                    int centerX = (panelLogoWrapper.Width - pictureBoxLogo.Width) / 2;
                    int centerY = (panelLogoWrapper.Height - pictureBoxLogo.Height) / 2;
                    pictureBoxLogo.Location = new Point(centerX, centerY);
                }
            };
            
            // Centrar inicialmente después de que se haya establecido el tamaño
            panelLogoWrapper.Layout += (s, e) => {
                if (panelLogoWrapper.Width > 0 && panelLogoWrapper.Height > 0)
                {
                    int centerX = (panelLogoWrapper.Width - pictureBoxLogo.Width) / 2;
                    int centerY = (panelLogoWrapper.Height - pictureBoxLogo.Height) / 2;
                    pictureBoxLogo.Location = new Point(centerX, centerY);
                }
            };
            
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            layoutIzquierdo.Controls.Add(panelLogoWrapper, 0, 0);
            
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.Absolute, 60));
            
            // MONSTERS INC.
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            layoutIzquierdo.Controls.Add(lblMonstersInc, 0, 2);
            
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.Absolute, 15));
            
            // Sistema de Conversiones
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            layoutIzquierdo.Controls.Add(lblSistemaConversiones, 0, 4);
            
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.Absolute, 20));
            
            // Enterprise Edition
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            layoutIzquierdo.Controls.Add(lblEnterprise, 0, 6);
            
            layoutIzquierdo.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));
            
            panelIzquierdo.Controls.Add(layoutIzquierdo);
            panelPrincipal.Controls.Add(panelIzquierdo);

            // Panel derecho - Gris claro con formulario centrado
            panelDerecho.Dock = DockStyle.Fill;
            panelDerecho.BackColor = GRIS_CLARO;
            
            // TableLayout para posicionar el formulario más a la derecha
            TableLayoutPanel panelCentro = new TableLayoutPanel();
            panelCentro.Dock = DockStyle.Fill;
            panelCentro.RowCount = 3;
            panelCentro.ColumnCount = 3;
            panelCentro.BackColor = GRIS_CLARO;
            
            // Espacios verticales para centrar verticalmente
            panelCentro.RowStyles.Add(new RowStyle(SizeType.Percent, 50F));
            panelCentro.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            panelCentro.RowStyles.Add(new RowStyle(SizeType.Percent, 50F));
            
            // Espacios horizontales - mucho más espacio a la izquierda, poco a la derecha
            panelCentro.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 90F)); // Mucho más espacio izquierdo
            panelCentro.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));
            panelCentro.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 10F)); // Poco espacio derecho
            
            // Panel del formulario - Caja blanca que contiene todo el formulario
            Panel panelFormularioWrapper = new Panel();
            panelFormularioWrapper.BackColor = BLANCO; // Fondo blanco para la caja
            panelFormularioWrapper.AutoSize = true;
            panelFormularioWrapper.Size = new Size(450, 500);
            // Sin borde - solo el efecto visual del fondo blanco sobre gris claro
            panelFormularioWrapper.BorderStyle = BorderStyle.None;
            
            TableLayoutPanel formLayout = new TableLayoutPanel();
            formLayout.Dock = DockStyle.Fill;
            formLayout.RowCount = 8;
            formLayout.ColumnCount = 1;
            formLayout.Padding = new Padding(50);
            formLayout.AutoSize = true;
            formLayout.BackColor = BLANCO; // Fondo blanco para que coincida con el panel
            
            // LOGUÉATE
            formLayout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            formLayout.Controls.Add(lblTituloLogin, 0, 0);
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 40));
            
            // Usuario - Label y TextBox en la misma línea con TableLayoutPanel para alineación perfecta
            TableLayoutPanel panelUsuario = new TableLayoutPanel();
            panelUsuario.ColumnCount = 2;
            panelUsuario.RowCount = 1;
            panelUsuario.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 120)); // Ancho fijo para label
            panelUsuario.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize)); // TextBox
            panelUsuario.AutoSize = true;
            panelUsuario.Controls.Add(lblUsuario, 0, 0);
            panelUsuario.Controls.Add(txtUsuario, 1, 0);
            lblUsuario.Dock = DockStyle.Fill;
            txtUsuario.Margin = new Padding(10, 0, 0, 0); // Espaciado entre label y textbox
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            formLayout.Controls.Add(panelUsuario, 0, 2);
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 20));
            
            // Contraseña - Label y TextBox en la misma línea con TableLayoutPanel para alineación perfecta
            TableLayoutPanel panelContrasena = new TableLayoutPanel();
            panelContrasena.ColumnCount = 2;
            panelContrasena.RowCount = 1;
            panelContrasena.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 120)); // Mismo ancho que Usuario
            panelContrasena.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize)); // TextBox
            panelContrasena.AutoSize = true;
            panelContrasena.Controls.Add(lblContrasena, 0, 0);
            panelContrasena.Controls.Add(txtContrasena, 1, 0);
            lblContrasena.Dock = DockStyle.Fill;
            txtContrasena.Margin = new Padding(10, 0, 0, 0); // Mismo espaciado que Usuario
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            formLayout.Controls.Add(panelContrasena, 0, 4);
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 30));
            
            // Botones
            FlowLayoutPanel panelBotones = new FlowLayoutPanel();
            panelBotones.FlowDirection = FlowDirection.LeftToRight;
            panelBotones.AutoSize = true;
            panelBotones.Controls.Add(btnIngresar);
            panelBotones.Controls.Add(btnSalir);
            formLayout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            formLayout.Controls.Add(panelBotones, 0, 6);
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 20));
            
            // Mensaje
            formLayout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            formLayout.Controls.Add(lblMensaje, 0, 8);
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 10));
            
            // Intentos
            formLayout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            formLayout.Controls.Add(lblIntento, 0, 10);
            
            formLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));
            
            panelFormularioWrapper.Controls.Add(formLayout);
            
            // Colocar el formulario en el centro del TableLayoutPanel
            panelCentro.Controls.Add(panelFormularioWrapper, 1, 1);
            
            panelDerecho.Controls.Add(panelCentro);
            panelPrincipal.Controls.Add(panelDerecho);
        }

        private void ConfigurarEventos()
        {
            btnIngresar.Click += (s, e) => VerificarCredenciales();
            btnSalir.Click += (s, e) => Application.Exit();
            txtUsuario.KeyDown += (s, e) => { if (e.KeyCode == Keys.Enter) txtContrasena.Focus(); };
            txtContrasena.KeyDown += (s, e) => { if (e.KeyCode == Keys.Enter) VerificarCredenciales(); };
            btnIngresar.MouseEnter += (s, e) => btnIngresar.BackColor = PURPURA_MONSTER;
            btnIngresar.MouseLeave += (s, e) => btnIngresar.BackColor = AZUL_PRINCIPAL;
            btnSalir.MouseEnter += (s, e) => btnSalir.BackColor = Color.FromArgb(200, 50, 60);
            btnSalir.MouseLeave += (s, e) => btnSalir.BackColor = ROJO_MONSTER;
            
            // Cerrar todas las ventanas cuando se cierra el login (si no hay login exitoso)
            this.FormClosed += (s, e) =>
            {
                if (!loginExitoso)
                {
                    // Cerrar todas las formas abiertas
                    foreach (Form form in Application.OpenForms)
                    {
                        if (form != this && form is VentanaPrincipal)
                        {
                            form.Close();
                        }
                    }
                }
            };
        }

        private void VerificarCredenciales()
        {
            string usuario    = txtUsuario.Text.Trim().ToUpper();
            string contrasena = txtContrasena.Text.Trim();
            intentos++;

            bool esValido = ValidarCredencialesEnServidor(usuario, contrasena);

            if (esValido)
            {
                lblMensaje.Text = "¡Autenticación exitosa! Bienvenido al sistema.";
                lblMensaje.ForeColor = AZUL_PRINCIPAL;
                lblIntento.Text = "";
                loginExitoso = true;
                btnIngresar.BackColor = AZUL_PRINCIPAL;
                btnIngresar.Text = "ACCEDIENDO...";
                btnIngresar.Enabled = false;

                Timer timer = new Timer();
                timer.Interval = 2000;
                timer.Tick += (s, e) => { timer.Stop(); this.Hide(); AbrirVentanaPrincipal(); };
                timer.Start();
            }
            else
            {
                if (intentos >= MAX_INTENTOS)
                {
                    lblMensaje.Text = "ACCESO DENEGADO";
                    lblMensaje.ForeColor = ROJO_MONSTER;
                    lblIntento.Text = "Demasiados intentos fallidos. Sistema bloqueado.";
                    lblIntento.ForeColor = ROJO_MONSTER;
                    btnIngresar.Enabled = false;
                    btnIngresar.BackColor = GRIS_CLARO;
                    btnIngresar.Text = "BLOQUEADO";

                    Timer timer = new Timer();
                    timer.Interval = 3000;
                    timer.Tick += (s, e) => { timer.Stop(); Application.Exit(); };
                    timer.Start();
                }
                else
                {
                    lblMensaje.Text = "Credenciales incorrectas";
                    lblMensaje.ForeColor = ROJO_MONSTER;
                    lblIntento.Text = "Intento " + intentos + " de " + MAX_INTENTOS;
                    lblIntento.ForeColor = ROJO_MONSTER;
                    txtUsuario.Text = "";
                    txtContrasena.Text = "";
                    txtUsuario.Focus();
                }
            }
        }

        /// <summary>
        /// Llama al servidor SOAP para validar las credenciales.
        /// El cliente NUNCA almacena ni conoce las credenciales correctas.
        /// </summary>
        private bool ValidarCredencialesEnServidor(string usuario, string contrasena)
        {
            try
            {
                // Para SOAP, usamos el cliente WCF generado (si existe) o una llamada HTTP raw
                var clienteSOAP = new ClienteConversionSoap(System.Configuration.ConfigurationManager.AppSettings["UrlServicioSOAP"]);
                return clienteSOAP.Login(usuario, contrasena);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al conectar con el servidor: {ex.Message}",
                    "Error de conexión", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return false;
            }
        }

        private void AbrirVentanaPrincipal()
        {
            // Obtener la URL del servicio desde App.config
            string urlServicio = System.Configuration.ConfigurationManager.AppSettings["UrlServicioSOAP"];
            if (string.IsNullOrEmpty(urlServicio))
            {
                throw new Exception("La URL del servicio SOAP no está configurada en App.config");
            }
            var clienteSOAP = new ClienteConversionSoap(urlServicio);
            var ventanaPrincipal = new VentanaPrincipal(clienteSOAP);
            ventanaPrincipal.FormClosed += (s, e) => 
            {
                // Si VentanaPrincipal se cierra, cerrar también el login
                this.Close();
            };
            ventanaPrincipal.Show();
            this.Hide(); // Ocultar el login después de mostrar la ventana principal
        }

        public bool IsLoginExitoso()
        {
            return loginExitoso;
        }
    }
}

