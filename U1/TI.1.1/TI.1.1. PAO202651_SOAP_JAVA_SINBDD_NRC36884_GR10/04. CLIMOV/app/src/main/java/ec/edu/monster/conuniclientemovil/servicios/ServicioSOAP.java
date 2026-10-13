package ec.edu.monster.conuniclientemovil.servicios;

import android.content.Context;
import ec.edu.monster.conuniclientemovil.R;
import java.io.IOException;
import org.ksoap2.SoapFault;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

public final class ServicioSOAP {

    private static final String ESPACIO_NOMBRES = "http://ws.controladores.monster.edu.ec/";
    private final String urlServicio;
    private final String token;

    public ServicioSOAP(Context contexto, String token) {
        this.urlServicio = contexto.getString(R.string.endpoint_soap);
        this.token = token;
    }

    public String iniciarSesion(String usuario, String contrasenia) throws IOException {
        SoapObject solicitud = new SoapObject(ESPACIO_NOMBRES, "login");
        solicitud.addProperty("usuario", usuario);
        solicitud.addProperty("contrasenia", contrasenia);
        return invocar(solicitud, false);
    }

    public double convertir(String tipo, double valor, String unidadInicial, String unidadFinal)
            throws IOException {
        String operacion;
        switch (tipo) {
            case "longitud":
                operacion = "convertirLongitud";
                break;
            case "masa":
                operacion = "convertirMasa";
                break;
            case "temperatura":
                operacion = "convertirTemperatura";
                break;
            default:
                throw new IllegalArgumentException("Tipo de conversión no válido.");
        }
        SoapObject solicitud = new SoapObject(ESPACIO_NOMBRES, operacion);
        solicitud.addProperty("valor", valor);
        solicitud.addProperty("unidadInicial", unidadInicial);
        solicitud.addProperty("unidadFinal", unidadFinal);
        return Double.parseDouble(invocar(solicitud, true));
    }

    private String invocar(SoapObject solicitud, boolean requiereToken) throws IOException {
        SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sobre.dotNet = false;
        sobre.setOutputSoapObject(solicitud);
        if (requiereToken) {
            if (token == null || token.isBlank()) {
                throw new IOException("La sesión terminó. Inicie sesión nuevamente.");
            }
            Element encabezadoToken = new Element().createElement(ESPACIO_NOMBRES, "token");
            encabezadoToken.addChild(Node.TEXT, token);
            sobre.headerOut = new Element[]{encabezadoToken};
        }

        HttpTransportSE transporte = new HttpTransportSE(urlServicio, 15000);
        try {
            transporte.call("", sobre);
        } catch (XmlPullParserException excepcion) {
            throw new IOException("La respuesta del servidor SOAP no es válida.", excepcion);
        }
        if (sobre.bodyIn instanceof SoapFault) {
            SoapFault fallo = (SoapFault) sobre.bodyIn;
            throw new IOException(fallo.faultstring);
        }

        Object respuesta = sobre.getResponse();
        if (respuesta instanceof SoapObject) {
            SoapObject objeto = (SoapObject) respuesta;
            if (objeto.getPropertyCount() == 0) {
                throw new IOException("El servidor SOAP devolvió una respuesta vacía.");
            }
            respuesta = objeto.getProperty(0);
        }
        if (respuesta == null) {
            throw new IOException("El servidor SOAP no devolvió un resultado.");
        }
        return respuesta.toString();
    }
}
