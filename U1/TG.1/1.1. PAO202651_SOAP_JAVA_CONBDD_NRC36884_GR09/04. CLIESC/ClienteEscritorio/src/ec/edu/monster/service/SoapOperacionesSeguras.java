package ec.edu.monster.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

final class SoapOperacionesSeguras {
    private static final String SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String NS = "http://ws.monster.edu.ec/";
    private final URI endpoint;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    SoapOperacionesSeguras(String endpoint) { this.endpoint = URI.create(endpoint); }

    String crearSesion(String usuario, String contrasena) throws IOException {
        String token = textoOpcional(invocar("crearSesion", Map.of("usuario", usuario, "contrasena", contrasena)), "token");
        return token.isBlank() ? null : token;
    }

    boolean transferir(String token, String origen, String destino, BigDecimal importe, UUID clave) throws IOException {
        String resultado = texto(invocar("transferir", Map.of("token", token, "cuentaOrigen", origen,
                "cuentaDestino", destino, "importe", importe.toPlainString(), "claveOperacion", clave.toString())), "resultado");
        return "1".equals(resultado);
    }

    java.util.List<Movimiento> traerMovimientos(String token, String cuenta) throws IOException {
        Document documento = invocar("traerMovimientos", Map.of("token", token, "cuenta", cuenta));
        NodeList nodos = documento.getElementsByTagNameNS("*", "movimiento");
        java.util.List<Movimiento> movimientos = new java.util.ArrayList<>(nodos.getLength());
        for (int i = 0; i < nodos.getLength(); i++) {
            var fila = (org.w3c.dom.Element) nodos.item(i);
            var fecha = fecha(fila);
            movimientos.add(new Movimiento(texto(fila, "cuenta"), Integer.parseInt(texto(fila, "nromov")), fecha,
                    texto(fila, "tipo"), texto(fila, "accion"), new BigDecimal(texto(fila, "importe"))));
        }
        return java.util.List.copyOf(movimientos);
    }

    java.util.List<Cuenta> traerCuentas(String token) throws IOException {
        Document documento = invocar("traerCuentas", Map.of("token", token));
        NodeList nodos = documento.getElementsByTagNameNS("*", "cuenta");
        java.util.List<Cuenta> cuentas = new java.util.ArrayList<>(nodos.getLength());
        for (int i = 0; i < nodos.getLength(); i++) {
            var fila = (org.w3c.dom.Element) nodos.item(i);
            if (fila.getElementsByTagNameNS("*", "numero").getLength() > 0)
                cuentas.add(new Cuenta(texto(fila, "numero"), texto(fila, "moneda"), new BigDecimal(texto(fila, "saldo"))));
        }
        return java.util.List.copyOf(cuentas);
    }

    int regDeposito(String token, String cuenta, BigDecimal importe) throws IOException {
        String resultado = texto(invocar("regDeposito", Map.of("token", token, "cuenta", cuenta,
                "importe", importe.toPlainString())), "resultado");
        return Integer.parseInt(resultado);
    }

    void cerrarSesion(String token) throws IOException {
        invocar("cerrarSesion", Map.of("token", token));
    }

    private Document invocar(String operacion, Map<String, String> parametros) throws IOException {
        try {
            StringWriter contenido = new StringWriter();
            XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(contenido);
            xml.writeStartDocument("UTF-8", "1.0");
            xml.writeStartElement("soap", "Envelope", SOAP);
            xml.writeNamespace("soap", SOAP);
            xml.writeStartElement("soap", "Body", SOAP);
            xml.writeStartElement("ns", operacion, NS);
            xml.writeNamespace("ns", NS);
            for (var parametro : parametros.entrySet()) {
                xml.writeStartElement("ns", parametro.getKey(), NS);
                xml.writeCharacters(parametro.getValue());
                xml.writeEndElement();
            }
            xml.writeEndElement(); xml.writeEndElement(); xml.writeEndElement(); xml.writeEndDocument(); xml.close();

            HttpRequest solicitud = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "text/xml; charset=utf-8").header("SOAPAction", "\"\"")
                    .POST(HttpRequest.BodyPublishers.ofString(contenido.toString())).build();
            HttpResponse<String> respuesta = http.send(solicitud, HttpResponse.BodyHandlers.ofString());
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            fabrica.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            fabrica.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Document documento = fabrica.newDocumentBuilder().parse(new InputSource(new StringReader(respuesta.body())));
            String fallo = textoOpcional(documento, "Fault");
            if (!fallo.isBlank()) {
                String mensaje = textoOpcional(documento, "faultstring");
                throw new IOException(mensaje.isBlank() ? "EurekaBank rechazó la operación SOAP." : mensaje);
            }
            if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300)
                throw new IOException("El servicio SOAP respondió HTTP " + respuesta.statusCode() + ".");
            return documento;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("La solicitud SOAP fue interrumpida.", e);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("No se pudo interpretar la respuesta SOAP.", e);
        }
    }

    private static String texto(Document documento, String nombre) throws IOException {
        String valor = textoOpcional(documento, nombre);
        if (valor.isBlank()) throw new IOException("Respuesta SOAP incompleta: " + nombre);
        return valor;
    }

    private static String texto(org.w3c.dom.Element elemento, String nombre) throws IOException {
        NodeList hijos = elemento.getElementsByTagNameNS("*", nombre);
        if (hijos.getLength() == 0) throw new IOException("Respuesta SOAP incompleta: " + nombre);
        return hijos.item(0).getTextContent().strip();
    }

    private static java.time.LocalDateTime fecha(org.w3c.dom.Element elemento) throws IOException {
        try {
            return javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(texto(elemento, "fecha"))
                    .toGregorianCalendar().toZonedDateTime().toLocalDateTime();
        } catch (Exception e) { throw new IOException("Fecha de movimiento SOAP inválida.", e); }
    }

    private static String textoOpcional(Document documento, String nombre) {
        NodeList elementos = documento.getElementsByTagNameNS("*", nombre);
        return elementos.getLength() == 0 ? "" : elementos.item(0).getTextContent().strip();
    }
}
