package ec.edu.monster.service;

import ec.edu.monster.BuildConfig;
import ec.edu.monster.modelo.Movimiento;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class EurekaService {
    private static final String NS = "http://ws.monster.edu.ec/";
    private final String endpoint;

    public EurekaService() { this(BuildConfig.EUREKABANK_SOAP_URL); }

    public EurekaService(String endpoint) {
        this.endpoint = endpoint;
    }

    public String crearSesion(String usuario, String contrasena) throws IOException {
        Document respuesta = invocar("crearSesion", "<usuario>" + xml(usuario) + "</usuario><contrasena>"
                + xml(contrasena) + "</contrasena>");
        return texto(respuesta, "token");
    }

    public boolean transferir(String token, String origen, String destino, BigDecimal importe, UUID clave)
            throws IOException {
        Document respuesta = invocar("transferir", "<token>" + xml(token) + "</token><cuentaOrigen>"
                + xml(origen) + "</cuentaOrigen><cuentaDestino>" + xml(destino) + "</cuentaDestino><importe>"
                + importe.toPlainString() + "</importe><claveOperacion>" + clave + "</claveOperacion>");
        return "1".equals(texto(respuesta, "resultado"));
    }

    public void cerrarSesion(String token) throws IOException {
        invocar("cerrarSesion", "<token>" + xml(token) + "</token>");
    }

    public List<Movimiento> traerMovimientos(String cuenta) throws IOException {
        Document doc = invocar("<traerMovimientos><cuenta>" + xml(cuenta)
                + "</cuenta></traerMovimientos>");
        NodeList nodos = doc.getElementsByTagNameNS(NS, "movimiento");
        List<Movimiento> resultado = new ArrayList<>(nodos.getLength());
        for (int i = 0; i < nodos.getLength(); i++) {
            Element m = (Element) nodos.item(i);
            resultado.add(new Movimiento(texto(m, "cuenta"),
                    Integer.parseInt(texto(m, "nromov")), texto(m, "fecha"),
                    texto(m, "tipo"), texto(m, "accion"), new BigDecimal(texto(m, "importe"))));
        }
        return List.copyOf(resultado);
    }

    public boolean registrarDeposito(String cuenta, BigDecimal importe) throws IOException {
        String body = "<regDeposito><cuenta>" + xml(cuenta) + "</cuenta><importe>"
                + importe.toPlainString() + "</importe></regDeposito>";
        return "1".equals(texto(invocar(body), "resultado"));
    }

    private Document invocar(String operacion) throws IOException {
        String nombre = operacion.substring(1, operacion.indexOf('>'));
        String contenido = operacion.substring(operacion.indexOf('>') + 1, operacion.lastIndexOf("</"));
        return invocar(nombre, contenido);
    }

    private Document invocar(String nombre, String contenido) throws IOException {
        HttpURLConnection conexion = (HttpURLConnection) new URL(endpoint).openConnection();
        try {
            conexion.setRequestMethod("POST");
            conexion.setConnectTimeout(5000);
            conexion.setReadTimeout(15000);
            conexion.setDoOutput(true);
            conexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope "
                    + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                    + "<ns:" + nombre + " xmlns:ns=\"" + NS + "\">"
                    + contenido + "</ns:" + nombre
                    + "></soap:Body></soap:Envelope>";
            try (var salida = conexion.getOutputStream()) {
                salida.write(envelope.getBytes(StandardCharsets.UTF_8));
            }
            int estado = conexion.getResponseCode();
            InputStream stream = estado >= 400 ? conexion.getErrorStream() : conexion.getInputStream();
            if (stream == null) throw new IOException("EurekaBank no devolvió una respuesta.");
            try (stream) {
                DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
                fabrica.setNamespaceAware(true);
                fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                fabrica.setXIncludeAware(false);
                fabrica.setExpandEntityReferences(false);
                Document respuesta = fabrica.newDocumentBuilder().parse(stream);
                if (estado >= 400) throw new IOException("El servicio EurekaBank rechazó la operación.");
                return respuesta;
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("No se pudo interpretar la respuesta de EurekaBank.", e);
        } finally {
            conexion.disconnect();
        }
    }

    private static String texto(Node raiz, String nombre) throws IOException {
        NodeList hijos = raiz instanceof Document documento
                ? documento.getElementsByTagNameNS(NS, nombre)
                : ((Element) raiz).getElementsByTagNameNS(NS, nombre);
        if (hijos.getLength() == 0) throw new IOException("Respuesta SOAP incompleta: " + nombre);
        return hijos.item(0).getTextContent();
    }

    private static String xml(String valor) {
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
