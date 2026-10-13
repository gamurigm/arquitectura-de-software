<%-- 
    Document   : conversor
    Created on : 15 abr 2026, 14:33:20
    Author     : Soto
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String token = (String) session.getAttribute("miToken");
    if (token == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Panel de Conversiones - ESPE</title>
    <style>
        /* Estilos generales */
        body { 
            font-family: Arial, sans-serif; 
            background-color: #121212; 
            color: white; 
            display: flex; 
            justify-content: center; 
            align-items: center; 
            height: 100vh; 
            margin: 0; 
        }

        /* Contenedor principal con el borde sutil */
        .converter-box { 
            background-color: #111111; 
            padding: 40px; 
            border-radius: 4px; 
            border: 2px solid #e0e0fa; /* Borde clarito de tu imagen */
            width: 450px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.8);
        }

        h3 {
            margin-top: 0;
            font-size: 1.2em;
            margin-bottom: 10px;
        }

        /* Selector principal de arriba */
        .main-select {
            width: 60%;
            padding: 8px;
            background-color: #1a1a1a;
            color: white;
            border: 1px solid #555;
            margin-bottom: 40px;
            outline: none;
        }

        /* Contenedor de las dos columnas */
        .columns-container {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            margin-bottom: 30px;
        }

        /* Cada columna (Origen y Destino) */
        .col {
            display: flex;
            flex-direction: column;
            width: 40%;
        }

        .col label {
            font-weight: bold;
            font-size: 0.9em;
            margin-bottom: 15px;
        }

        .col select, .col input {
            background-color: #111;
            color: white;
            border: 1px solid #fff;
            padding: 8px;
            margin-bottom: 15px;
            outline: none;
        }

        /* Columna del medio para la mano */
        .col-center {
            width: 15%;
            display: flex;
            justify-content: center;
            align-items: center;
            padding-bottom: 25px; /* Para alinear con los inputs */
        }

        .hand-img {
            width: 40px; /* Ajusta el tamaño de tu imagen aquí */
        }

        /* Botón azul gigante */
        button { 
            background-color: #5b88e5; 
            color: white; 
            cursor: pointer; 
            font-weight: bold; 
            font-size: 1.1em;
            width: 100%;
            padding: 15px;
            border: none;
        }

        button:hover { 
            background-color: #4a75c9; 
        }

        .error { 
            color: #ff4d4d; 
            margin-top: 15px; 
            font-weight: bold; 
            text-align: center;
        }
        
        .logout {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #888;
            text-decoration: none;
            font-size: 0.9em;
        }
    </style>
    
    <script>
        const dicUnidades = {
            longitud: ["centimetros", "metros", "pies", "yardas", "millas"],
            masa: ["gramos", "kilogramos", "libras", "quintales"],
            temperatura: ["fahrenheit", "kelvin", "celsius", "newton", "reaumur"]
        };

        function actualizarListas() {
            const magnitud = document.getElementById("cmbTipo").value;
            const listaOrigen = document.getElementById("listaOrigen");
            const listaDestino = document.getElementById("listaDestino");
            
            listaOrigen.innerHTML = "";
            listaDestino.innerHTML = "";

            dicUnidades[magnitud].forEach(unidad => {
                listaOrigen.add(new Option(unidad, unidad));
                listaDestino.add(new Option(unidad, unidad));
            });
            
            // Limpiamos los inputs al cambiar de magnitud
            document.getElementById("txtValor").value = "";
            document.getElementById("txtResultado").value = "";
            document.getElementById("errorTxt").innerText = "";
        }

        window.onload = function() {
            actualizarListas();

            document.getElementById("formConversor").addEventListener("submit", async function(e) {
                e.preventDefault();

                const formData = new URLSearchParams(new FormData(this));

                try {
                    const response = await fetch('ConversorServlet', {
                        method: 'POST',
                        body: formData
                    });
                    
                    const texto = await response.text();

                    document.getElementById("errorTxt").innerText = "";
                    document.getElementById("txtResultado").value = "";

                    if (texto === "ERROR_SESSION") {
                        window.location.href = "index.jsp";
                    } else if (texto.startsWith("ERROR")) {
                        document.getElementById("errorTxt").innerText = texto.replace("ERROR:", "");
                    } else {
                        // Aquí metemos el resultado en la cajita derecha
                        document.getElementById("txtResultado").value = texto;
                    }

                } catch (error) {
                    document.getElementById("errorTxt").innerText = "Error conectando al servidor.";
                }
            });
        };
    </script>
</head>
<body>
    <div class="converter-box">
        
        <form id="formConversor">
            <h3>Elija el tipo de unidad para la conversión</h3>
            
            <select name="cmbTipo" id="cmbTipo" onchange="actualizarListas()" class="main-select">
                <option value="longitud">Longitud</option>
                <option value="masa">Masa</option>
                <option value="temperatura">Temperatura</option>
            </select>

            <div class="columns-container">
                <div class="col">
                    <label>Unidad de origen</label>
                    <select name="txtOrigen" id="listaOrigen"></select>
                    <input type="number" step="any" name="txtValor" id="txtValor" required>
                </div>

                <div class="col-center">
                    <img src="Imagenes/flecha40px.png" alt=""/>
                </div>

                <div class="col">
                    <label>Unidad de destino</label>
                    <select name="txtDestino" id="listaDestino"></select>
                    <input type="text" id="txtResultado" readonly>
                </div>
            </div>

            <button type="submit">CONVERTIR</button>
        </form>

        <div id="errorTxt" class="error"></div>
        <a href="index.jsp" class="logout">Cerrar Sesión</a>
    </div>
</body>
</html>