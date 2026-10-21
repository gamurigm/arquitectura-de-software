package ec.edu.monster;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public final class SoapSimulado implements AutoCloseable {
    private final HttpServer servidor;
    private final ExecutorService ejecutor = Executors.newCachedThreadPool();
    public final AtomicInteger llamadas = new AtomicInteger();
    public volatile int resultadoLogin = 1;
    public volatile int resultadoDeposito = 1;
    public volatile boolean fallar;
    public volatile long demora;
    public volatile BigDecimal ultimoImporte;
    public volatile String ultimaCuenta;
    public volatile String ultimaClave;

    public SoapSimulado() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/banco", this::responder);
        servidor.setExecutor(ejecutor);
        servidor.start();
    }

    public String endpoint() {
        return "http://127.0.0.1:" + servidor.getAddress().getPort() + "/banco";
    }

    private void responder(HttpExchange intercambio) throws IOException {
        llamadas.incrementAndGet();
        try {
            if (demora > 0) {
                Thread.sleep(demora);
            }
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document documento = fabrica.newDocumentBuilder().parse(
                    new ByteArrayInputStream(intercambio.getRequestBody().readAllBytes()));
            String operacion = documento.getElementsByTagNameNS(
                    "http://schemas.xmlsoap.org/soap/envelope/", "Body")
                    .item(0).getFirstChild().getLocalName();
            String contenido;
            int estado = 200;
            if (fallar) {
                estado = 500;
                contenido = "<soap:Fault><faultcode>soap:Server</faultcode>"
                        + "<faultstring>Fallo de prueba</faultstring></soap:Fault>";
            } else {
                contenido = switch (operacion) {
                    case "login" -> {
                        ultimaClave = texto(documento, "contrasena");
                        yield respuesta("login", "<login>" + resultadoLogin + "</login>");
                    }
                    case "regDeposito" -> {
                        ultimaCuenta = texto(documento, "cuenta");
                        ultimoImporte = new BigDecimal(texto(documento, "importe"));
                        yield respuesta("regDeposito", "<resultado>" + resultadoDeposito + "</resultado>");
                    }
                    case "traerMovimientos" -> {
                        ultimaCuenta = texto(documento, "cuenta");
                        yield respuesta("traerMovimientos", "<movimiento><accion>INGRESO</accion>"
                                + "<cuenta>00100001</cuenta><fecha>2026-10-21T10:30:00</fecha>"
                                + "<importe>1234567890.19</importe><nromov>3</nromov>"
                                + "<tipo>DEPOSITO</tipo></movimiento>");
                    }
                    default -> throw new IllegalArgumentException(operacion);
                };
            }
            byte[] cuerpo = ("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "<soap:Body>" + contenido + "</soap:Body></soap:Envelope>")
                    .getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().set("Content-Type", "text/xml; charset=utf-8");
            intercambio.sendResponseHeaders(estado, cuerpo.length);
            intercambio.getResponseBody().write(cuerpo);
        } catch (Exception error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            intercambio.close();
        } finally {
            intercambio.close();
        }
    }

    private static String respuesta(String operacion, String contenido) {
        return "<ns:" + operacion + "Response xmlns:ns=\"http://ws.monster.edu.ec/\">"
                + contenido + "</ns:" + operacion + "Response>";
    }

    private static String texto(Document documento, String etiqueta) {
        return documento.getElementsByTagName(etiqueta).item(0).getTextContent();
    }

    @Override
    public void close() {
        servidor.stop(0);
        ejecutor.shutdownNow();
    }
}
