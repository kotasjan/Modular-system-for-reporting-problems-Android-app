package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APILoginResponse
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.full_view_progress_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Aktivita sloužící k registraci uživatele.
 */
class RegisterActivity : AppCompatActivity() {

    lateinit var dialog: Dialog // dialog načítání

    // onCreate metoda inicializuje aktivitu (nastavení view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // nastavení dialogu s progress barem
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.setCancelable(false)

        // nastavení listeneru na zmáčknutí tlačítka "Registrovat se"
        button_register_sign_in.setOnClickListener {
            registerUser(editText_name_sign_up_input.text.toString(),
                editText_email_sign_up_input.text.toString(),
                editText_telephone_sign_up_input.text.toString(),
                editText_password_sign_up_input.text.toString(),
                editText_password_confirm_sign_up_input.text.toString())
        }

        // pokud uživatel již má účet, může se přihlásit
        textView_login_user.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
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

    // registrace uživatele
    private fun registerUser(name: String, email: String, telephone: String, password: String, password_confirmation: String) {

        when {

            // kontrola vstupů, zda jsou ve správném tvaru
            checkErrors(name, email, telephone, password, password_confirmation) -> return

            else -> {

                dialog.progress_text.text = getString(R.string.registration_is_running) // nastavení textu dialogu
                showDialog() // zobrazení dialogu načítání

                // zaslání požadavku na registraci uživatele
                Common.api.registerUser(name, email, telephone.toInt(), password, password_confirmation).enqueue(object :
                    Callback<APILoginResponse> {

                    // v případě selhání ukončit dialog a zobrazit chybu uživateli
                    override fun onFailure(call: Call<APILoginResponse>?, t: Throwable?) {
                        hideDialog()
                        Toast.makeText(this@RegisterActivity, t!!.message, Toast.LENGTH_SHORT).show()
                    }

                    // v případě úspěchu spustit aktivitu LoginActivity a pokusit se přihlásit do aplikace
                    override fun onResponse(call: Call<APILoginResponse>?, response: Response<APILoginResponse>?) {

                        hideDialog()

                        // pokud byl uživatel vytvořen, dát info uživateli a spustit přihlašování
                        if (response!!.code() == 201) {
                            Toast.makeText(this@RegisterActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("password", password)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegisterActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }

                    }
                })
            }
        }
    }

    // kontrola vstupů, zda jsou ve správném tvaru
    private fun checkErrors(name: String, email: String, telephone: String, password: String, password_confirmation: String): Boolean {

        editText_name_sign_up_input.error = null
        editText_email_sign_up_input.error = null
        editText_telephone_sign_up_input.error = null
        editText_password_sign_up_input.error = null
        editText_password_confirm_sign_up_input.error = null
        checkBox_sign_in.error = null

        // kontrola chyb a zobrazení chybových hlášek uvnitř inputů
        when {
            name.isEmpty() -> { // pokud je pole jména prázdné
                editText_name_sign_up_input.error = "Jméno musí být vyplněno"
                return true
            }
            email.isEmpty() -> { // pokud je pole email prázdné
                editText_email_sign_up_input.error = "Email musí být vyplněn"
                return true
            }
            !isEmailValid(email) -> { // pokud je email ve správném formátu
                editText_email_sign_up_input.error = "Email nemá požadovaný formát (např. email@email.cz)"
                return true
            }
            !telephoneIsValid(telephone) -> { // kontrola telefoního čísla, zda je v odpovídajícím tvaru
                editText_telephone_sign_up_input.error = "Telefon není v požadovaném formátu (např. 111111111)"
                return true
            }
            password.isEmpty() -> { // pokud je pole hesla prázdné
                editText_password_sign_up_input.error = "Heslo musí být vyplněno a musí mít nejméně 8 znaků"
                return true
            }
            password.length < 8 -> { // pokud délka hesla je menší než 8 znaků
                editText_password_sign_up_input.error = "Délka hesla musí být minimálně 8 znaků"
                return true
            }
            password_confirmation.isEmpty() -> { // pokud je pole potvrzení hesla prázdné
                editText_password_confirm_sign_up_input.error = "Heslo musí být potvrzeno"
                return true
            }
            password != password_confirmation -> { // pokud se hesla neshodují
                editText_password_sign_up_input.error = "Zadaná hesla se neshodují"
                editText_password_confirm_sign_up_input.error = "Zadaná hesla se neshodují"
                return true
            }
            !checkBox_sign_in.isChecked -> { // pokud je checkbox prázdný
                checkBox_sign_in.error = "Pro registraci je nutné souhlasit s podmínkami"
                return true
            }
        }
        return false
    }

    // kotrola telefoního čísla
    fun telephoneIsValid(telephone: String): Boolean {
        val v = telephone.toIntOrNull() // kontrola, zda se jedná o číslo
        return when (v) {
            null -> false
            else -> telephone.length == 9 // délka českého číslaa musí být 9 znaků
        }
    }

    // kontrola validity emailu
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // zobrazení dialogu s progress barem
    fun showDialog() {
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()
    }

    // skrytí dialogu
    fun hideDialog() {
        dialog.dismiss()
    }
}
