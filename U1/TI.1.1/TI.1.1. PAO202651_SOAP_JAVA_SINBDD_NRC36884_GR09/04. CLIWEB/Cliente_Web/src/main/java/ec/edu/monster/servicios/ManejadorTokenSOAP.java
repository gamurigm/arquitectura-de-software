package ec.edu.monster.servicios;

import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;
import javax.xml.namespace.QName;

public final class ManejadorTokenSOAP implements SOAPHandler<SOAPMessageContext> {

    private final String token;

    public ManejadorTokenSOAP(String token) {
        this.token = token;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext contexto) {
        if (!Boolean.TRUE.equals(contexto.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
            return true;
        }
        QName operacion = (QName) contexto.get(MessageContext.WSDL_OPERATION);
        if (operacion != null && "login".equals(operacion.getLocalPart())) {
            return true;
        }
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("La sesión terminó. Inicie sesión nuevamente.");
        }
        try {
            SOAPEnvelope sobre = contexto.getMessage().getSOAPPart().getEnvelope();
            SOAPHeader encabezado = contexto.getMessage().getSOAPHeader();
            if (encabezado == null) {
                encabezado = sobre.addHeader();
            }
            QName nombreToken = new QName(ClienteSOAP.NAMESPACE, "token", "ws");
            encabezado.addHeaderElement(nombreToken).addTextNode(token);
            contexto.getMessage().saveChanges();
            return true;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo adjuntar el token SOAP", ex);
        }
    }

    @Override public Set<QName> getHeaders() { return Set.of(); }
    @Override public boolean handleFault(SOAPMessageContext contexto) { return true; }
    @Override public void close(MessageContext contexto) { }
}
