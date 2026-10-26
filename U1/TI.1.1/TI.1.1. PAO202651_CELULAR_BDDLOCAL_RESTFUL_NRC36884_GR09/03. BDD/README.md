# 03. BDD — Comercializadora

Estos scripts pertenecen solo a la tienda y crean/siembran `ComercializadoraCelulares`:

1. `01_CrearBases.sql`
2. `02_DatosPrueba.sql`

Son idempotentes y no borran datos. Aplicarlos con el inicializador `04. SERVIDOR/Administracion`. Para SQL Server Developer local, usar `CELULARES_SQL_MASTER` con la instancia predeterminada. La base BanQuitoCore, sus datos y su inicializador están en el proyecto bancario bajo `U1/TG.1/1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR09`.
