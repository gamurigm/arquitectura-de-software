<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Banca personal | EurekaBank</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/eureka.css">
</head>
<body>
<header>
    <h1>EUREKABANK</h1>
    <span>Sesión de <strong><c:out value="${usuario}"/></strong></span>
    <form action="${pageContext.request.contextPath}/logout" method="post"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><button type="submit">Cerrar sesión</button></form>
</header>
<main>
    <c:if test="${not empty mensaje}"><p class="status ${error ? 'error' : ''}" role="status"><c:out value="${mensaje}"/></p></c:if>
    <section class="accounts" aria-labelledby="cuentas-titulo">
        <div class="section-heading"><div><p class="eyebrow">TU DINERO, DE UN VISTAZO</p><h2 id="cuentas-titulo">Mis cuentas</h2></div><span class="account-count"><c:out value="${cuentas.size()}"/> activas</span></div>
        <div class="account-list"><c:forEach var="c" items="${cuentas}"><article class="account-card"><span class="account-icon" aria-hidden="true">EB</span><div class="account-meta"><span class="account-type"><c:out value="${c.nombreMoneda()}"/></span><strong><c:out value="${c.numero()}"/></strong><small>Cuenta activa</small></div><div class="account-balance"><span>Saldo disponible</span><strong><c:out value="${c.simbolo()}"/> <c:out value="${c.saldoFormateado()}"/></strong></div></article></c:forEach></div>
    </section>
    <div class="grid">
        <section aria-labelledby="consulta-titulo">
            <h2 id="consulta-titulo">Consultar movimientos</h2>
            <form action="${pageContext.request.contextPath}/movimientos" method="get">
                <label for="cuenta-consulta">Número de cuenta</label>
                <select id="cuenta-consulta" name="cuenta" required><c:forEach var="c" items="${cuentas}"><option value="<c:out value='${c.numero}'/>"><c:out value="${c.numero}"/> · <c:out value="${c.nombreMoneda()}"/> · <c:out value="${c.simbolo()}"/> <c:out value="${c.saldoFormateado()}"/></option></c:forEach></select>
                <button type="submit">Ver movimientos</button>
            </form>
        </section>
        <section aria-labelledby="deposito-titulo">
            <h2 id="deposito-titulo">Registrar depósito</h2>
            <form action="${pageContext.request.contextPath}/deposito" method="post">
                <label for="cuenta-deposito">Número de cuenta</label>
                <select id="cuenta-deposito" name="cuenta" required><c:forEach var="c" items="${cuentas}"><option value="<c:out value='${c.numero}'/>"><c:out value="${c.numero}"/> · <c:out value="${c.nombreMoneda()}"/> · <c:out value="${c.simbolo()}"/> <c:out value="${c.saldoFormateado()}"/></option></c:forEach></select>
                <label for="importe">Importe en la moneda de la cuenta</label>
                <input id="importe" name="importe" inputmode="decimal" type="number" min="0.01" step="0.01" required>
                <button type="submit">Confirmar depósito</button>
            </form>
        </section>
        <section aria-labelledby="transferencia-titulo">
            <h2 id="transferencia-titulo">Transferir</h2>
            <form action="${pageContext.request.contextPath}/transferencia" method="post">
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                <input type="hidden" name="claveOperacion" value="<c:out value='${claveOperacion}'/>">
                <label for="cuenta-origen">Cuenta de origen</label>
                <select id="cuenta-origen" name="cuentaOrigen" required><c:forEach var="c" items="${cuentas}"><option value="<c:out value='${c.numero}'/>" data-currency="<c:out value='${c.moneda}'/>"><c:out value="${c.numero}"/> · <c:out value="${c.nombreMoneda()}"/> · <c:out value="${c.simbolo()}"/> <c:out value="${c.saldoFormateado()}"/></option></c:forEach></select>
                <label for="cuenta-destino">Cuenta de destino · misma moneda</label>
                <select id="cuenta-destino" name="cuentaDestino" required><option value="">Seleccione cuenta de origen</option></select>
                <p id="resumen-transferencia" class="form-hint" role="status">Elige una cuenta de destino para continuar.</p>
                <label for="importe-transferencia">Importe en la moneda de la cuenta</label>
                <input id="importe-transferencia" name="importe" inputmode="decimal" type="number" min="0.01" max="9999999999.99" step="0.01" required>
                <p class="form-hint">Verifica origen, destino y saldo antes de confirmar.</p>
                <button type="submit">Confirmar transferencia</button>
            </form>
        </section>
    </div>
    <script src="${pageContext.request.contextPath}/assets/eureka.js" defer></script>
    <c:if test="${not empty movimientos}">
        <div class="tabla">
            <table>
                <caption>Movimientos de la cuenta <c:out value="${cuenta}"/></caption>
                <thead><tr><th scope="col">N.º</th><th scope="col">Fecha</th><th scope="col">Tipo</th><th scope="col">Acción</th><th scope="col">Importe</th></tr></thead>
                <tbody><c:forEach var="m" items="${movimientos}">
                    <tr><td><c:out value="${m.numero}"/></td><td><c:out value="${m.fecha}"/></td><td><c:out value="${m.tipo}"/></td><td><c:out value="${m.accion}"/></td><td><c:out value="${m.importe}"/></td></tr>
                </c:forEach></tbody>
            </table>
        </div>
    </c:if>
</main>
</body>
</html>
