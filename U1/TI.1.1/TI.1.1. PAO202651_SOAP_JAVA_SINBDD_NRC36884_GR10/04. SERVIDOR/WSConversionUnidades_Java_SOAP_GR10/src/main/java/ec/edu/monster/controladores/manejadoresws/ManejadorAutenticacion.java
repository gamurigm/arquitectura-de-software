package ec.edu.monster.controladores.manejadoresws;

import ec.edu.monster.seguridad.AdministradorTokens;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;
import javax.xml.namespace.QName;
import org.w3c.dom.NodeList;

public class ManejadorAutenticacion implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext contexto) {
        if (Boolean.TRUE.equals(contexto.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
            return true;
        }

        QName operacion = (QName) contexto.get(MessageContext.WSDL_OPERATION);
        if (operacion != null && "login".equals(operacion.getLocalPart())) {
            return true;
        }

        try {
            var encabezado = contexto.getMessage().getSOAPHeader();
            if (encabezado == null) {
                throw new WebServiceException("Acceso denegado: se requiere un encabezado SOAP con el token.");
            }

            NodeList nodosToken = encabezado.getElementsByTagNameNS("*", "token");
            if (nodosToken.getLength() == 0) {
                throw new WebServiceException("Acceso denegado: no se encontró el token. Inicie sesión primero.");
            }

            String token = nodosToken.item(0).getTextContent().trim();
            if (!AdministradorTokens.validarToken(token)) {
                throw new WebServiceException("Acceso denegado: token inválido.");
            }
            return true;
        } catch (WebServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WebServiceException("No se pudo validar el token SOAP.", ex);
        }
    }

    @Override
    public Set<QName> getHeaders() {
        return Set.of();
    }

    @Override
    public boolean handleFault(SOAPMessageContext contexto) {
        return true;
    }

    @Override
    public void close(MessageContext contexto) {
    }
}