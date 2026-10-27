-- Datos de prueba GR09, convertidos desde Carga_Datos.sql de Java.
-- Monster queda vinculado al cliente 00005, titular de dos cuentas activas.
-- Ejecutar una sola vez sobre EurekaBankGR09 vacía.
USE [EurekaBankGR09];
SET XACT_ABORT ON;
IF EXISTS (SELECT 1 FROM dbo.[Asignado]) OR
    EXISTS (SELECT 1 FROM dbo.[CargoMantenimiento]) OR
    EXISTS (SELECT 1 FROM dbo.[Cliente]) OR
    EXISTS (SELECT 1 FROM dbo.[Contador]) OR
    EXISTS (SELECT 1 FROM dbo.[CostoMovimiento]) OR
    EXISTS (SELECT 1 FROM dbo.[Credenciales]) OR
    EXISTS (SELECT 1 FROM dbo.[Cuenta]) OR
    EXISTS (SELECT 1 FROM dbo.[Empleado]) OR
    EXISTS (SELECT 1 FROM dbo.[InteresMensual]) OR
    EXISTS (SELECT 1 FROM dbo.[Modulo]) OR
    EXISTS (SELECT 1 FROM dbo.[Moneda]) OR
    EXISTS (SELECT 1 FROM dbo.[Movimiento]) OR
    EXISTS (SELECT 1 FROM dbo.[Parametro]) OR
    EXISTS (SELECT 1 FROM dbo.[Permiso]) OR
    EXISTS (SELECT 1 FROM dbo.[Sucursal]) OR
    EXISTS (SELECT 1 FROM dbo.[TipoMovimiento]) OR
    EXISTS (SELECT 1 FROM dbo.[Usuario])
BEGIN
    ;THROW 51000, 'La carga de prueba requiere las tablas destino vacias.', 1;
END;
BEGIN TRY
    BEGIN TRANSACTION;
    INSERT INTO dbo.[Moneda] ([chr_monecodigo], [vch_monedescripcion]) VALUES ('01', 'Soles');
    INSERT INTO dbo.[Moneda] ([chr_monecodigo], [vch_monedescripcion]) VALUES ('02', 'Dolares');
    INSERT INTO dbo.[CargoMantenimiento] ([chr_monecodigo], [dec_cargMontoMaximo], [dec_cargImporte]) VALUES ('01', 3500.00, 7.00);
    INSERT INTO dbo.[CargoMantenimiento] ([chr_monecodigo], [dec_cargMontoMaximo], [dec_cargImporte]) VALUES ('02', 1200.00, 2.50);
    INSERT INTO dbo.[CostoMovimiento] ([chr_monecodigo], [dec_costimporte]) VALUES ('01', 2.00);
    INSERT INTO dbo.[CostoMovimiento] ([chr_monecodigo], [dec_costimporte]) VALUES ('02', 0.60);
    INSERT INTO dbo.[InteresMensual] ([chr_monecodigo], [dec_inteimporte]) VALUES ('01', 0.70);
    INSERT INTO dbo.[InteresMensual] ([chr_monecodigo], [dec_inteimporte]) VALUES ('02', 0.60);
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('001', 'Apertura de Cuenta', 'INGRESO', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('002', 'Cancelar Cuenta', 'SALIDA', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('003', 'Deposito', 'INGRESO', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('004', 'Retiro', 'SALIDA', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('005', 'Interes', 'INGRESO', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('006', 'Mantenimiento', 'SALIDA', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('007', 'ITF', 'SALIDA', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('008', 'Transferencia', 'INGRESO', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('009', 'Transferencia', 'SALIDA', 'ACTIVO');
    INSERT INTO dbo.[TipoMovimiento] ([chr_tipocodigo], [vch_tipodescripcion], [vch_tipoaccion], [vch_tipoestado]) VALUES ('010', 'Cargo por Movimiento', 'SALIDA', 'ACTIVO');
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('001', 'Sipan', 'Chiclayo', 'Av. Balta 1456', 2);
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('002', 'Chan Chan', 'Trujillo', 'Jr. Independencia 456', 3);
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('003', 'Los Olivos', 'Lima', 'Av. Central 1234', 0);
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('004', 'Pardo', 'Lima', 'Av. Pardo 345 - Miraflores', 0);
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('005', 'Misti', 'Arequipa', 'Bolivar 546', 0);
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('006', 'Machupicchu', 'Cusco', 'Calle El Sol 534', 0);
    INSERT INTO dbo.[Sucursal] ([chr_sucucodigo], [vch_sucunombre], [vch_sucuciudad], [vch_sucudireccion], [int_sucucontcuenta]) VALUES ('007', 'Grau', 'Piura', 'Av. Grau 1528', 0);
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('9999', 'Internet', 'Internet', 'internet', 'Internet', 'internet');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0001', 'Romero', 'Castillo', 'Carlos Alberto', 'Trujillo', 'Call1 1 Nro. 456');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0002', 'Castro', 'Vargas', 'Lidia', 'Lima', 'Federico Villarreal 456 - SMP');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0003', 'Reyes', 'Ortiz', 'Claudia', 'Lima', 'Av. Aviación 3456 - San Borja');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0004', 'Ramos', 'Garibay', 'Angelica', 'Chiclayo', 'Calle Barcelona 345');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0005', 'Ruiz', 'Zabaleta', 'Claudia', 'Cusco', 'Calle Cruz Verde 364');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0006', 'Cruz', 'Tarazona', 'Ricardo', 'Areguipa', 'Calle La Gruta 304');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0007', 'Diaz', 'Flores', 'Edith', 'Lima', 'Av. Pardo 546');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0008', 'Sarmiento', 'Bellido', 'Claudia Rocio', 'Areguipa', 'Calle Alfonso Ugarte 1567');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0009', 'Pachas', 'Sifuentes', 'Luis Alberto', 'Trujillo', 'Francisco Pizarro 1263');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0010', 'Tello', 'Alarcon', 'Hugo Valentin', 'Cusco', 'Los Angeles 865');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0011', 'Carrasco', 'Vargas', 'Pedro Hugo', 'Chiclayo', 'Av. Balta 1265');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0012', 'Mendoza', 'Jara', 'Monica Valeria', 'Lima', 'Calle Las Toronjas 450');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0013', 'Espinoza', 'Melgar', 'Victor Eduardo', 'Huancayo', 'Av. San Martin 6734 Dpto. 508 ');
    INSERT INTO dbo.[Empleado] ([chr_emplcodigo], [vch_emplpaterno], [vch_emplmaterno], [vch_emplnombre], [vch_emplciudad], [vch_empldireccion]) VALUES ('0014', 'Hidalgo', 'Sandoval', 'Milagros Leonor', 'Chiclayo', 'Av. Luis Gonzales 1230');
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('9999', 'internet', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'internet'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0001', 'cromero', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'chicho'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0002', 'lcastro', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'flaca'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0003', 'creyes', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'linda'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0004', 'aramos', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'china'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0005', 'cvalencia', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'angel'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0006', 'rcruz', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'cerebro'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0007', 'ediaz', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'princesa'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0008', 'csarmiento', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'chinita'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0009', 'lpachas', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'gato'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0010', 'htello', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'machupichu'), 2));
    INSERT INTO dbo.[Usuario] ([chr_emplcodigo], [vch_emplusuario], [vch_emplclave]) VALUES ('0011', 'pcarrasco', CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'tinajones'), 2));
    INSERT INTO dbo.[Modulo] ([int_moducodigo], [vch_modunombre]) VALUES (1, 'Procesos');
    INSERT INTO dbo.[Modulo] ([int_moducodigo], [vch_modunombre]) VALUES (2, 'Tablas');
    INSERT INTO dbo.[Modulo] ([int_moducodigo], [vch_modunombre]) VALUES (3, 'Consultas');
    INSERT INTO dbo.[Modulo] ([int_moducodigo], [vch_modunombre]) VALUES (4, 'Reportes');
    INSERT INTO dbo.[Modulo] ([int_moducodigo], [vch_modunombre]) VALUES (5, 'Util');
    INSERT INTO dbo.[Modulo] ([int_moducodigo], [vch_modunombre]) VALUES (6, 'Seguridad');
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0001', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0001', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0001', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0001', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0001', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0001', 6);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0002', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0002', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0002', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0002', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0002', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0002', 6);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0003', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0003', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0003', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0003', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0003', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0003', 6);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0004', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0004', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0004', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0004', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0004', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0004', 6);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0005', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0005', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0005', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0005', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0005', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0005', 6);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0006', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0006', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0006', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0006', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0006', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0006', 6);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0007', 1);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0007', 2);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0007', 3);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0007', 4);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0007', 5);
    INSERT INTO dbo.[Permiso] ([chr_emplcodigo], [int_moducodigo]) VALUES ('0007', 6);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000001', '001', '0004', '20071115', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000002', '002', '0001', '20071120', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000003', '003', '0002', '20071128', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000004', '004', '0003', '20071212', '20080325');
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000005', '005', '0006', '20071220', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000006', '006', '0005', '20080105', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000007', '004', '0007', '20080107', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000008', '005', '0008', '20080107', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000009', '001', '0011', '20080108', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000010', '002', '0009', '20080108', null);
    INSERT INTO dbo.[Asignado] ([chr_asigcodigo], [chr_sucucodigo], [chr_emplcodigo], [dtt_asigfechaalta], [dtt_asigfechabaja]) VALUES ('000011', '006', '0010', '20080108', null);
    INSERT INTO dbo.[Parametro] ([chr_paracodigo], [vch_paradescripcion], [vch_paravalor], [vch_paraestado]) VALUES ('001', 'ITF - Impuesto a la Transacciones Financieras', '0.08', 'ACTIVO');
    INSERT INTO dbo.[Parametro] ([chr_paracodigo], [vch_paradescripcion], [vch_paravalor], [vch_paraestado]) VALUES ('002', 'Número de Operaciones Sin Costo', '15', 'ACTIVO');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00001', 'CORONEL', 'CASTILLO', 'ERIC GUSTAVO', '06914897', 'LIMA', 'LOS OLIVOS', '996-664-457', 'gcoronelc@gmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00002', 'VALENCIA', 'MORALES', 'PEDRO HUGO', '01576173', 'LIMA', 'MAGDALENA', '924-7834', 'pvalencia@terra.com.pe');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00003', 'MARCELO', 'VILLALOBOS', 'RICARDO', '10762367', 'LIMA', 'LINCE', '993-62966', 'ricardomarcelo@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00004', 'ROMERO', 'CASTILLO', 'CARLOS ALBERTO', '06531983', 'LIMA', 'LOS OLIVOS', '865-84762', 'c.romero@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00005', 'ARANDA', 'LUNA', 'ALAN ALBERTO', '10875611', 'LIMA', 'SAN ISIDRO', '834-67125', 'a.aranda@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00006', 'AYALA', 'PAZ', 'JORGE LUIS', '10679245', 'LIMA', 'SAN BORJA', '963-34769', 'j.ayala@yahoo.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00007', 'CHAVEZ', 'CANALES', 'EDGAR RAFAEL', '10145693', 'LIMA', 'MIRAFLORES', '999-96673', 'e.chavez@gmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00008', 'FLORES', 'CHAFLOQUE', 'ROSA LIZET', '10773456', 'LIMA', 'LA MOLINA', '966-87567', 'r.florez@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00009', 'FLORES', 'CASTILLO', 'CRISTIAN RAFAEL', '10346723', 'LIMA', 'LOS OLIVOS', '978-43768', 'c.flores@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00010', 'GONZALES', 'GARCIA', 'GABRIEL ALEJANDRO', '10192376', 'LIMA', 'SAN MIGUEL', '945-56782', 'g.gonzales@yahoo.es');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00011', 'LAY', 'VALLEJOS', 'JUAN CARLOS', '10942287', 'LIMA', 'LINCE', '956-12657', 'j.lay@peru.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00012', 'MONTALVO', 'SOTO', 'DEYSI LIDIA', '10612376', 'LIMA', 'SURCO', '965-67235', 'd.montalvo@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00013', 'RICALDE', 'RAMIREZ', 'ROSARIO ESMERALDA', '10761324', 'LIMA', 'MIRAFLORES', '991-23546', 'r.ricalde@gmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00014', 'RODRIGUEZ', 'FLORES', 'ENRIQUE MANUEL', '10773345', 'LIMA', 'LINCE', '976-82838', 'e.rodriguez@gmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00015', 'ROJAS', 'OSCANOA', 'FELIX NINO', '10238943', 'LIMA', 'LIMA', '962-32158', 'f.rojas@yahoo.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00016', 'TEJADA', 'DEL AGUILA', 'TANIA LORENA', '10446791', 'LIMA', 'PUEBLO LIBRE', '966-23854', 't.tejada@hotmail.com');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00017', 'VALDEVIESO', 'LEYVA', 'LIDIA ROXANA', '10452682', 'LIMA', 'SURCO', '956-78951', 'r.valdivieso@terra.com.pe');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00018', 'VALENTIN', 'COTRINA', 'JUAN DIEGO', '10398247', 'LIMA', 'LA MOLINA', '921-12456', 'j.valentin@terra.com.pe');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00019', 'YAURICASA', 'BAUTISTA', 'YESABETH', '10934584', 'LIMA', 'MAGDALENA', '977-75777', 'y.yauricasa@terra.com.pe');
    INSERT INTO dbo.[Cliente] ([chr_cliecodigo], [vch_cliepaterno], [vch_cliematerno], [vch_clienombre], [chr_cliedni], [vch_clieciudad], [vch_cliedireccion], [vch_clietelefono], [vch_clieemail]) VALUES ('00020', 'ZEGARRA', 'GARCIA', 'FERNANDO MOISES', '10772365', 'LIMA', 'SAN ISIDRO', '936-45876', 'f.zegarra@hotmail.com');
    INSERT INTO dbo.[Cuenta] ([chr_cuencodigo], [chr_monecodigo], [chr_sucucodigo], [chr_emplcreacuenta], [chr_cliecodigo], [dec_cuensaldo], [dtt_cuenfechacreacion], [vch_cuenestado], [int_cuencontmov], [chr_cuenclave]) VALUES ('00200001', '01', '002', '0001', '00008', 7000, '20080105', 'ACTIVO', 15, '123456');
    INSERT INTO dbo.[Cuenta] ([chr_cuencodigo], [chr_monecodigo], [chr_sucucodigo], [chr_emplcreacuenta], [chr_cliecodigo], [dec_cuensaldo], [dtt_cuenfechacreacion], [vch_cuenestado], [int_cuencontmov], [chr_cuenclave]) VALUES ('00200002', '01', '002', '0001', '00001', 6800, '20080109', 'ACTIVO', 3, '123456');
    INSERT INTO dbo.[Cuenta] ([chr_cuencodigo], [chr_monecodigo], [chr_sucucodigo], [chr_emplcreacuenta], [chr_cliecodigo], [dec_cuensaldo], [dtt_cuenfechacreacion], [vch_cuenestado], [int_cuencontmov], [chr_cuenclave]) VALUES ('00200003', '02', '002', '0001', '00007', 6000, '20080111', 'ACTIVO', 6, '123456');
    INSERT INTO dbo.[Cuenta] ([chr_cuencodigo], [chr_monecodigo], [chr_sucucodigo], [chr_emplcreacuenta], [chr_cliecodigo], [dec_cuensaldo], [dtt_cuenfechacreacion], [vch_cuenestado], [int_cuencontmov], [chr_cuenclave]) VALUES ('00100001', '01', '001', '0004', '00005', 6900, '20080106', 'ACTIVO', 7, '123456');
    INSERT INTO dbo.[Cuenta] ([chr_cuencodigo], [chr_monecodigo], [chr_sucucodigo], [chr_emplcreacuenta], [chr_cliecodigo], [dec_cuensaldo], [dtt_cuenfechacreacion], [vch_cuenestado], [int_cuencontmov], [chr_cuenclave]) VALUES ('00100002', '02', '001', '0004', '00005', 4500, '20080108', 'ACTIVO', 4, '123456');
    INSERT INTO dbo.[Cuenta] ([chr_cuencodigo], [chr_monecodigo], [chr_sucucodigo], [chr_emplcreacuenta], [chr_cliecodigo], [dec_cuensaldo], [dtt_cuenfechacreacion], [vch_cuenestado], [int_cuencontmov], [chr_cuenclave]) VALUES ('00300001', '01', '003', '0002', '00010', 0000, '20080107', 'CANCELADO', 3, '123456');
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100002', 01, '20080108', '0004', '001', 1800, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100002', 02, '20080125', '0004', '004', 1000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100002', 03, '20080213', '0004', '003', 2200, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100002', 04, '20080308', '0004', '003', 1500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 01, '20080106', '0004', '001', 2800, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 02, '20080115', '0004', '003', 3200, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 03, '20080120', '0004', '004', 0800, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 04, '20080214', '0004', '003', 2000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 05, '20080225', '0004', '004', 0500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 06, '20080303', '0004', '004', 0800, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00100001', 07, '20080315', '0004', '003', 1000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200003', 01, '20080111', '0001', '001', 2500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200003', 02, '20080117', '0001', '003', 1500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200003', 03, '20080120', '0001', '004', 0500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200003', 04, '20080209', '0001', '004', 0500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200003', 05, '20080225', '0001', '003', 3500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200003', 06, '20080311', '0001', '004', 0500, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200002', 01, '20080109', '0001', '001', 3800, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200002', 02, '20080120', '0001', '003', 4200, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200002', 03, '20080306', '0001', '004', 1200, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 01, '20080105', '0001', '001', 5000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 02, '20080107', '0001', '003', 4000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 03, '20080109', '0001', '004', 2000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 04, '20080111', '0001', '003', 1000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 05, '20080113', '0001', '003', 2000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 06, '20080115', '0001', '004', 4000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 07, '20080119', '0001', '003', 2000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 08, '20080121', '0001', '004', 3000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 09, '20080123', '0001', '003', 7000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 10, '20080127', '0001', '004', 1000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 11, '20080130', '0001', '004', 3000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 12, '20080204', '0001', '003', 2000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 13, '20080208', '0001', '004', 4000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 14, '20080213', '0001', '003', 2000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00200001', 15, '20080219', '0001', '004', 1000, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00300001', 01, '20080107', '0002', '001', 5600, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00300001', 02, '20080118', '0002', '003', 1400, null);
    INSERT INTO dbo.[Movimiento] ([chr_cuencodigo], [int_movinumero], [dtt_movifecha], [chr_emplcodigo], [chr_tipocodigo], [dec_moviimporte], [chr_cuenreferencia]) VALUES ('00300001', 03, '20080125', '0002', '002', 7000, null);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('Moneda', 2, 2);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('TipoMovimiento', 10, 3);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('Sucursal', 7, 3);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('Empleado', 14, 4);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('Asignado', 11, 6);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('Parametro', 2, 3);
    INSERT INTO dbo.[Contador] ([vch_conttabla], [int_contitem], [int_contlongitud]) VALUES ('Cliente', 20, 5);
    INSERT INTO dbo.[Credenciales] ([usuario], [contrasena], [chr_cliecodigo]) VALUES ('Monster', 'pbkdf2-sha256$600000$5cV5Rt7hgFTlKOdg0I25uA$XVGksBT2fPaLTMjHEbMEIiOa-2o8LU9dEzEFcUCEj7o', '00005');
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
