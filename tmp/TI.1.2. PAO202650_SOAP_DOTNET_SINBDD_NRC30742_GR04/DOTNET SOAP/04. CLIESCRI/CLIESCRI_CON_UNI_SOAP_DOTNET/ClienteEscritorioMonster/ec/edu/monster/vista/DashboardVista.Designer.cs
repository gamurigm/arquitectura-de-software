namespace ClienteEscritorioMonster.ec.edu.monster.vista
{
    partial class DashboardVista
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.lblResultadoCalculoMasaSistema = new System.Windows.Forms.Label();
            this.btnEjecutarConversionMasaWCF = new System.Windows.Forms.Button();
            this.txtValorNumericoIngresadoMasa = new System.Windows.Forms.TextBox();
            this.cmbListaOperacionesMasaSeleccionada = new System.Windows.Forms.ComboBox();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.lblResultadoCalculoTempSistema = new System.Windows.Forms.Label();
            this.btnEjecutarConversionTempWCF = new System.Windows.Forms.Button();
            this.txtValorNumericoIngresadoTemp = new System.Windows.Forms.TextBox();
            this.cmbListaOperacionesTempSeleccionada = new System.Windows.Forms.ComboBox();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.tabPage3 = new System.Windows.Forms.TabPage();
            this.lblResultadoCalculoLongSistema = new System.Windows.Forms.Label();
            this.btnEjecutarConversionLongWCF = new System.Windows.Forms.Button();
            this.txtValorNumericoIngresadoLong = new System.Windows.Forms.TextBox();
            this.cmbListaOperacionesLongSeleccionada = new System.Windows.Forms.ComboBox();
            this.label6 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.tabPage3.SuspendLayout();
            this.SuspendLayout();
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Controls.Add(this.tabPage3);
            this.tabControl1.Location = new System.Drawing.Point(13, 13);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(775, 425);
            this.tabControl1.TabIndex = 0;
            // 
            // tabPage1
            // 
            this.tabPage1.BackColor = System.Drawing.Color.DeepSkyBlue;
            this.tabPage1.Controls.Add(this.label2);
            this.tabPage1.Controls.Add(this.label1);
            this.tabPage1.Controls.Add(this.lblResultadoCalculoMasaSistema);
            this.tabPage1.Controls.Add(this.btnEjecutarConversionMasaWCF);
            this.tabPage1.Controls.Add(this.txtValorNumericoIngresadoMasa);
            this.tabPage1.Controls.Add(this.cmbListaOperacionesMasaSeleccionada);
            this.tabPage1.Location = new System.Drawing.Point(4, 25);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(767, 396);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Masa";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(497, 32);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(165, 16);
            this.label2.TabIndex = 6;
            this.label2.Text = "Escribe el valor a convertir";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(29, 32);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(148, 16);
            this.label1.TabIndex = 5;
            this.label1.Text = "Escoge una conversión";
            // 
            // lblResultadoCalculoMasaSistema
            // 
            this.lblResultadoCalculoMasaSistema.AutoSize = true;
            this.lblResultadoCalculoMasaSistema.Location = new System.Drawing.Point(333, 167);
            this.lblResultadoCalculoMasaSistema.Name = "lblResultadoCalculoMasaSistema";
            this.lblResultadoCalculoMasaSistema.Size = new System.Drawing.Size(69, 16);
            this.lblResultadoCalculoMasaSistema.TabIndex = 4;
            this.lblResultadoCalculoMasaSistema.Text = "Resultado";
            // 
            // btnEjecutarConversionMasaWCF
            // 
            this.btnEjecutarConversionMasaWCF.BackColor = System.Drawing.Color.SeaGreen;
            this.btnEjecutarConversionMasaWCF.Location = new System.Drawing.Point(324, 114);
            this.btnEjecutarConversionMasaWCF.Name = "btnEjecutarConversionMasaWCF";
            this.btnEjecutarConversionMasaWCF.Size = new System.Drawing.Size(111, 23);
            this.btnEjecutarConversionMasaWCF.TabIndex = 2;
            this.btnEjecutarConversionMasaWCF.Text = "CALCULAR";
            this.btnEjecutarConversionMasaWCF.UseVisualStyleBackColor = false;
            this.btnEjecutarConversionMasaWCF.Click += new System.EventHandler(this.btnEjecutarConversionMasaWCF_Click);
            // 
            // txtValorNumericoIngresadoMasa
            // 
            this.txtValorNumericoIngresadoMasa.Location = new System.Drawing.Point(500, 53);
            this.txtValorNumericoIngresadoMasa.Name = "txtValorNumericoIngresadoMasa";
            this.txtValorNumericoIngresadoMasa.Size = new System.Drawing.Size(186, 22);
            this.txtValorNumericoIngresadoMasa.TabIndex = 1;
            // 
            // cmbListaOperacionesMasaSeleccionada
            // 
            this.cmbListaOperacionesMasaSeleccionada.FormattingEnabled = true;
            this.cmbListaOperacionesMasaSeleccionada.Location = new System.Drawing.Point(32, 51);
            this.cmbListaOperacionesMasaSeleccionada.Name = "cmbListaOperacionesMasaSeleccionada";
            this.cmbListaOperacionesMasaSeleccionada.Size = new System.Drawing.Size(428, 24);
            this.cmbListaOperacionesMasaSeleccionada.TabIndex = 0;
            this.cmbListaOperacionesMasaSeleccionada.SelectedIndexChanged += new System.EventHandler(this.cmbListaOperacionesMasaSeleccionada_SelectedIndexChanged);
            // 
            // tabPage2
            // 
            this.tabPage2.BackColor = System.Drawing.Color.DeepSkyBlue;
            this.tabPage2.Controls.Add(this.lblResultadoCalculoTempSistema);
            this.tabPage2.Controls.Add(this.btnEjecutarConversionTempWCF);
            this.tabPage2.Controls.Add(this.txtValorNumericoIngresadoTemp);
            this.tabPage2.Controls.Add(this.cmbListaOperacionesTempSeleccionada);
            this.tabPage2.Controls.Add(this.label4);
            this.tabPage2.Controls.Add(this.label3);
            this.tabPage2.Location = new System.Drawing.Point(4, 25);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(767, 396);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Temperatura";
            this.tabPage2.Click += new System.EventHandler(this.tabPage2_Click);
            // 
            // lblResultadoCalculoTempSistema
            // 
            this.lblResultadoCalculoTempSistema.AutoSize = true;
            this.lblResultadoCalculoTempSistema.Location = new System.Drawing.Point(329, 175);
            this.lblResultadoCalculoTempSistema.Name = "lblResultadoCalculoTempSistema";
            this.lblResultadoCalculoTempSistema.Size = new System.Drawing.Size(73, 16);
            this.lblResultadoCalculoTempSistema.TabIndex = 5;
            this.lblResultadoCalculoTempSistema.Text = "Respuesta";
            // 
            // btnEjecutarConversionTempWCF
            // 
            this.btnEjecutarConversionTempWCF.BackColor = System.Drawing.Color.Green;
            this.btnEjecutarConversionTempWCF.Location = new System.Drawing.Point(320, 125);
            this.btnEjecutarConversionTempWCF.Name = "btnEjecutarConversionTempWCF";
            this.btnEjecutarConversionTempWCF.Size = new System.Drawing.Size(111, 26);
            this.btnEjecutarConversionTempWCF.TabIndex = 4;
            this.btnEjecutarConversionTempWCF.Text = "CALCULAR";
            this.btnEjecutarConversionTempWCF.UseVisualStyleBackColor = false;
            this.btnEjecutarConversionTempWCF.Click += new System.EventHandler(this.btnEjecutarConversionTempWCF_Click);
            // 
            // txtValorNumericoIngresadoTemp
            // 
            this.txtValorNumericoIngresadoTemp.Location = new System.Drawing.Point(500, 62);
            this.txtValorNumericoIngresadoTemp.Name = "txtValorNumericoIngresadoTemp";
            this.txtValorNumericoIngresadoTemp.Size = new System.Drawing.Size(187, 22);
            this.txtValorNumericoIngresadoTemp.TabIndex = 3;
            // 
            // cmbListaOperacionesTempSeleccionada
            // 
            this.cmbListaOperacionesTempSeleccionada.FormattingEnabled = true;
            this.cmbListaOperacionesTempSeleccionada.Location = new System.Drawing.Point(43, 61);
            this.cmbListaOperacionesTempSeleccionada.Name = "cmbListaOperacionesTempSeleccionada";
            this.cmbListaOperacionesTempSeleccionada.Size = new System.Drawing.Size(422, 24);
            this.cmbListaOperacionesTempSeleccionada.TabIndex = 2;
            this.cmbListaOperacionesTempSeleccionada.SelectedIndexChanged += new System.EventHandler(this.cmbListaOperacionesTempSeleccionada_SelectedIndexChanged);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(497, 42);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(165, 16);
            this.label4.TabIndex = 1;
            this.label4.Text = "Escribe el valor a convertir";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(40, 42);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(148, 16);
            this.label3.TabIndex = 0;
            this.label3.Text = "Escoge una conversión";
            // 
            // tabPage3
            // 
            this.tabPage3.BackColor = System.Drawing.Color.DeepSkyBlue;
            this.tabPage3.Controls.Add(this.lblResultadoCalculoLongSistema);
            this.tabPage3.Controls.Add(this.btnEjecutarConversionLongWCF);
            this.tabPage3.Controls.Add(this.txtValorNumericoIngresadoLong);
            this.tabPage3.Controls.Add(this.cmbListaOperacionesLongSeleccionada);
            this.tabPage3.Controls.Add(this.label6);
            this.tabPage3.Controls.Add(this.label5);
            this.tabPage3.Location = new System.Drawing.Point(4, 25);
            this.tabPage3.Name = "tabPage3";
            this.tabPage3.Size = new System.Drawing.Size(767, 396);
            this.tabPage3.TabIndex = 2;
            this.tabPage3.Text = "Longitud";
            // 
            // lblResultadoCalculoLongSistema
            // 
            this.lblResultadoCalculoLongSistema.AutoSize = true;
            this.lblResultadoCalculoLongSistema.Location = new System.Drawing.Point(345, 167);
            this.lblResultadoCalculoLongSistema.Name = "lblResultadoCalculoLongSistema";
            this.lblResultadoCalculoLongSistema.Size = new System.Drawing.Size(69, 16);
            this.lblResultadoCalculoLongSistema.TabIndex = 5;
            this.lblResultadoCalculoLongSistema.Text = "Resultado";
            // 
            // btnEjecutarConversionLongWCF
            // 
            this.btnEjecutarConversionLongWCF.BackColor = System.Drawing.Color.Green;
            this.btnEjecutarConversionLongWCF.Location = new System.Drawing.Point(339, 117);
            this.btnEjecutarConversionLongWCF.Name = "btnEjecutarConversionLongWCF";
            this.btnEjecutarConversionLongWCF.Size = new System.Drawing.Size(113, 29);
            this.btnEjecutarConversionLongWCF.TabIndex = 4;
            this.btnEjecutarConversionLongWCF.Text = "CALCULAR";
            this.btnEjecutarConversionLongWCF.UseVisualStyleBackColor = false;
            this.btnEjecutarConversionLongWCF.Click += new System.EventHandler(this.btnEjecutarConversionLongWCF_Click);
            // 
            // txtValorNumericoIngresadoLong
            // 
            this.txtValorNumericoIngresadoLong.Location = new System.Drawing.Point(508, 60);
            this.txtValorNumericoIngresadoLong.Name = "txtValorNumericoIngresadoLong";
            this.txtValorNumericoIngresadoLong.Size = new System.Drawing.Size(183, 22);
            this.txtValorNumericoIngresadoLong.TabIndex = 3;
            // 
            // cmbListaOperacionesLongSeleccionada
            // 
            this.cmbListaOperacionesLongSeleccionada.FormattingEnabled = true;
            this.cmbListaOperacionesLongSeleccionada.Location = new System.Drawing.Point(44, 58);
            this.cmbListaOperacionesLongSeleccionada.Name = "cmbListaOperacionesLongSeleccionada";
            this.cmbListaOperacionesLongSeleccionada.Size = new System.Drawing.Size(408, 24);
            this.cmbListaOperacionesLongSeleccionada.TabIndex = 2;
            this.cmbListaOperacionesLongSeleccionada.SelectedIndexChanged += new System.EventHandler(this.cmbListaOperacionesLongSeleccionada_SelectedIndexChanged);
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(505, 39);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(165, 16);
            this.label6.TabIndex = 1;
            this.label6.Text = "Escribe el valor a convertir";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(41, 39);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(148, 16);
            this.label5.TabIndex = 0;
            this.label5.Text = "Escoge una conversión";
            // 
            // DashboardVista
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Controls.Add(this.tabControl1);
            this.Name = "DashboardVista";
            this.Text = "DashboardVista";
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage1.PerformLayout();
            this.tabPage2.ResumeLayout(false);
            this.tabPage2.PerformLayout();
            this.tabPage3.ResumeLayout(false);
            this.tabPage3.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.TextBox txtValorNumericoIngresadoMasa;
        private System.Windows.Forms.ComboBox cmbListaOperacionesMasaSeleccionada;
        private System.Windows.Forms.Button btnEjecutarConversionMasaWCF;
        private System.Windows.Forms.Label lblResultadoCalculoMasaSistema;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TabPage tabPage3;
        private System.Windows.Forms.Label lblResultadoCalculoTempSistema;
        private System.Windows.Forms.Button btnEjecutarConversionTempWCF;
        private System.Windows.Forms.TextBox txtValorNumericoIngresadoTemp;
        private System.Windows.Forms.ComboBox cmbListaOperacionesTempSeleccionada;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblResultadoCalculoLongSistema;
        private System.Windows.Forms.Button btnEjecutarConversionLongWCF;
        private System.Windows.Forms.TextBox txtValorNumericoIngresadoLong;
        private System.Windows.Forms.ComboBox cmbListaOperacionesLongSeleccionada;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label5;
    }
}