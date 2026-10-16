package ec.edu.monster.servidor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

data class ResultadoLoginSoap(
    val autenticado: Boolean,
    val mensaje: String
)

object ClienteAutenticacionSoap {

    private const val NAMESPACE = "http://tempuri.org/"
    private const val INTERFAZ_NOMBRE = "WSConversion"

    // Para celular real usando IIS:
    private const val SERVER_IP = "10.40.25.94"
    private const val SERVER_PORT = "8085"

    // Para emulador usando IIS Express:
    // private const val SERVER_IP = "10.0.2.2"
    // private const val SERVER_PORT = "62533"

    private const val URL = "http://$SERVER_IP:$SERVER_PORT/Service1.svc"
    private const val OPERATION = "login"

    suspend fun login(usuario: String, contrasena: String): ResultadoLoginSoap = withContext(Dispatchers.IO) {
        val request = SoapObject(NAMESPACE, OPERATION).apply {
            addProperty("usuario", usuario)
            addProperty("contrasena", contrasena)
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = true
            setOutputSoapObject(request)
        }

        val transport = HttpTransportSE(URL, 60000)

        return@withContext try {
            val soapAction = "$NAMESPACE$INTERFAZ_NOMBRE/$OPERATION"
            transport.call(soapAction, envelope)

            val response = envelope.response as? SoapPrimitive
            val autenticado = response?.toString()?.trim()?.toBoolean() ?: false

            ResultadoLoginSoap(
                autenticado = autenticado,
                mensaje = if (autenticado) "Autenticación exitosa" else "Credenciales incorrectas"
            )
        } catch (e: Exception) {
            ResultadoLoginSoap(false, e.message ?: "No se pudo conectar al servidor SOAP")
        }
    }
}

