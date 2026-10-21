<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Iniciar Sesión</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <style>
            body {
                font-family: Arial, sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
                background-image: url('./fondo_monster.jpg');
                background-size: cover;
                background-position: center;
            }
            .container {
                width: 650px;
                height: 500px;
                padding: 30px;
                background-color: rgba(255, 255, 255, 0.8);
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                background-image: url('./monster1.jpeg');
                background-size: cover;
                background-position: center;
                background-repeat: no-repeat;
                display: flex;
                flex-direction: column;
                justify-content: center;
            }
            h1 {
                text-align: center;
                margin-bottom: 20px;
                color: #4885A6;
                background-color: rgba(255, 255, 255, 0.6);
                padding: 10px;
                border-radius: 5px;
            }
            #loginForm {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 10px;
            }
            input[type="text"],
            input[type="password"] {
                width: 150%;
                max-width: 300px;
                padding: 10px;
                margin: 5px 0;
                box-sizing: border-box;
            }
            input[type="submit"],
            input[type="button"] {
                width: 150%;
                max-width: 300px;
                padding: 10px;
                margin: 5px 0;
                box-sizing: border-box;
                background-color: #4885A6;
                color: white;
                border: none;
                cursor: pointer;
            }
            input[type="submit"]:hover,
            input[type="button"]:hover {
                background-color: #35667a;
            }
            label {
                align-self: flex-start;
                margin-left: 10%;
            }
            #message {
                margin-top: 15px;
                font-weight: bold;
                text-align: center;
                color: #4885A6;
                background-color: rgba(255, 255, 255, 0.6);
                padding: 10px;
                border-radius: 5px;
                display: none; /* Oculto por defecto */
            }
            .button-group {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 10px;
                margin-top: 10px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Iniciar Sesión</h1>
            <form id="loginForm">
                <input type="text" id="username" name="username" required placeholder="Usuario">
                <input type="password" id="password" name="password" required placeholder="Contraseña">
                <div class="button-group">
                    <input type="submit" value="Iniciar Sesión">
                    <input type="button" value="Registrarse" onclick="window.location.href = 'Registrarse.jsp';">
                </div>
            </form>
            <div id="message"></div>
        </div>
        <script>
            $(document).ready(function () {
                $('#loginForm').submit(function (e) {
                    e.preventDefault();
                    var username = $('#username').val();
                    var password = $('#password').val();

                    $.ajax({
                        url: 'http://localhost:8080/WSConversionUnidades/webresources/conversionUnidades/login',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({username: username, password: password}),
                        success: function (response) {
                            localStorage.setItem('username', username); // Almacenar el nombre de usuario en localStorage
                            $('#message').text('Inicio de sesión exitoso. Redirigiendo...');
                            $('#message').fadeIn(); // Mostrar el mensaje con animación
                            setTimeout(function () {
                                window.location.href = 'ConversionT.jsp';
                            }, 1500);
                        },
                        error: function (xhr, status, error) {
                            $('#message').text('Error en el inicio de sesión: ' );
                            $('#message').fadeIn(); // Mostrar el mensaje con animación
                        }
                    });
                });
            });
        </script>
    </body>
</html>
