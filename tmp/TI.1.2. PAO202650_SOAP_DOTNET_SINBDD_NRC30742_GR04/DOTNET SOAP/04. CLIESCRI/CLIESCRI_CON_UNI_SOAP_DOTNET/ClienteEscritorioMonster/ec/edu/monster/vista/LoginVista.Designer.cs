namespace ClienteEscritorioMonster.ec.edu.monster.vista
{
    partial class LoginVista
    {
        /// <summary>
        /// Variable del diseñador necesaria.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Limpiar los recursos que se estén usando.
        /// </summary>
        /// <param name="disposing">true si los recursos administrados se deben desechar; false en caso contrario.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Código generado por el Diseñador de Windows Forms

        /// <summary>
        /// Método necesario para admitir el Diseñador. No se puede modificar
        /// el contenido de este método con el editor de código.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(LoginVista));
            this.pbxFotografiaPerfilUsuario = new System.Windows.Forms.PictureBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.txtNombreUsuarioInstitucionalIngresado = new System.Windows.Forms.TextBox();
            this.txtClaveAccesoSeguridadIngresada = new System.Windows.Forms.TextBox();
            this.btnValidarCredencialesAccesoWCF = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.pbxFotografiaPerfilUsuario)).BeginInit();
            this.SuspendLayout();
            // 
            // pbxFotografiaPerfilUsuario
            // 
            this.pbxFotografiaPerfilUsuario.Image = ((System.Drawing.Image)(resources.GetObject("pbxFotografiaPerfilUsuario.Image")));
            this.pbxFotografiaPerfilUsuario.Location = new System.Drawing.Point(266, 34);
            this.pbxFotografiaPerfilUsuario.Name = "pbxFotografiaPerfilUsuario";
            this.pbxFotografiaPerfilUsuario.Size = new System.Drawing.Size(264, 163);
            this.pbxFotografiaPerfilUsuario.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pbxFotografiaPerfilUsuario.TabIndex = 0;
            this.pbxFotografiaPerfilUsuario.TabStop = false;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(190, 238);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(57, 16);
            this.label1.TabIndex = 1;
            this.label1.Text = "Usuario:";
            this.label1.Click += new System.EventHandler(this.label1_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(168, 271);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(79, 16);
            this.label2.TabIndex = 2;
            this.label2.Text = "Contraseña:";
            // 
            // txtNombreUsuarioInstitucionalIngresado
            // 
            this.txtNombreUsuarioInstitucionalIngresado.Location = new System.Drawing.Point(250, 238);
            this.txtNombreUsuarioInstitucionalIngresado.Name = "txtNombreUsuarioInstitucionalIngresado";
            this.txtNombreUsuarioInstitucionalIngresado.Size = new System.Drawing.Size(340, 22);
            this.txtNombreUsuarioInstitucionalIngresado.TabIndex = 3;
            // 
            // txtClaveAccesoSeguridadIngresada
            // 
            this.txtClaveAccesoSeguridadIngresada.Location = new System.Drawing.Point(250, 271);
            this.txtClaveAccesoSeguridadIngresada.Name = "txtClaveAccesoSeguridadIngresada";
            this.txtClaveAccesoSeguridadIngresada.PasswordChar = '*';
            this.txtClaveAccesoSeguridadIngresada.Size = new System.Drawing.Size(340, 22);
            this.txtClaveAccesoSeguridadIngresada.TabIndex = 4;
            // 
            // btnValidarCredencialesAccesoWCF
            // 
            this.btnValidarCredencialesAccesoWCF.BackColor = System.Drawing.Color.DarkGreen;
            this.btnValidarCredencialesAccesoWCF.Location = new System.Drawing.Point(360, 349);
            this.btnValidarCredencialesAccesoWCF.Name = "btnValidarCredencialesAccesoWCF";
            this.btnValidarCredencialesAccesoWCF.Size = new System.Drawing.Size(86, 23);
            this.btnValidarCredencialesAccesoWCF.TabIndex = 5;
            this.btnValidarCredencialesAccesoWCF.Text = "INGRESAR";
            this.btnValidarCredencialesAccesoWCF.UseVisualStyleBackColor = false;
            this.btnValidarCredencialesAccesoWCF.Click += new System.EventHandler(this.btnValidarCredencialesAccesoWCF_Click);
            // 
            // LoginVista
            // 
            this.BackColor = System.Drawing.Color.Blue;
            this.ClientSize = new System.Drawing.Size(812, 498);
            this.Controls.Add(this.btnValidarCredencialesAccesoWCF);
            this.Controls.Add(this.txtClaveAccesoSeguridadIngresada);
            this.Controls.Add(this.txtNombreUsuarioInstitucionalIngresado);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.pbxFotografiaPerfilUsuario);
            this.ForeColor = System.Drawing.SystemColors.ControlDark;
            this.Name = "LoginVista";
            this.Load += new System.EventHandler(this.btnValidarCredencialesAccesoWCF_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pbxFotografiaPerfilUsuario)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.PictureBox pbxFotografiaPerfilUsuario;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox txtNombreUsuarioInstitucionalIngresado;
        private System.Windows.Forms.TextBox txtClaveAccesoSeguridadIngresada;
        private System.Windows.Forms.Button btnValidarCredencialesAccesoWCF;
    }
}

