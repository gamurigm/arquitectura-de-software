<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="DashboardVista.aspx.cs" Inherits="ClienteWebMonster.ec.edu.monster.vista.DashboardVista" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Dashboard - Ecosistema Monster</title>
    <style>
        body {
            background-color: #AEE7F8;
            font-family: 'Segoe UI', Arial, sans-serif;
            padding: 40px;
        }
        .contenedor-principal {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0px 4px 10px rgba(0,0,0,0.1);
            max-width: 800px;
            margin: 0 auto;
        }
        .pestanas {
            margin-bottom: 20px;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
        }
        .boton-pestana {
            background-color: #f1f1f1;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            font-weight: bold;
            border-radius: 5px 5px 0 0;
        }
        .boton-pestana.activa { background-color: #AEE7F8; }
        .fila-formulario {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }
        .caja-texto, .lista-desplegable {
            width: 100%;
            padding: 8px;
            margin-top: 5px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .boton-verde {
            background-color: #ABF1BC;
            border: 1px solid #7DDB95;
            padding: 10px 30px;
            font-weight: bold;
            cursor: pointer;
            border-radius: 4px;
            display: block;
            margin: 20px auto;
        }
        .boton-verde:hover { background-color: #92E8A6; }
        .resultado {
            text-align: center;
            font-size: 1.2em;
            font-weight: bold;
            color: #333;
            margin-top: 20px;
        }
        .logo-sistema {
            display: block;
            margin: 0 auto 15px;
        }
    </style>
</head>
<body>
    <form id="form1" runat="server">
        <div class="contenedor-principal">

            <asp:Image ID="imgLogo" runat="server" 
                ImageUrl="https://compote.slate.com/images/aead9b4c-c925-4175-b797-5ee15e74f5c1.jpg" 
                CssClass="logo-sistema" 
                Width="50%" />

            <h2>Panel de Conversiones</h2>
            
            <div class="pestanas">
                <asp:Button ID="btnTabMasa" runat="server" Text="Masa" CssClass="boton-pestana activa" OnClick="CambiarPestana" CommandArgument="0" />
                <asp:Button ID="btnTabTemp" runat="server" Text="Temperatura" CssClass="boton-pestana" OnClick="CambiarPestana" CommandArgument="1" />
                <asp:Button ID="btnTabLong" runat="server" Text="Longitud" CssClass="boton-pestana" OnClick="CambiarPestana" CommandArgument="2" />
            </div>

            <asp:MultiView ID="mvwPestanas" runat="server" ActiveViewIndex="0">
                
                <asp:View ID="vwMasa" runat="server">
                    <div class="fila-formulario">
                        <div style="width: 48%;">
                            <label>Escoge una conversión:</label>
                            <asp:DropDownList ID="cmbListaOperacionesMasaSeleccionada" runat="server" CssClass="lista-desplegable"></asp:DropDownList>
                        </div>
                        <div style="width: 48%;">
                            <label>Escribe el valor a convertir:</label>
                            <asp:TextBox ID="txtValorNumericoIngresadoMasa" runat="server" CssClass="caja-texto"></asp:TextBox>
                        </div>
                    </div>
                    <asp:Button ID="btnEjecutarConversionMasaWCF" runat="server" Text="CALCULAR" CssClass="boton-verde" OnClick="btnEjecutarConversionMasaWCF_Click" />
                    <div class="resultado">
                        <asp:Label ID="lblResultadoCalculoMasaSistema" runat="server" Text="Resultado"></asp:Label>
                    </div>
                </asp:View>

                <asp:View ID="vwTemp" runat="server">
                    <div class="fila-formulario">
                        <div style="width: 48%;">
                            <label>Escoge una conversión:</label>
                            <asp:DropDownList ID="cmbListaOperacionesTempSeleccionada" runat="server" CssClass="lista-desplegable"></asp:DropDownList>
                        </div>
                        <div style="width: 48%;">
                            <label>Escribe el valor a convertir:</label>
                            <asp:TextBox ID="txtValorNumericoIngresadoTemp" runat="server" CssClass="caja-texto"></asp:TextBox>
                        </div>
                    </div>
                    <asp:Button ID="btnEjecutarConversionTempWCF" runat="server" Text="CALCULAR" CssClass="boton-verde" OnClick="btnEjecutarConversionTempWCF_Click" />
                    <div class="resultado">
                        <asp:Label ID="lblResultadoCalculoTempSistema" runat="server" Text="Resultado"></asp:Label>
                    </div>
                </asp:View>

                <asp:View ID="vwLong" runat="server">
                    <div class="fila-formulario">
                        <div style="width: 48%;">
                            <label>Escoge una conversión:</label>
                            <asp:DropDownList ID="cmbListaOperacionesLongSeleccionada" runat="server" CssClass="lista-desplegable"></asp:DropDownList>
                        </div>
                        <div style="width: 48%;">
                            <label>Escribe el valor a convertir:</label>
                            <asp:TextBox ID="txtValorNumericoIngresadoLong" runat="server" CssClass="caja-texto"></asp:TextBox>
                        </div>
                    </div>
                    <asp:Button ID="btnEjecutarConversionLongWCF" runat="server" Text="CALCULAR" CssClass="boton-verde" OnClick="btnEjecutarConversionLongWCF_Click" />
                    <div class="resultado">
                        <asp:Label ID="lblResultadoCalculoLongSistema" runat="server" Text="Resultado"></asp:Label>
                    </div>
                </asp:View>

            </asp:MultiView>
        </div>
    </form>
</body>
</html>