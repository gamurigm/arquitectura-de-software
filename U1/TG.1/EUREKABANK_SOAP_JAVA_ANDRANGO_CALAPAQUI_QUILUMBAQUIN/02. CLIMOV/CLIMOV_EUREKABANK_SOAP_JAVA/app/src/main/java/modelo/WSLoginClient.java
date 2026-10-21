package modelo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WSLoginClient {

    private static final String NAMESPACE = "http://ws.monster.edu.ec/";
    private static final String URL = "http://10.244.172.53:8080/WSEureka"; // Cambia a la URL del servicio sin ?wsdl
    private static final String METHOD_NAME = "login";
    private static final String SOAP_ACTION = NAMESPACE + "login"; // Debe coincidir con el WSDL

    public String login(String username, String password) {
        try {
            // Crea la solicitud SOAP
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("usuario", username);
            request.addProperty("contrasena", password);


            // Crea el sobre SOAP
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false; // Cambiar a true si el servicio web es .NET
            envelope.setOutputSoapObject(request);

            // Llama al servicio
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, envelope);

            // Maneja la respuesta
            Object response = envelope.getResponse();
            if (response instanceof SoapObject) {
                SoapObject responseObject = (SoapObject) response;
                return responseObject.toString(); // Personaliza según la estructura de tu respuesta
            } else {
                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getClass().getName() + " - " + e.getMessage();
        }
    }







}
