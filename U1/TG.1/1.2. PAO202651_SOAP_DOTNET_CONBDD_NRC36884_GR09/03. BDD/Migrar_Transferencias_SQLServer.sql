SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF COL_LENGTH('dbo.Credenciales', 'chr_cliecodigo') IS NULL
    ALTER TABLE dbo.Credenciales ADD chr_cliecodigo CHAR(5) NULL;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Credenciales_Cliente')
    ALTER TABLE dbo.Credenciales WITH CHECK
        ADD CONSTRAINT FK_Credenciales_Cliente FOREIGN KEY (chr_cliecodigo)
        REFERENCES dbo.Cliente (chr_cliecodigo);

IF OBJECT_ID('dbo.Transferencias', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Transferencias (
        id_transferencia UNIQUEIDENTIFIER NOT NULL,
        usuario VARCHAR(30) NOT NULL,
        clave_operacion UNIQUEIDENTIFIER NOT NULL,
        chr_cliecodigo CHAR(5) NOT NULL,
        chr_cuencodigo_origen CHAR(8) NOT NULL,
        chr_cuencodigo_destino CHAR(8) NOT NULL,
        dec_importe DECIMAL(12,2) NOT NULL,
        dtt_fecha DATETIME2(0) NOT NULL,
        CONSTRAINT PK_Transferencias PRIMARY KEY (id_transferencia),
        CONSTRAINT UQ_Transferencias_Usuario_Clave UNIQUE (usuario, clave_operacion),
        CONSTRAINT CK_Transferencias_Importe CHECK (dec_importe > 0),
        CONSTRAINT CK_Transferencias_Cuentas CHECK (chr_cuencodigo_origen <> chr_cuencodigo_destino),
        CONSTRAINT FK_Transferencias_Cliente FOREIGN KEY (chr_cliecodigo) REFERENCES dbo.Cliente (chr_cliecodigo),
        CONSTRAINT FK_Transferencias_Origen FOREIGN KEY (chr_cuencodigo_origen) REFERENCES dbo.Cuenta (chr_cuencodigo),
        CONSTRAINT FK_Transferencias_Destino FOREIGN KEY (chr_cuencodigo_destino) REFERENCES dbo.Cuenta (chr_cuencodigo)
    );
END;

IF COL_LENGTH('dbo.Movimiento', 'id_transferencia') IS NULL
    ALTER TABLE dbo.Movimiento ADD id_transferencia UNIQUEIDENTIFIER NULL;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Movimiento_Transferencias')
    ALTER TABLE dbo.Movimiento WITH CHECK
        ADD CONSTRAINT FK_Movimiento_Transferencias FOREIGN KEY (id_transferencia)
        REFERENCES dbo.Transferencias (id_transferencia);

COMMIT TRANSACTION;

-- Asigne chr_cliecodigo solo tras validar la identidad del titular. No se infiere desde el nombre de usuario.
