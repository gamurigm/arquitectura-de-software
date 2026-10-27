/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2008                    */
/* Created on:     8/12/2024 13:13:07                           */
/*==============================================================*/
CREATE DATABASE BANQUITO;
USE BANQUITO;

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('CREDITO') and o.name = 'FK_CREDITO_REALIZA_CLIENTE')
alter table CREDITO
   drop constraint FK_CREDITO_REALIZA_CLIENTE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('CUENTA') and o.name = 'FK_CUENTA_POSEE_CLIENTE')
alter table CUENTA
   drop constraint FK_CUENTA_POSEE_CLIENTE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('CUOTA') and o.name = 'FK_CUOTA_CONTIENE3_CREDITO')
alter table CUOTA
   drop constraint FK_CUOTA_CONTIENE3_CREDITO
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('DETALLE_FACTURA') and o.name = 'FK_DETALLE__CONTIENE_TELEFONO')
alter table DETALLE_FACTURA
   drop constraint FK_DETALLE__CONTIENE_TELEFONO
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('DETALLE_FACTURA') and o.name = 'FK_DETALLE__CONTIENE2_FACTURA')
alter table DETALLE_FACTURA
   drop constraint FK_DETALLE__CONTIENE2_FACTURA
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('FACTURA') and o.name = 'FK_FACTURA_SOLICITA_CLIENTE')
alter table FACTURA
   drop constraint FK_FACTURA_SOLICITA_CLIENTE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('MOVIMIENTO') and o.name = 'FK_MOVIMIEN_TIENE_CUENTA')
alter table MOVIMIENTO
   drop constraint FK_MOVIMIEN_TIENE_CUENTA
go

if exists (select 1
            from  sysobjects
           where  id = object_id('CLIENTE')
            and   type = 'U')
   drop table CLIENTE
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('CREDITO')
            and   name  = 'REALIZA_FK'
            and   indid > 0
            and   indid < 255)
   drop index CREDITO.REALIZA_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('CREDITO')
            and   type = 'U')
   drop table CREDITO
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('CUENTA')
            and   name  = 'POSEE_FK'
            and   indid > 0
            and   indid < 255)
   drop index CUENTA.POSEE_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('CUENTA')
            and   type = 'U')
   drop table CUENTA
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('CUOTA')
            and   name  = 'CONTIENE3_FK'
            and   indid > 0
            and   indid < 255)
   drop index CUOTA.CONTIENE3_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('CUOTA')
            and   type = 'U')
   drop table CUOTA
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('DETALLE_FACTURA')
            and   name  = 'CONTIENE_FK'
            and   indid > 0
            and   indid < 255)
   drop index DETALLE_FACTURA.CONTIENE_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('DETALLE_FACTURA')
            and   name  = 'CONTIENE2_FK'
            and   indid > 0
            and   indid < 255)
   drop index DETALLE_FACTURA.CONTIENE2_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('DETALLE_FACTURA')
            and   type = 'U')
   drop table DETALLE_FACTURA
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('FACTURA')
            and   name  = 'SOLICITA_FK'
            and   indid > 0
            and   indid < 255)
   drop index FACTURA.SOLICITA_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('FACTURA')
            and   type = 'U')
   drop table FACTURA
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('MOVIMIENTO')
            and   name  = 'TIENE_FK'
            and   indid > 0
            and   indid < 255)
   drop index MOVIMIENTO.TIENE_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('MOVIMIENTO')
            and   type = 'U')
   drop table MOVIMIENTO
go

if exists (select 1
            from  sysobjects
           where  id = object_id('TELEFONO')
            and   type = 'U')
   drop table TELEFONO
go

/*==============================================================*/
/* Table: CLIENTE                                               */
/*==============================================================*/
create table CLIENTE (
   COD_CLIENTE          int                  not null,
   CEDULA               varchar(100)         null,
   NOMBRE               varchar(100)         null,
   GENERO               varchar(1)           null,
   FECHA_NACIMIENTO     datetime             null,
   constraint PK_CLIENTE primary key nonclustered (COD_CLIENTE)
)
go

/*==============================================================*/
/* Table: CREDITO                                               */
/*==============================================================*/
create table CREDITO (
   ID_CREDITO           int                  not null,
   COD_CLIENTE          int                  null,
   ESTADO               varchar(2)           null,
   CUOTAS               int                  null,
   VALOR_PRESTAMO       decimal(10,2)        null,
   TASA_ANUAL           decimal(10,2)        null,
   constraint PK_CREDITO primary key nonclustered (ID_CREDITO)
)
go

/*==============================================================*/
/* Index: REALIZA_FK                                            */
/*==============================================================*/
create index REALIZA_FK on CREDITO (
COD_CLIENTE ASC
)
go

/*==============================================================*/
/* Table: CUENTA                                                */
/*==============================================================*/
create table CUENTA (
   NUM_CUENTA           varchar(8)           not null,
   COD_CLIENTE          int                  null,
   SALDO                decimal(10,2)        null,
   constraint PK_CUENTA primary key nonclustered (NUM_CUENTA)
)
go

/*==============================================================*/
/* Index: POSEE_FK                                              */
/*==============================================================*/
create index POSEE_FK on CUENTA (
COD_CLIENTE ASC
)
go

/*==============================================================*/
/* Table: CUOTA                                                 */
/*==============================================================*/
create table CUOTA (
   N_CUOTA              int                  not null,
   ID_CREDITO           int                  null,
   VALOR_CUOTA          decimal(10,2)        null,
   INTERES_PAGADO       decimal(10,2)        null,
   CAPITAL_PAGADO       decimal(10,2)        null,
   SALDO                decimal(10,2)        null,
   constraint PK_CUOTA primary key nonclustered (N_CUOTA)
)
go

/*==============================================================*/
/* Index: CONTIENE3_FK                                          */
/*==============================================================*/
create index CONTIENE3_FK on CUOTA (
ID_CREDITO ASC
)
go

/*==============================================================*/
/* Table: DETALLE_FACTURA                                       */
/*==============================================================*/
create table DETALLE_FACTURA (
   COD_TEL              int                  not null,
   ID_FACTURA           int                  not null,
   CANTIDAD             int                  null,
   PRECIO_UNITARIO      decimal(10,2)        null,
   SUBTOTAL             decimal(10,2)        null,
   constraint PK_DETALLE_FACTURA primary key nonclustered (COD_TEL, ID_FACTURA)
)
go

/*==============================================================*/
/* Index: CONTIENE2_FK                                          */
/*==============================================================*/
create index CONTIENE2_FK on DETALLE_FACTURA (
ID_FACTURA ASC
)
go

/*==============================================================*/
/* Index: CONTIENE_FK                                           */
/*==============================================================*/
create index CONTIENE_FK on DETALLE_FACTURA (
COD_TEL ASC
)
go

/*==============================================================*/
/* Table: FACTURA                                               */
/*==============================================================*/
create table FACTURA (
   ID_FACTURA           int                  not null,
   COD_CLIENTE          int                  null,
   FECHA                datetime             null,
   TOTAL                decimal(10,2)        null,
   FORMA_PAGO           varchar(2)           null,
   DESCUENTO            int                  null,
   constraint PK_FACTURA primary key nonclustered (ID_FACTURA)
)
go

/*==============================================================*/
/* Index: SOLICITA_FK                                           */
/*==============================================================*/
create index SOLICITA_FK on FACTURA (
COD_CLIENTE ASC
)
go

/*==============================================================*/
/* Table: MOVIMIENTO                                            */
/*==============================================================*/
create table MOVIMIENTO (
   COD_MOVIMIENTO       int                  not null,
   NUM_CUENTA           varchar(8)           null,
   TIPO                 varchar(3)           null,
   VALOR                decimal(10,2)        null,
   FECHA                datetime             null,
   constraint PK_MOVIMIENTO primary key nonclustered (COD_MOVIMIENTO)
)
go

/*==============================================================*/
/* Index: TIENE_FK                                              */
/*==============================================================*/
create index TIENE_FK on MOVIMIENTO (
NUM_CUENTA ASC
)
go

/*==============================================================*/
/* Table: TELEFONO                                              */
/*==============================================================*/
create table TELEFONO (
   COD_TEL              int                  not null,
   NOMBRE               varchar(100)         null,
   PRECIO               decimal(10,2)        null,
   constraint PK_TELEFONO primary key nonclustered (COD_TEL)
)
go

alter table CREDITO
   add constraint FK_CREDITO_REALIZA_CLIENTE foreign key (COD_CLIENTE)
      references CLIENTE (COD_CLIENTE)
go

alter table CUENTA
   add constraint FK_CUENTA_POSEE_CLIENTE foreign key (COD_CLIENTE)
      references CLIENTE (COD_CLIENTE)
go

alter table CUOTA
   add constraint FK_CUOTA_CONTIENE3_CREDITO foreign key (ID_CREDITO)
      references CREDITO (ID_CREDITO)
go

alter table DETALLE_FACTURA
   add constraint FK_DETALLE__CONTIENE_TELEFONO foreign key (COD_TEL)
      references TELEFONO (COD_TEL)
go

alter table DETALLE_FACTURA
   add constraint FK_DETALLE__CONTIENE2_FACTURA foreign key (ID_FACTURA)
      references FACTURA (ID_FACTURA)
go

alter table FACTURA
   add constraint FK_FACTURA_SOLICITA_CLIENTE foreign key (COD_CLIENTE)
      references CLIENTE (COD_CLIENTE)
go

alter table MOVIMIENTO
   add constraint FK_MOVIMIEN_TIENE_CUENTA foreign key (NUM_CUENTA)
      references CUENTA (NUM_CUENTA)
go

select * from credito;

select * from CUOTA;

select * from telefono;

select * from CLIENTE