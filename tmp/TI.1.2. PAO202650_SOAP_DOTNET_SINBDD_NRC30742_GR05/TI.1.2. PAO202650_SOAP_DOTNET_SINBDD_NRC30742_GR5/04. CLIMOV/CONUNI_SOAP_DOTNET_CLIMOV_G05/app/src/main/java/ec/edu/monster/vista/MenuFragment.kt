package ec.edu.monster.vista

import android.content.Intent // Importa Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ec.edu.monster.MainActivity // Importa tu LoginActivity
import ec.edu.monster.R
import ec.edu.monster.databinding.FrmInicioBinding

class MenuFragment : Fragment() {

    private var _binding: FrmInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FrmInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // Cambia 'View view' por 'view: View' y 'Bundle savedInstanceState' por 'savedInstanceState: Bundle?'
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura los listeners de las tarjetas
        binding.cardTemperatura.setOnClickListener {
            navegarATemperatura()
        }

        binding.cardLongitud.setOnClickListener {
            navegarALongitud()
        }

        binding.cardPeso.setOnClickListener {
            navegarAPeso()
        }

        binding.btnLogout.setOnClickListener {
            // Cierra la MenuActivity
            activity?.finish()

            // Opcional: Reabrir la pantalla de Login
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }
    // --- FIN DE LA CORRECCIÓN ---

    private fun navegarATemperatura() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TemperaturaFragment())
            .addToBackStack(null) // Permite al usuario regresar al menú
            .commit()
    }
    private fun navegarALongitud() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LongitudFragment()) // <-- Lanza LongitudFragment
            .addToBackStack(null)
            .commit()
    }

    private fun navegarAPeso() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PesoFragment()) // <-- Lanza PesoFragment
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}