package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APILoginResponse
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.full_view_progress_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Aktivita sloužící k přihlašování uživatele.
 */
class LoginActivity : AppCompatActivity() {

    lateinit var dialog: Dialog // dialog načítání

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // nastavení layoutu aktivity

        // nastavení listeneru na zmáčknutí tlačítka "Přihlásit se"
        button_login_login.setOnClickListener {
            loginUser(editText_email_sign_in_input.text.toString(), editText_password_sign_in_input.text.toString())
        }

        // nastavení listeneru na zmáčknutí tlačítka "Vytvořit nový účet"
        create_new_account_text.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        // nastavení dialogu s progress barem
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.setCancelable(false)

        // pokud aktivita přijímá data z předchozí aktivity
        if (intent.extras != null) {
            val email = intent.getStringExtra("email")
            val password = intent.getStringExtra("password")
            editText_email_sign_in_input.setText(email) // vložit uložený email do pole s emailem
            editText_password_sign_in_input.setText(password) // vložit uložené heslo do pole s heslem
            loginUser(email, password) // přihlásit uživatele
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

    // kontrola, zda je email validní
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // přihlášení uživatele
    private fun loginUser(email: String, password: String) {

        when {
            // kontrola vstupů
            checkErrors(email, password) -> return

            else -> {

                dialog.progress_text.text = getString(R.string.loggin_in_running) // nastavení textu dialogu
                dialog.show() // zobrazení dialogu načítání

                // zaslání požadavku na přihlášení
                Common.api.loginUser(email, password).enqueue(object : Callback<APILoginResponse> {

                    // v případě selhání ukončit dialog a zobrazit chybu uživateli
                    override fun onFailure(call: Call<APILoginResponse>?, t: Throwable?) {
                        dialog.dismiss()
                        Toast.makeText(this@LoginActivity, t!!.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<APILoginResponse>?, response: Response<APILoginResponse>?) {

                        when {
                            // pokud HTTP status kód 401, uživatel není autorizován
                            response!!.code() == 401 -> {
                                editText_email_sign_in_input?.error = getString(R.string.err_login_3)
                                editText_password_sign_in_input?.setText("")
                                dialog.dismiss()
                            }
                            response.isSuccessful -> { // úspěch

                                Toast.makeText(this@LoginActivity, getString(R.string.user_logged_in), Toast.LENGTH_SHORT).show()

                                val user = response.body()!!.user
                                user.password = password

                                ViewModelProviders.of(this@LoginActivity).get(UserViewModel::class.java).insert(user) // uložení uživatele

                                Common.token = "Bearer " + response.body()!!.access_token // uložení tokenu
                                Common.userID = user.id
                                Common.login = true

                                startActivity(Intent(this@LoginActivity, MainActivity::class.java)) // spuštění hlavní aktivity

                            }
                            else -> {
                                Toast.makeText(this@LoginActivity,
                                    getString(R.string.err_login_4),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        dialog.dismiss() // skrytí dialogu
                    }
                })
            }
        }
    }

    // kontrola vstupů, zda jsou ve správném tvaru
    private fun checkErrors(email: String, password: String): Boolean {

        editText_email_sign_in_input.error = null
        editText_password_sign_in_input.error = null

        when {
            !isEmailValid(email) -> {
                editText_email_sign_in_input?.error = getString(R.string.err_login_1)
                editText_password_sign_in_input?.setText("")
                return true
            }
            password.isEmpty() -> {
                editText_password_sign_in_input?.error = getString(R.string.err_login_2)
                return true
            }
        }
        return false
    }
}