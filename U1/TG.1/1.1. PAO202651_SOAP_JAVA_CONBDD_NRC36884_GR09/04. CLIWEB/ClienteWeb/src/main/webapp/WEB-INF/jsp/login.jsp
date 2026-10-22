<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Ingresar | EurekaBank</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/monster.css"></head>
<body><main class="shell shell-login"><header class="brand"><span class="brand-mark" aria-hidden="true">E</span><div><p class="eyebrow">MONSTER · BANCA</p><h1>EurekaBank</h1></div></header>
<section class="panel"><h2>Iniciar sesión</h2><p class="muted">Ingrese sus credenciales de acceso.</p>
<% if (request.getAttribute("error") != null) { %><p class="alert" role="alert"><%= ec.edu.monster.controlador.VistaHtml.escapar(request.getAttribute("error")) %></p><% } %>
<form method="post" action="${pageContext.request.contextPath}/login"><input type="hidden" name="_csrf" value="${csrf}"><label for="usuario">Usuario</label><input id="usuario" name="usuario" autocomplete="username" required maxlength="30"><label for="contrasena">Contraseña</label><input id="contrasena" type="password" name="contrasena" autocomplete="current-password" required maxlength="1024"><button type="submit">Ingresar</button></form></section>
<footer>Atención segura · EurekaBank</footer></main></body></html>
