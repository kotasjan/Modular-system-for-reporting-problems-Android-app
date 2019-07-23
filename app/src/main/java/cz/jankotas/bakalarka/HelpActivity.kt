package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

/**
 * Aktivita s návodem k použití aplikace.
 */
class HelpActivity : AppCompatActivity(){

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help) // nastavení layoutu aktivity

        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // toolbar s tlačítkem zpět
        supportActionBar!!.setDisplayShowHomeEnabled(true) // zobrazení tlašítka
    }

    // navigace do předchozí aktivity
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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
