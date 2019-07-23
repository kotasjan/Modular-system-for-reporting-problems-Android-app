package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.*
import kotlinx.android.synthetic.main.activity_report_bug.*
import kotlinx.android.synthetic.main.activity_report_get_description.*
import kotlinx.android.synthetic.main.full_view_progress_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Aktivita pro hlášení chyb v aplikaci.
 */
class ReportBugActivity : AppCompatActivity() {

    private lateinit var dialog: Dialog // dialog pro nahrávání chyby

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_bug)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // listener který naslouchá změně/úpravě textu v editTextu
        description_text.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                bug_description_letters.text = "${description_text.text.length}/255" // změna počtu znaků v editTextu
            }
        })

        // nastavení listeneru pro tlačítko k odeslání chybového hlášení
        send_bug.setOnClickListener {
            if(description_text.text.isNotEmpty()) {
                sendBugReport()
            } else {
                description_text.error = "Input je prázdný"
            }
        }
    }

    // navigace do předchozí aktivity
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // odeslání chybového hlášení
    private fun sendBugReport() {

        // nastavení dialogu
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.progress_text.text = "Odesílání hlášení.."
        dialog.show() // zobrazení dialogu

        // vytvoření objektu chybového hlášení
        val newBugReport = Bug(description_text.text.toString())

        // odeslání chybového hlášení serveru
        Common.api.sendBugReport(BugPOJO( newBugReport)).enqueue(object : Callback<APIBugResponse> {
            override fun onResponse(call: Call<APIBugResponse>, response: Response<APIBugResponse>) {
                dialog.dismiss()
                if (response.body() != null) {
                    if (!response.body()!!.error) finishActivity() // v případě úspěšného odeslání hlášení ukončit aktivitu
                    else Toast.makeText(this@ReportBugActivity, "Při odesílání hlášení se vyskytla chyba", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(Common.APP_NAME, response.toString())
                    Toast.makeText(this@ReportBugActivity, "Při odesílání hlášení se vyskytla chyba", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<APIBugResponse>, t: Throwable) {
                dialog.dismiss()
                Log.d(Common.APP_NAME, "Failure during sending report.")
                Toast.makeText(this@ReportBugActivity, "Hlášení se nepodařilo odeslat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ukončení aktivity po odeslání hlášení
    private fun finishActivity() {
        Toast.makeText(this, "Děkujeme za odeslání hlášení", Toast.LENGTH_SHORT).show()
        finish()
    }
}