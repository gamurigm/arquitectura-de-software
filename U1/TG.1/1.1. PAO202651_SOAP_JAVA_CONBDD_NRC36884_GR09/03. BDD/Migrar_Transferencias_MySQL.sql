-- MIGRACION PARA ESQUEMAS LEGADOS: usar solo antes de aplicar el soporte actual de transferencias.
-- No ejecutar en una instalacion nueva ni despues del script vigente de creacion del esquema:
-- este ya crea Transferencias y agrega Movimiento.id_transferencia y su clave foranea.
-- Este archivo usa CREATE TABLE y ALTER TABLE sin proteccion de repeticion; volver a ejecutarlo
-- sobre esos objetos provoca errores por tabla, columna o restriccion ya existentes.
-- Antes de migrar, respaldar la base y validar la asociacion de Credenciales.chr_cliecodigo.
USE eurekabank_gr09;

SET @ddl = IF(
    EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'Credenciales' AND COLUMN_NAME = 'chr_cliecodigo'),
    'SELECT 1',
    'ALTER TABLE Credenciales ADD COLUMN chr_cliecodigo CHAR(5) NULL'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    EXISTS (SELECT 1 FROM information_schema.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'Credenciales' AND CONSTRAINT_NAME = 'FK_Credenciales_Cliente'),
    'SELECT 1',
    'ALTER TABLE Credenciales ADD CONSTRAINT FK_Credenciales_Cliente FOREIGN KEY (chr_cliecodigo) REFERENCES Cliente (chr_cliecodigo) ON DELETE RESTRICT ON UPDATE RESTRICT'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE Transferencias (
    id_transferencia CHAR(36) NOT NULL,
    usuario VARCHAR(30) NOT NULL,
    clave_operacion CHAR(36) NOT NULL,
    chr_cliecodigo CHAR(5) NOT NULL,
    chr_cuencodigo_origen CHAR(8) NOT NULL,
    chr_cuencodigo_destino CHAR(8) NOT NULL,
    dec_importe DECIMAL(12,2) NOT NULL,
    dtt_fecha DATETIME NOT NULL,
    CONSTRAINT PK_Transferencias PRIMARY KEY (id_transferencia),
    CONSTRAINT UQ_Transferencias_Usuario_Clave UNIQUE (usuario, clave_operacion),
    CONSTRAINT CK_Transferencias_Importe CHECK (dec_importe > 0),
    CONSTRAINT CK_Transferencias_Cuentas CHECK (chr_cuencodigo_origen <> chr_cuencodigo_destino),
    CONSTRAINT FK_Transferencias_Cliente FOREIGN KEY (chr_cliecodigo) REFERENCES Cliente (chr_cliecodigo),
    CONSTRAINT FK_Transferencias_Origen FOREIGN KEY (chr_cuencodigo_origen) REFERENCES Cuenta (chr_cuencodigo),
    CONSTRAINT FK_Transferencias_Destino FOREIGN KEY (chr_cuencodigo_destino) REFERENCES Cuenta (chr_cuencodigo)
) ENGINE = INNODB;

ALTER TABLE Movimiento
    ADD COLUMN id_transferencia CHAR(36) NULL,
    ADD CONSTRAINT FK_Movimiento_Transferencias FOREIGN KEY (id_transferencia)
        REFERENCES Transferencias (id_transferencia)
        ON DELETE RESTRICT ON UPDATE RESTRICT;

-- Asigne chr_cliecodigo solo tras validar la identidad del titular. No se infiere desde el nombre de usuario.
