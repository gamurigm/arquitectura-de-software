package ec.edu.monster.service;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.ws.WSEureka;
import ec.edu.monster.ws.WSEureka_Service;
import com.sun.xml.ws.developer.JAXWSProperties;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.List;

public final class EurekaService {
    private static final String ENDPOINT_PREDETERMINADO =
            "http://localhost:8080/WS_EurekaBank_SOAP_JAVA_GR09/WSEureka";
    private final URL wsdl;
    private final String endpoint;
    private final SoapOperacionesSeguras seguras;

    public EurekaService() {
        this(System.getenv().getOrDefault("EUREKABANK_SOAP_URL", ENDPOINT_PREDETERMINADO));
    }

    public EurekaService(String endpoint) {
        URI uri = URI.create(endpoint.strip());
        if (uri.getHost() == null || uri.getUserInfo() != null
                || !("http".equalsIgnoreCase(uri.getScheme())
                || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalArgumentException("EUREKABANK_SOAP_URL debe ser una URL HTTP o HTTPS.");
        }
        wsdl = WSEureka_Service.class.getResource("/META-INF/wsdl/WSEureka.wsdl");
        if (wsdl == null) {
            throw new WebServiceException("No se encontró el WSDL local.");
        }
        this.endpoint = uri.toString();
        seguras = new SoapOperacionesSeguras(this.endpoint);
    }

    private WSEureka nuevoPuerto() {
        WSEureka puerto = new WSEureka_Service(wsdl).getWSEurekaPort();
        BindingProvider binding = (BindingProvider) puerto;
        binding.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
        binding.getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, 5000);
        binding.getRequestContext().put(JAXWSProperties.REQUEST_TIMEOUT, 15000);
        return puerto;
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        return nuevoPuerto().login(usuario, contrasena) == 1;
    }

    public String crearSesion(String usuario, String contrasena) throws java.io.IOException {
        return seguras.crearSesion(usuario, contrasena);
    }

    public boolean transferir(String token, String origen, String destino, BigDecimal importe,
                              java.util.UUID clave) throws java.io.IOException {
        return seguras.transferir(token, origen, destino, importe, clave);
    }

    public void cerrarSesion(String token) throws java.io.IOException { seguras.cerrarSesion(token); }

    public List<Movimiento> traerMovimientos(String token, String cuenta) throws java.io.IOException {
        return seguras.traerMovimientos(token, cuenta);
    }

    public List<ec.edu.monster.modelo.Cuenta> traerCuentas(String token) throws java.io.IOException { return seguras.traerCuentas(token); }

    public boolean registrarDeposito(String token, String cuenta, BigDecimal importe) throws java.io.IOException {
        return seguras.regDeposito(token, cuenta, importe) == 1;
    }
}
