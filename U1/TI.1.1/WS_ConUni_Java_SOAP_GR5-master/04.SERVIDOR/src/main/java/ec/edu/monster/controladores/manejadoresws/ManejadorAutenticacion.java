package ec.edu.monster.controladores.manejadoresws;

import ec.edu.monster.seguridad.AdministradorTokens;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import jakarta.xml.ws.handler.MessageContext;

import javax.xml.namespace.QName;
import org.w3c.dom.NodeList;
import java.util.Set;

public class ManejadorAutenticacion implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext contexto) {

        Boolean esSalida = (Boolean) contexto.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!esSalida) {
            try {

                QName operacion = (QName) contexto.get(MessageContext.WSDL_OPERATION);
                if (operacion != null && "login".equals(operacion.getLocalPart())) {
                    return true; 
                }

                var mensajeSOAP = contexto.getMessage();
                var encabezado = mensajeSOAP.getSOAPHeader();

                if (encabezado == null) {
                    throw new RuntimeException("Acceso denegado: Se requiere encabezado SOAP con el token de seguridad.");
                }

                NodeList nodosToken = encabezado.getElementsByTagNameNS("*", "token");

                if (nodosToken.getLength() == 0) {
                    throw new RuntimeException("Acceso denegado: No se encontró el token. Inicie sesión primero.");
                }

                String token = nodosToken.item(0).getTextContent();

                if (!AdministradorTokens.validarToken(token)) {
                    throw new RuntimeException("Acceso denegado: Token inválido o expirado.");
                }

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext contexto) {
        return true;
    }

    @Override
    public void close(MessageContext contexto) {}
}