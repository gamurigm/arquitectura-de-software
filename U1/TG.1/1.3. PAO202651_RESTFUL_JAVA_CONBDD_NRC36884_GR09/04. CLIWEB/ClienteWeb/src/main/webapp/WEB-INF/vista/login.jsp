<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Acceso | EurekaBank</title>
    <style>
        :root { color-scheme: light; font: 16px system-ui, sans-serif; color: #17365d; background: #edf4f8; }
        * { box-sizing: border-box; }
        body { min-height: 100vh; margin: 0; display: grid; place-items: center; padding: 24px; background: linear-gradient(140deg,#0c2145,#087f91); }
        main { width: min(100%, 430px); padding: 36px; background: #fff; border-radius: 20px; box-shadow: 0 18px 50px #08183255; }
        h1 { margin: 0; font-size: 1.8rem; letter-spacing: .06em; }
        p { color: #52647c; }
        label { display: block; margin: 18px 0 6px; font-weight: 650; }
        input { width: 100%; min-height: 46px; padding: 10px 12px; border: 1px solid #b8c8d8; border-radius: 9px; font: inherit; }
        input:focus, button:focus-visible { outline: 3px solid #49bfca; outline-offset: 2px; }
        button { width: 100%; min-height: 48px; margin-top: 24px; color: #fff; background: #123b72; border: 0; border-radius: 9px; font: inherit; font-weight: 700; cursor: pointer; }
        .error { padding: 10px 12px; color: #811b36; background: #fff0f2; border-radius: 8px; }
    </style>
</head>
<body>
<main>
    <h1>EUREKABANK</h1>
    <p>Banca personal · acceso seguro</p>
    <c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="usuario">Usuario</label>
        <input id="usuario" name="usuario" autocomplete="username" maxlength="30" required autofocus>
        <label for="contrasena">Contraseña</label>
        <input id="contrasena" name="contrasena" type="password" autocomplete="current-password" required>
        <button type="submit">Iniciar sesión</button>
    </form>
</main>
</body>
</html>
