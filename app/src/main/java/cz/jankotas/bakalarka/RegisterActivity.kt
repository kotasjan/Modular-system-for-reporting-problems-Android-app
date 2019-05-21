package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APILoginResponse
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)

        button_register_sign_in.setOnClickListener {
            registerUser(editText_name_sign_up_input.text.toString(),
                editText_email_sign_up_input.text.toString(),
                editText_telephone_sign_up_input.text.toString(),
                editText_password_sign_up_input.text.toString(),
                editText_password_confirm_sign_up_input.text.toString())
        }

        textView_create_new_account.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    fun registerUser(name: String, email: String, telephone: String, password: String, password_confirmation: String) {

        when {

            checkErrors(name, email, telephone, password, password_confirmation) -> return

            else -> {

                showDialog()

                Common.api.registerUser(name, email, telephone.toInt(), password, password_confirmation).enqueue(object :
                    Callback<APILoginResponse> {
                    override fun onFailure(call: Call<APILoginResponse>?, t: Throwable?) {

                        hideDialog()

                        Toast.makeText(this@RegisterActivity, t!!.message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<APILoginResponse>?, response: Response<APILoginResponse>?) {

                        hideDialog()

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

    private fun checkErrors(name: String, email: String, telephone: String, password: String, password_confirmation: String): Boolean {

        editText_name_sign_up_input.error = null
        editText_email_sign_up_input.error = null
        editText_telephone_sign_up_input.error = null
        editText_password_sign_up_input.error = null
        editText_password_confirm_sign_up_input.error = null
        checkBox_sign_in.error = null

        when {
            name.isEmpty() -> {
                editText_name_sign_up_input.error = "Jméno musí být vyplněno"
                return true
            }
            email.isEmpty() -> {
                editText_email_sign_up_input.error = "Email musí být vyplněn"
                return true
            }
            !isEmailValid(email) -> {
                editText_email_sign_up_input.error = "Email nemá požadovaný formát (např. email@email.cz)"
                return true
            }
            !telephoneIsValid(telephone) -> {
                editText_telephone_sign_up_input.error = "Telefon není v požadovaném formátu (např. 111111111)"
                return true
            }
            password.isEmpty() -> {
                editText_password_sign_up_input.error = "Heslo musí být vyplněno a musí mít nejméně 8 znaků"
                return true
            }
            password.length < 8 -> {
                editText_password_sign_up_input.error = "Délka hesla musí být minimálně 8 znaků"
                return true
            }
            password_confirmation.isEmpty() -> {
                editText_password_confirm_sign_up_input.error = "Heslo musí být potvrzeno"
                return true
            }
            password != password_confirmation -> {
                editText_password_sign_up_input.error = "Zadaná hesla se neshodují"
                editText_password_confirm_sign_up_input.error = "Zadaná hesla se neshodují"
                return true
            }
            !checkBox_sign_in.isChecked -> {
                checkBox_sign_in.error = "Pro registraci je nutné souhlasit s podmínkami"
                return true
            }
        }
        return false
    }

    fun telephoneIsValid(telephone: String): Boolean {
        val v = telephone.toIntOrNull()
        return when (v) {
            null -> false
            else -> telephone.length == 9
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun showDialog() {
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun hideDialog() {
        dialog.dismiss()
    }
}
