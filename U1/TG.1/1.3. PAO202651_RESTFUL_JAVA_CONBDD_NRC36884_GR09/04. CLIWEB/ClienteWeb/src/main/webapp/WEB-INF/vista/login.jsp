<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Acceso | EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/eureka.css">
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
