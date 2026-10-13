<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String error = (String) request.getAttribute("error");
    String baseUrl = (String) request.getAttribute("baseUrl");
    if (baseUrl == null) {
        baseUrl = "http://localhost:8080";
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
    </style>
</head>
<body>
<div class="card">
    <h1>Ingreso de usuarios</h1>
    <form action="<%= ctx %>/login" method="post" autocomplete="off">
        <label for="baseUrl">Servidor</label>
        <input type="text" id="baseUrl" name="baseUrl" value="<%= baseUrl %>" />

        <label for="usuario">Usuario</label>
        <input type="text" id="usuario" name="usuario" value="monster" />

        <label for="clave">Clave</label>
        <input type="password" id="clave" name="clave" value="monster9" />

        <button type="submit">INICIAR SESION</button>
    </form>
    <% if (error != null) { %>
        <div class="error"><%= error %></div>
    <% } %>
    <p class="muted">Usuario por defecto: monster / monster9</p>
</div>
</body>
</html>
