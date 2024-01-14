 package vic.dom.agendaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import vic.dom.agendaapp.databinding.ActivityMainBinding
import vic.dom.agendaapp.fragment.ContactosFragment
import vic.dom.agendaapp.fragment.EventosFragment
import vic.dom.agendaapp.fragment.FavoritosFragment

 class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        replaceFragment(ContactosFragment())
        binding.nav.selectedItemId = R.id.contactos

        binding.nav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.contactos -> replaceFragment(ContactosFragment())
                R.id.eventos -> replaceFragment(EventosFragment())
                R.id.favoritos -> replaceFragment(FavoritosFragment())
            }
            true
        }
    }

     private fun replaceFragment(fragment: Fragment) {
         val fragmentManager = supportFragmentManager
         val fragmentTransaction = fragmentManager.beginTransaction()
         fragmentTransaction.replace(R.id.fragment, fragment)
         fragmentTransaction.commit()
     }
}