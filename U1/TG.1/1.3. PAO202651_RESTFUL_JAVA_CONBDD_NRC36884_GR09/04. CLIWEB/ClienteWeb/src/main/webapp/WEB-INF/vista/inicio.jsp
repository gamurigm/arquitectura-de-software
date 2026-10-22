<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Banca personal | EurekaBank</title>
    <style>
        :root { color-scheme: light; font: 16px system-ui,sans-serif; color: #17365d; background: #edf4f8; }
        * { box-sizing: border-box; }
        body { margin: 0; }
        header { display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; gap: 14px; padding: 22px max(20px,calc((100% - 1120px)/2)); color: #fff; background: #102d57; }
        header h1 { margin: 0; font-size: 1.4rem; letter-spacing: .05em; }
        header form { margin: 0; }
        main { width: min(1120px,calc(100% - 32px)); margin: 28px auto; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(min(100%,310px),1fr)); gap: 18px; }
        section { padding: 22px; background: white; border: 1px solid #dce5ed; border-radius: 15px; box-shadow: 0 8px 24px #1639560c; }
        h2 { margin-top: 0; }
        label { display: block; margin: 14px 0 5px; font-weight: 650; }
        input { width: 100%; min-height: 44px; padding: 9px 11px; border: 1px solid #b8c8d8; border-radius: 8px; font: inherit; }
        input:focus,button:focus-visible { outline: 3px solid #49bfca; outline-offset: 2px; }
        button { min-height: 44px; margin-top: 16px; padding: 9px 15px; color: white; background: #123b72; border: 0; border-radius: 8px; font: inherit; font-weight: 650; cursor: pointer; }
        header button { margin: 0; color: #14345c; background: #fff; }
        .status { padding: 12px 14px; border-radius: 8px; background: #eff7f8; }
        .error { color: #811b36; background: #fff0f2; }
        .tabla { overflow-x: auto; margin-top: 18px; background: white; border-radius: 12px; }
        table { width: 100%; border-collapse: collapse; white-space: nowrap; }
        caption { padding: 12px; text-align: left; font-weight: 700; }
        th,td { padding: 12px; text-align: left; border-bottom: 1px solid #e1e8ef; }
        th { color: #52647c; background: #f5f8fa; }
        @media (max-width:560px) { header { align-items: flex-start; flex-direction: column; } section { padding: 17px; } }
    </style>
</head>
<body>
<header>
    <h1>EUREKABANK</h1>
    <span>Sesión de <strong><c:out value="${usuario}"/></strong></span>
    <form action="${pageContext.request.contextPath}/logout" method="post"><input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>"><button type="submit">Cerrar sesión</button></form>
</header>
<main>
    <c:if test="${not empty mensaje}"><p class="status ${error ? 'error' : ''}" role="status"><c:out value="${mensaje}"/></p></c:if>
    <div class="grid">
        <section aria-labelledby="consulta-titulo">
            <h2 id="consulta-titulo">Consultar movimientos</h2>
            <form action="${pageContext.request.contextPath}/movimientos" method="get">
                <label for="cuenta-consulta">Número de cuenta</label>
                <input id="cuenta-consulta" name="cuenta" inputmode="numeric" pattern="[0-9]{8}" maxlength="8" value="<c:out value='${cuenta}'/>" required>
                <button type="submit">Ver movimientos</button>
            </form>
        </section>
        <section aria-labelledby="deposito-titulo">
            <h2 id="deposito-titulo">Registrar depósito</h2>
            <form action="${pageContext.request.contextPath}/deposito" method="post">
                <label for="cuenta-deposito">Número de cuenta</label>
                <input id="cuenta-deposito" name="cuenta" inputmode="numeric" pattern="[0-9]{8}" maxlength="8" value="<c:out value='${cuenta}'/>" required>
                <label for="importe">Importe en dólares</label>
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
                <input id="cuenta-origen" name="cuentaOrigen" inputmode="numeric" pattern="[0-9]{8}" maxlength="8" required>
                <label for="cuenta-destino">Cuenta de destino</label>
                <input id="cuenta-destino" name="cuentaDestino" inputmode="numeric" pattern="[0-9]{8}" maxlength="8" value="<c:out value='${cuenta}'/>" required>
                <label for="importe-transferencia">Importe en dólares</label>
                <input id="importe-transferencia" name="importe" inputmode="decimal" type="number" min="0.01" max="9999999999.99" step="0.01" required>
                <button type="submit">Confirmar transferencia</button>
            </form>
        </section>
    </div>
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
