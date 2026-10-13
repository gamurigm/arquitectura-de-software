<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Conversión de Temperatura</title>
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
                position: relative;
                width: 650px;
                height: 500px;
                padding: 20px;
                background-color: rgba(255, 255, 255, 0.8);
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                background-image: url('monster2.jpeg');
                background-size: cover;
                background-position: center;
                display: flex;
                flex-direction: column; /* Alinear elementos en columna */
                justify-content: center; /* Centrar verticalmente */
                align-items: center; /* Centrar horizontalmente */
            }
            h1 {
                text-align: center;
                position: relative;
                color: #4885A6;
                z-index: 1;
            }
            h1::before {
                content: '';
                position: absolute;
                top: 0;
                left: 50%;
                transform: translateX(-50%);
                width: 100%;
                background-color: rgba(255, 255, 255, 0.8);
                padding: 10px;
                border-radius: 8px;
                z-index: -1;
                height: 35px;
            }
            input, button {
                width: 100%;
                max-width: 300px; /* Ancho máximo para los botones */
                padding: 10px;
                margin: 10px 0;
                box-sizing: border-box;
            }
            #resultado {
                margin-top: 20px;
                font-weight: bold;
                text-align: center;
                color: #4885A6;
                background-color: rgba(255, 255, 255, 0.8); /* Fondo semitransparente blanco */
                padding: 10px;
                border-radius: 8px;
                width: 100%;
                max-width: 300px; /* Ancho máximo para alineación con otros elementos */
            }
            #usernameDisplay {
                text-align: center;
                margin-bottom: 20px;
                font-weight: bold;
                color: white; /* Color de texto blanco */
                background-color: rgba(0, 0, 0, 0.7); /* Fondo semitransparente negro */
                padding: 10px;
                border-radius: 8px;
                width: 100%;
                max-width: 300px; /* Ancho máximo para alineación con otros elementos */
            }
            button {
                background-color: #4885A6;
                color: white;
                border: none;
                cursor: pointer;
            }
            button:hover {
                background-color: #35667a;
            }
            /* Estilos para el botón de regreso en la esquina superior derecha */
            .back-button {
                position: absolute;
                top: 10px;
                right: 10px;
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
            <!-- Botón de regreso en la esquina superior derecha -->
            <div class="back-button" onclick="window.location.href = 'IniciarSesion.jsp';">
                <img src="salir4.png" alt="Volver a Inicio de Sesión">
            </div>
            <div id="usernameDisplay">Bienvenido, Monster</div>
            <h1>Conversión de Temperatura</h1>
            <input type="text" id="temperatura" placeholder="Ingrese temperatura">
            <button onclick="convertir('celsiusToFahrenheit')">Celsius a Fahrenheit</button>
            <button onclick="convertir('fahrenheitToCelsius')">Fahrenheit a Celsius</button>
            <button onclick="convertir('celsiusToKelvin')">Celsius a Kelvin</button>
            <button onclick="convertir('kelvinToCelsius')">Kelvin a Celsius</button>
            <div id="resultado">Resultado: </div>
        </div>

        <script>
            $(document).ready(function () {
                var username = localStorage.getItem('username');
                if (username) {
                    $('#usernameDisplay').text('Bienvenido, ' + username);
                } else {
                    $('#usernameDisplay').text('Usuario no identificado');
                }
            });

            function convertir(tipo) {
                var temperatura = $('#temperatura').val();
                if (temperatura === '') {
                    alert('Por favor, ingrese una temperatura.');
                    return;
                }

                if (isNaN(temperatura)) {
                    alert('Por favor, ingrese un número válido.');
                    return;
                }

                $('#resultado').text('Convirtiendo...');

                var url = 'http://localhost:8080/WSConversionUnidades/webresources/conversionUnidades/' + tipo + '/' + temperatura;

                $.ajax({
                    url: url,
                    type: 'GET',
                    success: function (response) {
                        var conversionTexto = '';
                        switch (tipo) {
                            case 'celsiusToFahrenheit':
                                conversionTexto = 'Celsius a Fahrenheit';
                                break;
                            case 'fahrenheitToCelsius':
                                conversionTexto = 'Fahrenheit a Celsius';
                                break;
                            case 'celsiusToKelvin':
                                conversionTexto = 'Celsius a Kelvin';
                                break;
                            case 'kelvinToCelsius':
                                conversionTexto = 'Kelvin a Celsius';
                                break;
                        }
                        $('#resultado').text('Resultado: ' + conversionTexto + ' (' + response + ')');
                    },
                    error: function (xhr, status, error) {
                        $('#resultado').text('Error en la conversión: ' + error);
                    }
                });
            }
        </script>

    </body>
</html>
