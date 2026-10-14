<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%! private String escapeHtml(String value) {
    if (value == null) return "";
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
} %>
<%
    String error = (String) request.getAttribute("error");
    String urlBase = (String) request.getAttribute("urlBase");
    String usuario = (String) request.getAttribute("usuario");
    if (urlBase == null) {
        urlBase = "http://localhost:8080";
    }
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Conversor Monster</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Segoe UI', Tahoma, sans-serif;
            background: #121212; color: #fff;
            display: flex; align-items: center; justify-content: center;
            min-height: 100vh;
        }
        .card {
            background: #1a1a1a; border: 1px solid #e0e0fa; border-radius: 12px;
            padding: 36px 40px; width: 380px;
            box-shadow: 0 12px 32px rgba(91, 136, 229, 0.25);
        }
        h1 { font-size: 20px; text-align: center; margin-bottom: 24px; }
        label { display: block; font-size: 13px; font-weight: bold; margin: 14px 0 6px; }
        input {
            width: 100%; padding: 12px; border-radius: 8px; border: 1px solid #404040;
            background: #2a2a2a; color: #fff; font-size: 14px;
        }
        input:focus { outline: none; border-color: #5b88e5; }
        button {
            width: 100%; margin-top: 24px; padding: 12px; border: none; border-radius: 8px;
            background: #5b88e5; color: #fff; font-size: 15px; font-weight: bold; cursor: pointer;
        }
        button:hover { background: #4a75c9; }
        .error {
            margin-top: 16px; padding: 10px; border-radius: 8px;
            background: rgba(255, 77, 77, 0.15); border: 1px solid #ff4d4d;
            color: #ff8080; font-size: 13px; text-align: center;
        }
        .muted { margin-top: 14px; font-size: 12px; color: #888; text-align: center; }
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
<main class="login-container">
    <section class="form-section">
        <p class="muted">MONSTER · REST JAVA · GR09</p>
        <h1>Ingreso de usuarios</h1>
        <form action="<%= ctx %>/login" method="post" autocomplete="off">
            <label for="urlBase">Servidor</label>
            <input type="url" id="urlBase" name="urlBase" value="<%= escapeHtml(urlBase) %>" required />

            <label for="usuario">Usuario</label>
            <input type="text" id="usuario" name="usuario" value="<%= escapeHtml(usuario == null ? "Monster" : usuario) %>" required />

            <label for="clave">Clave</label>
            <input type="password" id="clave" name="clave" value="Monster9" required />

            <button type="submit">INICIAR SESION</button>
        </form>
        <% if (error != null) { %>
            <div class="error"><%= escapeHtml(error) %></div>
        <% } %>
        <p class="muted">Usuario por defecto: Monster / Monster9</p>
    </section>
    <div class="image-section">
        <img class="monster-image" src="<%= ctx %>/Imagenes/imagen_inicio_sesion.jpg" alt="Identidad visual Monster" />
    </div>
</main>
</body>
</html>
