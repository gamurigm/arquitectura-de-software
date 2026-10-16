package ec.edu.monster

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import ec.edu.monster.databinding.ActivityMainBinding
import ec.edu.monster.R

/**
 * Actividad principal de la aplicación Monsters Inc. Converter SOAP
 * Maneja la navegación entre fragmentos
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NO configurar ActionBar - mantener pantalla limpia
        // setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        
        // Ocultar ActionBar completamente y no usar FAB
        supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        
        return when (item.itemId) {
            R.id.action_inicio -> {
                navController.navigate(R.id.InicioFragment)
                true
            }
            R.id.action_temperatura -> {
                navController.navigate(R.id.TemperaturaFragment)
                true
            }
            R.id.action_longitud -> {
                navController.navigate(R.id.LongitudFragment)
                true
            }
            R.id.action_peso -> {
                navController.navigate(R.id.PesoFragment)
                true
            }
            R.id.action_settings -> {
                // Mostrar información sobre la aplicación
                Snackbar.make(binding.root, "Monsters Inc. Converter SOAP v1.0", Snackbar.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
