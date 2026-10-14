<%-- 
    Document   : conversor
    Created on : 15 abr 2026, 14:33:20
    Author     : Soto
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String token = (String) session.getAttribute("miToken");
    if (token == null) {
        response.sendRedirect("inicio.jsp");
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
    
    <script>
        const dicUnidades = {
            longitud: ["milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"],
            masa: ["miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"],
            temperatura: ["celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"]
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
                const valorTexto = document.getElementById("txtValor").value.trim();
                const valor = Number(valorTexto);
                const magnitud = document.getElementById("cmbTipo").value;
                if (valorTexto === "" || !Number.isFinite(valor)
                        || (magnitud === "masa" && valor < 0)) {
                    document.getElementById("errorTxt").innerText = magnitud === "masa" && valor < 0
                        ? "La masa no puede ser negativa."
                        : "Ingrese un número finito válido.";
                    return;
                }

                try {
                    const response = await fetch('convertir', {
                        method: 'POST',
                        body: formData
                    });
                    
                    const texto = await response.text();

                    document.getElementById("errorTxt").innerText = "";
                    document.getElementById("txtResultado").value = "";

                    if (texto === "ERROR_SESSION") {
                        window.location.href = "inicio.jsp";
                    } else if (texto.startsWith("ERROR")) {
                        document.getElementById("errorTxt").innerText = texto.replace("ERROR:", "");
                    } else {
                        if (!Number.isFinite(Number(texto))) {
                            document.getElementById("errorTxt").innerText = "El servidor devolvió un resultado inválido.";
                            return;
                        }
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
        <p class="muted">MONSTER · SOAP JAVA · GR09</p>
        <form id="formConversor">
            <h1>Conversor de unidades</h1>
            <p class="muted">Elija el tipo de unidad para comenzar.</p>
            
            <select name="cmbTipo" id="cmbTipo" onchange="actualizarListas()" class="main-select" required>
                <option value="longitud">Longitud</option>
                <option value="masa">Masa</option>
                <option value="temperatura">Temperatura</option>
            </select>

            <div class="columns-container">
                <div class="col">
                    <label>Unidad de origen</label>
                    <select name="txtOrigen" id="listaOrigen" required></select>
                    <input type="number" step="any" name="txtValor" id="txtValor" required>
                </div>

                <div class="col-center">
                    <img src="Imagenes/flecha40px.png" alt=""/>
                </div>

                <div class="col">
                    <label>Unidad de destino</label>
                    <select name="txtDestino" id="listaDestino" required></select>
                    <input type="text" id="txtResultado" readonly>
                </div>
            </div>

            <button type="submit">CONVERTIR</button>
        </form>

        <div id="errorTxt" class="error"></div>
        <a href="cerrar-sesion" class="logout">Cerrar Sesión</a>
    </div>
</body>
</html>
