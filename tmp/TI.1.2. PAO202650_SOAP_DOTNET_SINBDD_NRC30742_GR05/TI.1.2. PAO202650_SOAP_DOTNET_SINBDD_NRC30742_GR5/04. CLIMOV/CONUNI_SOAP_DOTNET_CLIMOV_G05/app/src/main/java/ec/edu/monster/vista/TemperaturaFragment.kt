package ec.edu.monster.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ec.edu.monster.R
import ec.edu.monster.controlador.ControladorConversion
import ec.edu.monster.databinding.FragmentTemperaturaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento para conversiones de temperatura
 * Permite convertir entre Celsius, Fahrenheit y Kelvin
 */
class TemperaturaFragment : Fragment() {

    private var _binding: FragmentTemperaturaBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var controlador: ControladorConversion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemperaturaBinding.inflate(inflater, container, false)
        controlador = ControladorConversion()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Configurar el spinner con las operaciones de temperatura
        val operaciones = controlador.getOperacionesTemperatura()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            operaciones.map { it.second }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOperacion.adapter = adapter
    }
    
    private fun setupListeners() {
        // Botón de regreso (flecha en el header)
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnConvertir.setOnClickListener {
            convertirTemperatura()
        }
        
        binding.btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
    }
    
    private fun convertirTemperatura() {
        val valorTexto = binding.editValor.text?.toString()?.trim() ?: ""
        
        if (valorTexto.isEmpty()) {
            binding.editValor.error = "Ingrese un valor"
            return
        }
        
        val valor = valorTexto.toDoubleOrNull()
        if (valor == null) {
            binding.editValor.error = "Valor inválido"
            return
        }
        
        val posicion = binding.spinnerOperacion.selectedItemPosition
        val operaciones = controlador.getOperacionesTemperatura()
        val operacion = operaciones[posicion].first
        
        // Mostrar loading
        binding.btnConvertir.isEnabled = false
        binding.btnConvertir.text = "Convirtiendo..."
        
        // Realizar conversión en corrutina
        lifecycleScope.launch {
            try {
                // Ejecutar llamada SOAP en hilo de background
                val resultado = withContext(Dispatchers.IO) {
                    controlador.convertirTemperatura(operacion, valor)
                }
                
                // Verificar que el Fragment aún esté adjunto antes de actualizar UI
                if (!isAdded || view == null) return@launch
                
                // Actualizar UI (ya estamos en el hilo principal con lifecycleScope)
                binding.btnConvertir.isEnabled = true
                binding.btnConvertir.text = "Convertir"
                
                if (resultado.exitosa) {
                    mostrarResultado(resultado)
                } else {
                    mostrarError(resultado.mensajeError)
                }
            } catch (e: Exception) {
                // Verificar que el Fragment aún esté adjunto antes de actualizar UI
                if (!isAdded || view == null) return@launch
                
                binding.btnConvertir.isEnabled = true
                binding.btnConvertir.text = "Convertir"
                mostrarError("Error de conexión: ${e.message}")
            }
        }
    }
    
    private fun mostrarResultado(conversion: ec.edu.monster.modelo.Conversion) {
        binding.textResultado.text = conversion.getResultadoConSimbolos()
        binding.textResultado.setTextColor(ContextCompat.getColor(requireContext(), R.color.monster_blue))
        binding.layoutResultado.visibility = View.VISIBLE
        
        Toast.makeText(requireContext(), "Conversión exitosa", Toast.LENGTH_SHORT).show()
    }
    
    private fun mostrarError(mensaje: String) {
        binding.textResultado.text = mensaje
        binding.textResultado.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        binding.layoutResultado.visibility = View.VISIBLE
        
        Toast.makeText(requireContext(), "Error en la conversión", Toast.LENGTH_SHORT).show()
    }
    
    private fun limpiarCampos() {
        binding.editValor.text?.clear()
        binding.spinnerOperacion.setSelection(0)
        binding.layoutResultado.visibility = View.GONE
        binding.editValor.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
