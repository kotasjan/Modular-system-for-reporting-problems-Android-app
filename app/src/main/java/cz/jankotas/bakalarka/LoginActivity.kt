package cz.jankotas.bakalarka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var emailInput: EditText = findViewById(R.id.email_login_input)
        var passwordInput: EditText = findViewById(R.id.password_login_input)

        val btnLogin: Button = findViewById(R.id.button_login)
        btnLogin.setOnClickListener {
            when {
                !isEmailValid(emailInput.text.toString()) -> {
                    emailInput.error = "Neplatný email"
                    passwordInput.setText("")
                }
                passwordInput.text.toString().isEmpty() -> passwordInput.error = "Heslo je povinné"
                else -> {

                    loginUser(emailInput.text.toString(), passwordInput.text.toString())

                }
            }
        }


    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun loginUser (email: String, password: String): String? {

        Log.d("Bakalarka", "Login user $email with password $password.")

        return null
    }

}