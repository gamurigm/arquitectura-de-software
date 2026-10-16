package ec.edu.monster.vista

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ec.edu.monster.R // Importa tus recursos (R.id.fragment_container)
import ec.edu.monster.databinding.ActivityMenuBinding // <-- Binding para activity_menu.xml

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Infla el layout del CONTENEDOR (activity_menu.xml)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Carga el primer Fragment (el menú) solo la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MenuFragment()) // Carga MenuFragment
                .commit()
        }
    }
}