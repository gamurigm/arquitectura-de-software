<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="LoginVista.aspx.cs" Inherits="ClienteWebMonster.ec.edu.monster.vista.LoginVista" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Login - Ecosistema Monster</title>
    <style>
        body {
            background-color: #AEE7F8;
            font-family: 'Segoe UI', Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .caja-login {
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0px 4px 10px rgba(0,0,0,0.1);
            text-align: center;
            width: 300px;
        }
        .logo-sistema {
            width: 150px;
            margin-bottom: 15px;
        }
        .caja-texto {
            width: 90%;
            padding: 8px;
            margin: 10px 0 20px 0;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .boton-verde {
            background-color: #ABF1BC;
            border: 1px solid #7DDB95;
            padding: 10px 20px;
            font-weight: bold;
            cursor: pointer;
            border-radius: 4px;
            width: 100%;
        }
        .boton-verde:hover { background-color: #92E8A6; }
    </style>
</head>
<body>
    <form id="form1" runat="server">
        <div class="caja-login">
            
            <asp:Image ID="imgLogo" runat="server" ImageUrl="https://compote.slate.com/images/aead9b4c-c925-4175-b797-5ee15e74f5c1.jpg" CssClass="logo-sistema" />
            
            <h2>Conversión de unidades - Cliente Web</h2>
            
            <asp:Label ID="Label1" runat="server" Text="Usuario:"></asp:Label>
            <asp:TextBox ID="txtNombreUsuarioInstitucionalIngresado" runat="server" CssClass="caja-texto"></asp:TextBox>
            
            <asp:Label ID="Label2" runat="server" Text="Contraseña:"></asp:Label>
            <asp:TextBox ID="txtClaveAccesoSeguridadIngresada" runat="server" TextMode="Password" CssClass="caja-texto"></asp:TextBox>
            
            <asp:Button ID="btnValidarCredencialesAccesoWCF" runat="server" Text="INGRESAR" CssClass="boton-verde" OnClick="btnValidarCredencialesAccesoWCF_Click" />
            
            <br /><br />
            <asp:Label ID="lblMensajeError" runat="server" ForeColor="Red"></asp:Label>
        </div>
    </form>
</body>
</html>