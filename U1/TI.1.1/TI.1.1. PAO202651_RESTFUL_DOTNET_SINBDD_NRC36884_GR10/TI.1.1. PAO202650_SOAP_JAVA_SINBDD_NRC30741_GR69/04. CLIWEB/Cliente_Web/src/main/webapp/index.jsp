<%-- 
    Document   : index
    Created on : 15 abr 2026, 14:17:06
    Author     : Soto
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Login - Conversor ESPE</title>
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
        </style>
    </head>
    <body>
        <div class="login-container">
            
            <div class="form-section">
                <h3>Ingrese sus credenciales:</h3>
                
                <form method="POST" action="LoginServlet">
                    <div class="input-group">
                        <label>Usuario:</label>
                        <input type="text" name="txtUsuario" required>
                    </div>
                    <div class="input-group">
                        <label>Clave:</label>
                        <input type="password" name="txtClave" required>
                    </div>
                    <button type="submit">INICIAR SESIÓN</button>
                </form>

                <%-- Mensaje de error si falla el login --%>
                <% if(request.getAttribute("error") != null) { %>
                    <div class="error"><%= request.getAttribute("error") %></div>
                <% } %>
            </div>

            <div class="image-section">
                <img src="Imagenes/monsterlogin.jpg" alt=""/>
            </div>

        </div>
    </body>
</html>