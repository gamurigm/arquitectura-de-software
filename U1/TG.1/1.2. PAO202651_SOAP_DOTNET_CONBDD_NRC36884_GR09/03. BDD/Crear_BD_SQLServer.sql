IF DB_ID(N'EurekaBankGR09') IS NULL CREATE DATABASE [EurekaBankGR09];
GO
USE [EurekaBankGR09];
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO
-- Generado desde GR09_EurekaBank_Physical_DotNet_SQLServer.pdm.
IF OBJECT_ID(N'dbo.TipoMovimiento', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[TipoMovimiento] (
    [chr_tipocodigo] CHAR(3) NOT NULL,
    [vch_tipodescripcion] VARCHAR(40) NOT NULL,
    [vch_tipoaccion] VARCHAR(10) NOT NULL,
    [vch_tipoestado] VARCHAR(15) NOT NULL,
    CONSTRAINT [PK_TipoMovimiento] PRIMARY KEY CLUSTERED ([chr_tipocodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Sucursal', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Sucursal] (
    [chr_sucucodigo] CHAR(3) NOT NULL,
    [vch_sucunombre] VARCHAR(50) NOT NULL,
    [vch_sucuciudad] VARCHAR(30) NOT NULL,
    [vch_sucudireccion] VARCHAR(50) NULL,
    [int_sucucontcuenta] INT NOT NULL,
    CONSTRAINT [PK_Sucursal] PRIMARY KEY CLUSTERED ([chr_sucucodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Empleado', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Empleado] (
    [chr_emplcodigo] CHAR(4) NOT NULL,
    [vch_emplpaterno] VARCHAR(25) NOT NULL,
    [vch_emplmaterno] VARCHAR(25) NOT NULL,
    [vch_emplnombre] VARCHAR(30) NOT NULL,
    [vch_emplciudad] VARCHAR(30) NOT NULL,
    [vch_empldireccion] VARCHAR(50) NULL,
    CONSTRAINT [PK_Empleado] PRIMARY KEY CLUSTERED ([chr_emplcodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Modulo', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Modulo] (
    [int_moducodigo] INT NOT NULL,
    [vch_modunombre] VARCHAR(50) NULL,
    CONSTRAINT [PK_Modulo] PRIMARY KEY CLUSTERED ([int_moducodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Usuario', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Usuario] (
    [chr_emplcodigo] CHAR(4) NOT NULL,
    [vch_emplusuario] VARCHAR(20) NOT NULL,
    [vch_emplclave] VARCHAR(50) NOT NULL,
    CONSTRAINT [PK_Usuario] PRIMARY KEY CLUSTERED ([chr_emplcodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Permiso', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Permiso] (
    [chr_emplcodigo] CHAR(4) NOT NULL,
    [int_moducodigo] INT NOT NULL,
    CONSTRAINT [PK_Permiso] PRIMARY KEY CLUSTERED ([chr_emplcodigo], [int_moducodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.LOGSESSION', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[LOGSESSION] (
    [ID] INT IDENTITY(1,1) NOT NULL,
    [chr_emplcodigo] CHAR(4) NOT NULL,
    [fec_ingreso] DATETIME2 NOT NULL,
    [fec_salida] DATETIME2 NULL,
    [ip] VARCHAR(20) NOT NULL,
    [hostname] VARCHAR(100) NOT NULL,
    CONSTRAINT [PK_LOGSESSION] PRIMARY KEY CLUSTERED ([ID])
);
END;
GO
IF OBJECT_ID(N'dbo.Asignado', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Asignado] (
    [chr_asigcodigo] CHAR(6) NOT NULL,
    [chr_sucucodigo] CHAR(3) NOT NULL,
    [chr_emplcodigo] CHAR(4) NOT NULL,
    [dtt_asigfechaalta] DATE NOT NULL,
    [dtt_asigfechabaja] DATE NULL,
    CONSTRAINT [PK_Asignado] PRIMARY KEY CLUSTERED ([chr_asigcodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Cliente', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Cliente] (
    [chr_cliecodigo] CHAR(5) NOT NULL,
    [vch_cliepaterno] VARCHAR(25) NOT NULL,
    [vch_cliematerno] VARCHAR(25) NOT NULL,
    [vch_clienombre] VARCHAR(30) NOT NULL,
    [chr_cliedni] CHAR(8) NOT NULL,
    [vch_clieciudad] VARCHAR(30) NOT NULL,
    [vch_cliedireccion] VARCHAR(50) NOT NULL,
    [vch_clietelefono] VARCHAR(20) NULL,
    [vch_clieemail] VARCHAR(50) NULL,
    CONSTRAINT [PK_Cliente] PRIMARY KEY CLUSTERED ([chr_cliecodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Moneda', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Moneda] (
    [chr_monecodigo] CHAR(2) NOT NULL,
    [vch_monedescripcion] VARCHAR(20) NOT NULL,
    CONSTRAINT [PK_Moneda] PRIMARY KEY CLUSTERED ([chr_monecodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Cuenta', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Cuenta] (
    [chr_cuencodigo] CHAR(8) NOT NULL,
    [chr_monecodigo] CHAR(2) NOT NULL,
    [chr_sucucodigo] CHAR(3) NOT NULL,
    [chr_emplcreacuenta] CHAR(4) NOT NULL,
    [chr_cliecodigo] CHAR(5) NOT NULL,
    [dec_cuensaldo] DECIMAL(12,2) NOT NULL,
    [dtt_cuenfechacreacion] DATE NOT NULL,
    [vch_cuenestado] VARCHAR(15) NOT NULL,
    [int_cuencontmov] INT NOT NULL,
    [chr_cuenclave] CHAR(6) NOT NULL,
    CONSTRAINT [PK_Cuenta] PRIMARY KEY CLUSTERED ([chr_cuencodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Movimiento', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Movimiento] (
    [chr_cuencodigo] CHAR(8) NOT NULL,
    [int_movinumero] INT NOT NULL,
    [dtt_movifecha] DATE NOT NULL,
    [chr_emplcodigo] CHAR(4) NOT NULL,
    [chr_tipocodigo] CHAR(3) NOT NULL,
    [dec_moviimporte] DECIMAL(12,2) NOT NULL,
    [chr_cuenreferencia] CHAR(8) NULL,
    [id_transferencia] UNIQUEIDENTIFIER NULL,
    CONSTRAINT [PK_Movimiento] PRIMARY KEY CLUSTERED ([chr_cuencodigo], [int_movinumero])
);
END;
GO
IF OBJECT_ID(N'dbo.Parametro', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Parametro] (
    [chr_paracodigo] CHAR(3) NOT NULL,
    [vch_paradescripcion] VARCHAR(50) NOT NULL,
    [vch_paravalor] VARCHAR(70) NOT NULL,
    [vch_paraestado] VARCHAR(15) NOT NULL,
    CONSTRAINT [PK_Parametro] PRIMARY KEY CLUSTERED ([chr_paracodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.InteresMensual', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[InteresMensual] (
    [chr_monecodigo] CHAR(2) NOT NULL,
    [dec_inteimporte] DECIMAL(12,2) NOT NULL,
    CONSTRAINT [PK_InteresMensual] PRIMARY KEY CLUSTERED ([chr_monecodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.CostoMovimiento', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[CostoMovimiento] (
    [chr_monecodigo] CHAR(2) NOT NULL,
    [dec_costimporte] DECIMAL(12,2) NOT NULL,
    CONSTRAINT [PK_CostoMovimiento] PRIMARY KEY CLUSTERED ([chr_monecodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.CargoMantenimiento', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[CargoMantenimiento] (
    [chr_monecodigo] CHAR(2) NOT NULL,
    [dec_cargMontoMaximo] DECIMAL(12,2) NOT NULL,
    [dec_cargImporte] DECIMAL(12,2) NOT NULL,
    CONSTRAINT [PK_CargoMantenimiento] PRIMARY KEY CLUSTERED ([chr_monecodigo])
);
END;
GO
IF OBJECT_ID(N'dbo.Contador', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Contador] (
    [vch_conttabla] VARCHAR(30) NOT NULL,
    [int_contitem] INT NOT NULL,
    [int_contlongitud] INT NOT NULL,
    CONSTRAINT [PK_Contador] PRIMARY KEY CLUSTERED ([vch_conttabla])
);
END;
GO
IF OBJECT_ID(N'dbo.Credenciales', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Credenciales] (
    [usuario] VARCHAR(30) NOT NULL,
    [contrasena] VARCHAR(255) NOT NULL,
    [chr_cliecodigo] CHAR(5) NULL,
    CONSTRAINT [PK_Credenciales] PRIMARY KEY CLUSTERED ([usuario])
);
END;
GO
IF OBJECT_ID(N'dbo.Transferencias', N'U') IS NULL BEGIN
CREATE TABLE [dbo].[Transferencias] (
    [id_transferencia] UNIQUEIDENTIFIER NOT NULL,
    [usuario] VARCHAR(30) NOT NULL,
    [clave_operacion] UNIQUEIDENTIFIER NOT NULL,
    [chr_cliecodigo] CHAR(5) NOT NULL,
    [chr_cuencodigo_origen] CHAR(8) NOT NULL,
    [chr_cuencodigo_destino] CHAR(8) NOT NULL,
    [dec_importe] DECIMAL(12,2) NOT NULL,
    [dtt_fecha] DATETIME2 NOT NULL,
    CONSTRAINT [PK_Transferencias] PRIMARY KEY CLUSTERED ([id_transferencia])
);
END;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.Usuario') AND name=N'U_Usuario_vch_emplusuario') CREATE UNIQUE NONCLUSTERED INDEX [U_Usuario_vch_emplusuario] ON [dbo].[Usuario] ([vch_emplusuario]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.Transferencias') AND name=N'UQ_Transferencias_Usuario_Clave') CREATE UNIQUE NONCLUSTERED INDEX [UQ_Transferencias_Usuario_Clave] ON [dbo].[Transferencias] ([usuario], [clave_operacion]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Usuario_Empleado') ALTER TABLE [dbo].[Usuario] WITH CHECK ADD CONSTRAINT [FK_Usuario_Empleado] FOREIGN KEY ([chr_emplcodigo]) REFERENCES [dbo].[Empleado] ([chr_emplcodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Permiso_Modulo') ALTER TABLE [dbo].[Permiso] WITH CHECK ADD CONSTRAINT [FK_Permiso_Modulo] FOREIGN KEY ([int_moducodigo]) REFERENCES [dbo].[Modulo] ([int_moducodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Permiso_Usuario') ALTER TABLE [dbo].[Permiso] WITH CHECK ADD CONSTRAINT [FK_Permiso_Usuario] FOREIGN KEY ([chr_emplcodigo]) REFERENCES [dbo].[Usuario] ([chr_emplcodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_log_session_empleado') ALTER TABLE [dbo].[LOGSESSION] WITH CHECK ADD CONSTRAINT [fk_log_session_empleado] FOREIGN KEY ([chr_emplcodigo]) REFERENCES [dbo].[Empleado] ([chr_emplcodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_asignado_empleado') ALTER TABLE [dbo].[Asignado] WITH CHECK ADD CONSTRAINT [fk_asignado_empleado] FOREIGN KEY ([chr_emplcodigo]) REFERENCES [dbo].[Empleado] ([chr_emplcodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_asignado_sucursal') ALTER TABLE [dbo].[Asignado] WITH CHECK ADD CONSTRAINT [fk_asignado_sucursal] FOREIGN KEY ([chr_sucucodigo]) REFERENCES [dbo].[Sucursal] ([chr_sucucodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_cuenta_cliente') ALTER TABLE [dbo].[Cuenta] WITH CHECK ADD CONSTRAINT [fk_cuenta_cliente] FOREIGN KEY ([chr_cliecodigo]) REFERENCES [dbo].[Cliente] ([chr_cliecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_cuente_empleado') ALTER TABLE [dbo].[Cuenta] WITH CHECK ADD CONSTRAINT [fk_cuente_empleado] FOREIGN KEY ([chr_emplcreacuenta]) REFERENCES [dbo].[Empleado] ([chr_emplcodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_cuenta_sucursal') ALTER TABLE [dbo].[Cuenta] WITH CHECK ADD CONSTRAINT [fk_cuenta_sucursal] FOREIGN KEY ([chr_sucucodigo]) REFERENCES [dbo].[Sucursal] ([chr_sucucodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_cuenta_moneda') ALTER TABLE [dbo].[Cuenta] WITH CHECK ADD CONSTRAINT [fk_cuenta_moneda] FOREIGN KEY ([chr_monecodigo]) REFERENCES [dbo].[Moneda] ([chr_monecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_movimiento_tipomovimiento') ALTER TABLE [dbo].[Movimiento] WITH CHECK ADD CONSTRAINT [fk_movimiento_tipomovimiento] FOREIGN KEY ([chr_tipocodigo]) REFERENCES [dbo].[TipoMovimiento] ([chr_tipocodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_movimiento_empleado') ALTER TABLE [dbo].[Movimiento] WITH CHECK ADD CONSTRAINT [fk_movimiento_empleado] FOREIGN KEY ([chr_emplcodigo]) REFERENCES [dbo].[Empleado] ([chr_emplcodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_movimiento_cuenta') ALTER TABLE [dbo].[Movimiento] WITH CHECK ADD CONSTRAINT [fk_movimiento_cuenta] FOREIGN KEY ([chr_cuencodigo]) REFERENCES [dbo].[Cuenta] ([chr_cuencodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Movimiento_Transferencias') ALTER TABLE [dbo].[Movimiento] WITH CHECK ADD CONSTRAINT [FK_Movimiento_Transferencias] FOREIGN KEY ([id_transferencia]) REFERENCES [dbo].[Transferencias] ([id_transferencia]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_interesmensual_moneda') ALTER TABLE [dbo].[InteresMensual] WITH CHECK ADD CONSTRAINT [fk_interesmensual_moneda] FOREIGN KEY ([chr_monecodigo]) REFERENCES [dbo].[Moneda] ([chr_monecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_costomovimiento_moneda') ALTER TABLE [dbo].[CostoMovimiento] WITH CHECK ADD CONSTRAINT [fk_costomovimiento_moneda] FOREIGN KEY ([chr_monecodigo]) REFERENCES [dbo].[Moneda] ([chr_monecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'fk_cargomantenimiento_moneda') ALTER TABLE [dbo].[CargoMantenimiento] WITH CHECK ADD CONSTRAINT [fk_cargomantenimiento_moneda] FOREIGN KEY ([chr_monecodigo]) REFERENCES [dbo].[Moneda] ([chr_monecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Credenciales_Cliente') ALTER TABLE [dbo].[Credenciales] WITH CHECK ADD CONSTRAINT [FK_Credenciales_Cliente] FOREIGN KEY ([chr_cliecodigo]) REFERENCES [dbo].[Cliente] ([chr_cliecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Transferencias_Cliente') ALTER TABLE [dbo].[Transferencias] WITH CHECK ADD CONSTRAINT [FK_Transferencias_Cliente] FOREIGN KEY ([chr_cliecodigo]) REFERENCES [dbo].[Cliente] ([chr_cliecodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Transferencias_Cuenta_Origen') ALTER TABLE [dbo].[Transferencias] WITH CHECK ADD CONSTRAINT [FK_Transferencias_Cuenta_Origen] FOREIGN KEY ([chr_cuencodigo_origen]) REFERENCES [dbo].[Cuenta] ([chr_cuencodigo]);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name=N'FK_Transferencias_Cuenta_Destino') ALTER TABLE [dbo].[Transferencias] WITH CHECK ADD CONSTRAINT [FK_Transferencias_Cuenta_Destino] FOREIGN KEY ([chr_cuencodigo_destino]) REFERENCES [dbo].[Cuenta] ([chr_cuencodigo]);
GO
