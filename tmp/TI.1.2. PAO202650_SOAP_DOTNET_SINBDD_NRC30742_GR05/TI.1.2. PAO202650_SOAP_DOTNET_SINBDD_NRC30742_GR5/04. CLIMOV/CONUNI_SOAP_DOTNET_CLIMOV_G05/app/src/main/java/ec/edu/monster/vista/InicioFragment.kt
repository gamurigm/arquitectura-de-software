package ec.edu.monster.vista

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ec.edu.monster.R
import ec.edu.monster.databinding.FragmentInicioBinding

/**
 * Fragmento de inicio con botones de navegación
 * Permite acceder a todos los tipos de conversión
 */
class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // La UI se configura directamente en el XML
        // No necesitamos configurar textos aquí
    }
    
    private fun setupListeners() {
        // Botón de logout con confirmación
        binding.btnLogout.setOnClickListener {
            mostrarConfirmacionSalir()
        }
        
        // Botón de información
        binding.btnInfo.setOnClickListener {
            mostrarModalInfo()
        }
        
        // Cards de conversión
        binding.cardTemperatura.setOnClickListener {
            findNavController().navigate(R.id.TemperaturaFragment)
        }
        
        binding.cardLongitud.setOnClickListener {
            findNavController().navigate(R.id.LongitudFragment)
        }
        
        binding.cardPeso.setOnClickListener {
            findNavController().navigate(R.id.PesoFragment)
        }
    }
    
    private fun mostrarConfirmacionSalir() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Salida")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí, Salir") { _, _ ->
                realizarLogout()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    
    private fun realizarLogout() {
        try {
            findNavController().navigate(R.id.LoginFragment)
        } catch (e: Exception) {
            // Si no hay navegación configurada, reiniciar la actividad
            requireActivity().recreate()
        }
    }
    
    private fun mostrarModalInfo() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_info, null)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        // Configurar botones del modal
        dialogView.findViewById<View>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }
        
        dialogView.findViewById<View>(R.id.btnEntendido).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


