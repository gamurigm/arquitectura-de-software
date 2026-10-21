<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registrarse</title>
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
                position: relative; /* Necesario para el posicionamiento absoluto del botón */
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
            #registerForm {
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
            /* Estilos para el botón de regreso */
            .back-button {
                position: absolute;
                top: 10px;
                left: 10px;
                z-index: 1000; /* Asegura que esté por encima del contenido */
                cursor: pointer;
            }
            .back-button img {
                width: 30px; /* Ajusta el tamaño de la imagen según necesites */
                height: auto;
            }
        </style>
    </head>
    <body>
        <div class="container"> 
            <h1>Registrarse</h1>
            <!-- Botón de regreso -->
            <div class="back-button" onclick="window.location.href = 'IniciarSesion.jsp';">
                <img src="regresar.png" alt="Volver a Inicio de Sesión">
            </div>
            <form id="registerForm">
                <input type="text" placeholder="Nombre de usuario" id="username" name="username" required>
                <input type="password" placeholder="Contraseña" id="password" name="password" required>
                <div class="button-group">
                    <input type="submit" value="Registrarse">
                </div>
            </form>
            <div id="message"></div>
        </div>
        <script>
            $(document).ready(function () {
                $('#registerForm').submit(function (e) {
                    e.preventDefault();
                    var username = $('#username').val();
                    var password = $('#password').val();

                    $.ajax({
                        url: 'http://localhost:8080/WSConversionUnidades/webresources/conversionUnidades/register',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({username: username, password: password}),
                        success: function (response) {
                            $('#message').text('Registro exitoso. Por favor, inicie sesión.');
                            $('#message').fadeIn(); // Mostrar el mensaje con animación
                            setTimeout(function () {
                                window.location.href = 'IniciarSesion.jsp';
                            }, 1500);
                        },
                        error: function (xhr, status, error) {
                            $('#message').text('Error en el registro: ' + xhr.responseJSON.message);
                            $('#message').fadeIn(); // Mostrar el mensaje con animación
                        }
                    });
                });
            });
        </script>
    </body>
</html>
