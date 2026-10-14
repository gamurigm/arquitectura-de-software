<%--
    Document   : index
    Created on : 15 abr 2026, 14:17:06
    Author     : Soto
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%! private String escapeHtml(String value) {
    if (value == null) return "";
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
} %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Inicio de sesión - Conversor de Unidades GR 9</title>
        <style>
            /* Fondo general de la página */
            body {
                font-family: sans-serif;
                background-color: #121212;
                color: white;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }

            /* Contenedor principal (la caja negra) */
            .login-container {
                display: flex;
                background-color: #151515;
                padding: 30px;
                border: 1px solid #333;
                box-shadow: 0 4px 15px rgba(0,0,0,0.8);
                gap: 40px; /* Espacio entre el form y la foto */
            }

            /* Columna izquierda (Formulario) */
            .form-section {
                display: flex;
                flex-direction: column;
                justify-content: center;
                width: 280px;
            }

            h3 {
                margin-top: 0;
                margin-bottom: 40px;
                font-size: 1.1em;
                font-weight: bold;
            }

            /* Fila para cada label + input */
            .input-group {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }

            label {
                font-weight: bold;
                font-size: 0.9em;
            }

            input {
                background-color: #404040;
                border: none;
                color: white;
                padding: 10px;
                width: 170px;
                outline: none;
            }

            /* Botón azul */
            button {
                background-color: #5b88e5;
                color: white;
                border: none;
                padding: 15px;
                font-weight: bold;
                margin-top: 20px;
                cursor: pointer;
                width: 100%;
                font-size: 1em;
            }

            button:hover {
                background-color: #4a75c9;
            }

            /* Columna derecha (Imagen) */
            .image-section img {
                width: 220px;
                height: 100%;
                object-fit: cover;
                border-radius: 2px;
            }

            /* Mensaje de error */
            .error {
                color: #ff4d4d;
                font-size: 0.85em;
                margin-top: 15px;
                text-align: center;
                font-weight: bold;
            }
        :root {
  color-scheme: dark;
  --background: #0B1020;
  --surface: #121C30;
  --field: #17243A;
  --border: #2D3A55;
  --text: #F4F7FC;
  --muted: #A9B7CB;
  --aqua: #52E0D2;
  --violet: #A78BFA;
  --danger: #FF8585;
  font-family: "Segoe UI", Inter, Arial, sans-serif;
}
* { box-sizing: border-box; }
body {
  min-height: 100vh; height: auto; margin: 0; padding: 24px;
  display: flex; align-items: center; justify-content: center;
  color: var(--text);
  background: radial-gradient(ellipse at 12% 0%, #52E0D21A, transparent 34rem),
              radial-gradient(ellipse at 92% 18%, #A78BFA18, transparent 30rem),
              var(--background);
}
.card, .login-container, .converter-box {
  width: min(760px, 100%); max-width: 760px; padding: clamp(24px, 5vw, 42px);
  color: var(--text); background: linear-gradient(145deg, #17243A, #10192B);
  border: 1px solid var(--border); border-radius: 20px;
  box-shadow: 0 24px 70px #03071380;
}
.login-container {
  display: grid; grid-template-columns: minmax(0, 1fr) 180px;
  align-items: center; gap: 32px;
}
.form-section { width: auto; min-width: 0; }
.image-section img, .monster-image {
  display: block; width: 180px; height: 240px; object-fit: cover;
  border: 1px solid var(--border); border-radius: 14px;
}
h1, h2, h3 { color: var(--text); letter-spacing: -.02em; }
h1 { font-size: clamp(26px, 5vw, 34px); }
p, .muted, .usuario { color: var(--muted); line-height: 1.55; }
label { display: block; margin: 10px 0 6px; color: var(--text); font-size: 14px; font-weight: 650; }
.input-group { display: grid; gap: 6px; margin-bottom: 14px; }
.input-group label { margin: 0; }
input, select {
  width: 100%; min-height: 48px; padding: 10px 14px;
  color: var(--text); background: var(--field); border: 1px solid var(--border);
  border-radius: 12px; font: inherit;
}
input::placeholder { color: var(--muted); }
input:focus, select:focus { outline: 3px solid #52E0D255; outline-offset: 1px; border-color: var(--aqua); }
button {
  width: 100%; min-height: 48px; margin-top: 14px; padding: 11px 18px;
  color: var(--background); background: var(--aqua); border: 1px solid var(--aqua);
  border-radius: 12px; font: inherit; font-weight: 750; cursor: pointer;
  transition: transform 150ms ease, background-color 150ms ease, box-shadow 150ms ease;
}
button:hover { transform: translateY(-1px); background: #7AEAE0; box-shadow: 0 10px 24px #52E0D22B; }
button:focus-visible, a:focus-visible { outline: 3px solid var(--violet); outline-offset: 3px; }
.card .top { display: flex; align-items: center; justify-content: space-between; gap: 14px; margin-bottom: 22px; }
.usuario a, .logout { color: var(--violet); }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.columns-container { display: grid; grid-template-columns: minmax(0, 1fr) 42px minmax(0, 1fr); gap: 16px; align-items: end; }
.col { display: flex; flex-direction: column; width: auto; min-width: 0; }
.col-center { display: flex; width: auto; justify-content: center; align-items: center; padding: 0 0 12px; }
.col-center img { width: 36px; height: 36px; object-fit: contain; }
.resultado { margin-top: 20px; padding: 18px; border: 1px solid #52E0D280; border-radius: 14px; background: #52E0D212; color: var(--text); text-align: center; }
.resultado .valor { color: var(--aqua); font-size: 26px; font-weight: 750; }
.error { margin-top: 16px; padding: 12px 14px; border: 1px solid #FF858580; border-radius: 12px; background: #FF858512; color: #FFD4D4; }
.logout { display: block; margin-top: 18px; text-align: center; text-decoration: none; }
@media (max-width: 620px) {
  body { align-items: flex-start; padding: 14px; }
  .card, .login-container, .converter-box { padding: 22px; border-radius: 18px; }
  .login-container, .grid, .columns-container { grid-template-columns: 1fr; gap: 12px; }
  .image-section img, .monster-image { width: 148px; height: 148px; margin: 8px auto 0; object-position: center 25%; }
  .col-center { padding: 0; transform: rotate(90deg); }
  .card .top { align-items: flex-start; flex-direction: column; }
}
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after { scroll-behavior: auto !important; transition-duration: .01ms !important; animation-duration: .01ms !important; }
}
    </style>
    </head>
    <body>
        <div class="login-container">

            <div class="form-section">
                <p class="muted">MONSTER · SOAP JAVA · GR09</p>
                <h1>Iniciar sesión</h1>

                <form method="POST" action="iniciar-sesion">
                    <div class="input-group">
                        <label>Usuario:</label>
                    <input type="text" name="txtUsuario" value="Monster" required>
                    </div>
                    <div class="input-group">
                        <label>Clave:</label>
                    <input type="password" name="txtClave" value="Monster9" required>
                    </div>
                    <button type="submit">INICIAR SESIÓN</button>
                </form>

                <%-- Mensaje de error si falla el login --%>
                <% if(request.getAttribute("error") != null) { %>
                    <div class="error"><%= escapeHtml(String.valueOf(request.getAttribute("error"))) %></div>
                <% } %>
            </div>

            <div class="image-section">
                <img src="Imagenes/imagen_inicio_sesion.jpg" alt=""/>
            </div>

        </div>
    </body>
</html>
