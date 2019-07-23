package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import cz.jankotas.bakalarka.common.SharedPrefs
import cz.jankotas.bakalarka.models.Category
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_welcome.*

/**
 * Úvodní aktivita, která se zobrazuje pouze před prvním přihlášením.
 */
class WelcomeActivity : AppCompatActivity() {

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // inicializace sdílených preferencí
        SharedPrefs.initializeSharedPreferences(this)

        // inicializace kategorií
        Category.setCategories(this)

        // získání dat uživatele (pokud již byl přihlášen)
        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this,
            androidx.lifecycle.Observer<User> { user ->
                if (user == null) return@Observer

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("email", user.email)
                intent.putExtra("password", user.password)

                startActivity(intent)
            })

        // tlačítko, které spustí aktivitu přihlášení
        button_login_welcome.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // tlačítko, které spustí aktivitu registrace
        button_register_welcome.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // zobrazení menu po kliknutí na ikonu tří teček v pravém horním rohu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report_bug, menu)
        return true
    }

    // definování akcí vzhledem k výběru položky z menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_report_bug -> {
                // spustit aktivitu ReportBugActivity
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
