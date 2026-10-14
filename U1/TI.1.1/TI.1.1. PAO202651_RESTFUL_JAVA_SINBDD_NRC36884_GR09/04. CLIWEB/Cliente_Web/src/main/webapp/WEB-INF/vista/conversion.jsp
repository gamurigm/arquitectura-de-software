<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%! private String escapeHtml(String value) {
    if (value == null) return "";
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
} %>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    @SuppressWarnings("unchecked")
    Map<String, List<String>> catalogo = (Map<String, List<String>>) request.getAttribute("catalogo");
    if (catalogo == null) {
        catalogo = new LinkedHashMap<>();
        catalogo.put("longitud", Arrays.asList("milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"));
        catalogo.put("masa", Arrays.asList("miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"));
        catalogo.put("temperatura", Arrays.asList("celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"));
    }
    String magnitud = (String) request.getAttribute("magnitud");
    if (magnitud == null) {
        magnitud = "longitud";
    }
    String origen = (String) request.getAttribute("origen");
    String destino = (String) request.getAttribute("destino");
    String valor = (String) request.getAttribute("valor");
    Object resultado = request.getAttribute("resultado");
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();

    StringBuilder json = new StringBuilder("{");
    boolean primero = true;
    for (Map.Entry<String, List<String>> entrada : catalogo.entrySet()) {
        if (!primero) {
            json.append(",");
        }
        primero = false;
        json.append("\"").append(entrada.getKey()).append("\":[");
        boolean primerUnidad = true;
        for (String unidad : entrada.getValue()) {
            if (!primerUnidad) {
                json.append(",");
            }
            primerUnidad = false;
            json.append("\"").append(unidad).append("\"");
        }
        json.append("]");
    }
    json.append("}");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Conversor Monster</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Segoe UI', Tahoma, sans-serif;
            background: #121212; color: #fff;
            display: flex; align-items: center; justify-content: center;
            min-height: 100vh; padding: 20px;
        }
        .card {
            background: #1a1a1a; border: 1px solid #e0e0fa; border-radius: 12px;
            padding: 32px 40px; width: 560px;
            box-shadow: 0 12px 32px rgba(91, 136, 229, 0.25);
        }
        .top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        h1 { font-size: 19px; }
        .usuario { font-size: 13px; color: #9bb0e5; }
        .usuario a { color: #ff8080; text-decoration: none; margin-left: 12px; }
        label { display: block; font-size: 13px; font-weight: bold; margin: 14px 0 6px; }
        select, input {
            width: 100%; padding: 11px; border-radius: 8px; border: 1px solid #404040;
            background: #2a2a2a; color: #fff; font-size: 14px;
        }
        select:focus, input:focus { outline: none; border-color: #5b88e5; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 18px; }
        button {
            width: 100%; margin-top: 24px; padding: 12px; border: none; border-radius: 8px;
            background: #5b88e5; color: #fff; font-size: 15px; font-weight: bold; cursor: pointer;
        }
        button:hover { background: #4a75c9; }
        .resultado {
            margin-top: 20px; padding: 14px; border-radius: 8px; text-align: center;
            background: rgba(126, 211, 126, 0.12); border: 1px solid #7ed37e; color: #b7f0b7;
        }
        .resultado .valor { font-size: 26px; font-weight: bold; color: #fff; }
        .error {
            margin-top: 20px; padding: 12px; border-radius: 8px; text-align: center;
            background: rgba(255, 77, 77, 0.15); border: 1px solid #ff4d4d; color: #ff8080; font-size: 14px;
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
<div class="card">
    <div class="top">
        <h1>Conversor de unidades</h1>
        <span class="usuario">
            <%= escapeHtml(usuario) %>
            <a href="<%= ctx %>/logout">Cerrar sesion</a>
        </span>
    </div>

    <form action="<%= ctx %>/convertir" method="post" id="formConversion">
        <label for="magnitud">Magnitud</label>
        <select id="magnitud" name="magnitud" required>
            <option value="longitud" <%= "longitud".equals(magnitud) ? "selected" : "" %>>Longitud</option>
            <option value="masa" <%= "masa".equals(magnitud) ? "selected" : "" %>>Masa</option>
            <option value="temperatura" <%= "temperatura".equals(magnitud) ? "selected" : "" %>>Temperatura</option>
        </select>

        <div class="grid">
            <div>
                <label for="origen">Unidad de origen</label>
                <select id="origen" name="origen" required></select>
            </div>
            <div>
                <label for="destino">Unidad de destino</label>
                <select id="destino" name="destino" required></select>
            </div>
        </div>

        <label for="valor">Valor a convertir</label>
        <input type="number" step="any" id="valor" name="valor" value="<%= escapeHtml(valor) %>" required />

        <button type="submit">CONVERTIR</button>
    </form>

    <% if (error != null) { %>
        <div class="error"><%= escapeHtml(error) %></div>
    <% } else if (mensaje != null) { %>
        <div class="resultado">
            <div class="valor"><%= escapeHtml(String.valueOf(resultado)) %></div>
            <div><%= escapeHtml(mensaje) %></div>
        </div>
    <% } %>
</div>

<script>
    const catalogo = <%= json %>;
    const magnitudSel = document.getElementById('magnitud');
    const origenSel = document.getElementById('origen');
    const destinoSel = document.getElementById('destino');
    const origenPre = '<%= origen != null && origen.matches("[a-z]+") ? origen : "" %>';
    const destinoPre = '<%= destino != null && destino.matches("[a-z]+") ? destino : "" %>';
    let primeraCarga = true;

    function llenar(select, lista, preseleccion) {
        select.innerHTML = '';
        lista.forEach(function (unidad, indice) {
            const opcion = document.createElement('option');
            opcion.value = unidad;
            opcion.textContent = unidad;
            if (unidad === preseleccion) {
                opcion.selected = true;
            }
            if (!preseleccion && indice === 0) {
                opcion.selected = true;
            }
            select.appendChild(opcion);
        });
    }

    function refrescar() {
        const lista = catalogo[magnitudSel.value] || [];
        llenar(origenSel, lista, primeraCarga ? origenPre : '');
        llenar(destinoSel, lista, primeraCarga ? destinoPre : '');
        primeraCarga = false;
    }

    magnitudSel.addEventListener('change', refrescar);
    refrescar();
    document.getElementById('formConversion').addEventListener('submit', function (event) {
        const texto = document.getElementById('valor').value.trim();
        const valor = Number(texto);
        if (texto === '' || !Number.isFinite(valor) || (magnitudSel.value === 'masa' && valor < 0)) {
            event.preventDefault();
            document.getElementById('valor').setCustomValidity(
                magnitudSel.value === 'masa' && valor < 0
                    ? 'La masa no puede ser negativa.'
                    : 'Ingrese un número finito válido.');
            document.getElementById('valor').reportValidity();
        }
    });
    document.getElementById('valor').addEventListener('input', event => event.target.setCustomValidity(''));
</script>
</body>
</html>
