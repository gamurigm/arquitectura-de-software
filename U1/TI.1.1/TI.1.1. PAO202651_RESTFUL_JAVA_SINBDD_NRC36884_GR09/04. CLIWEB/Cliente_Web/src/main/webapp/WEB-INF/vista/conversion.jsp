<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
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
        catalogo.put("longitud", Arrays.asList("centimetros", "metros", "pies", "yardas", "millas"));
        catalogo.put("masa", Arrays.asList("gramos", "kilogramos", "onzas", "libras", "quintales"));
        catalogo.put("temperatura", Arrays.asList("fahrenheit", "kelvin", "celsius", "newton", "reaumur"));
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
    </style>
</head>
<body>
<div class="card">
    <div class="top">
        <h1>Conversor de unidades</h1>
        <span class="usuario">
            <%= usuario %>
            <a href="<%= ctx %>/logout">Cerrar sesion</a>
        </span>
    </div>

    <form action="<%= ctx %>/convertir" method="post">
        <label for="magnitud">Magnitud</label>
        <select id="magnitud" name="magnitud">
            <option value="longitud" <%= "longitud".equals(magnitud) ? "selected" : "" %>>Longitud</option>
            <option value="masa" <%= "masa".equals(magnitud) ? "selected" : "" %>>Masa</option>
            <option value="temperatura" <%= "temperatura".equals(magnitud) ? "selected" : "" %>>Temperatura</option>
        </select>

        <div class="grid">
            <div>
                <label for="origen">Unidad de origen</label>
                <select id="origen" name="origen"></select>
            </div>
            <div>
                <label for="destino">Unidad de destino</label>
                <select id="destino" name="destino"></select>
            </div>
        </div>

        <label for="valor">Valor a convertir</label>
        <input type="text" id="valor" name="valor" value="<%= valor == null ? "" : valor %>" />

        <button type="submit">CONVERTIR</button>
    </form>

    <% if (error != null) { %>
        <div class="error"><%= error %></div>
    <% } else if (mensaje != null) { %>
        <div class="resultado">
            <div class="valor"><%= resultado %></div>
            <div><%= mensaje %></div>
        </div>
    <% } %>
</div>

<script>
    const catalogo = <%= json %>;
    const magnitudSel = document.getElementById('magnitud');
    const origenSel = document.getElementById('origen');
    const destinoSel = document.getElementById('destino');
    const origenPre = '<%= origen == null ? "" : origen %>';
    const destinoPre = '<%= destino == null ? "" : destino %>';
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
</script>
</body>
</html>
