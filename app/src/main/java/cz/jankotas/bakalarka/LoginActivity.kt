package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login_login.setOnClickListener {
            loginUser(editText_email_sign_in_input.text.toString(), editText_password_sign_in_input.text.toString())
        }

        create_new_account_text.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.setCancelable(false)

        if (intent.extras != null) {
            val email = intent.getStringExtra("email")
            val password = intent.getStringExtra("password")
            editText_email_sign_in_input.setText(email)
            editText_password_sign_in_input.setText(password)
            loginUser(email, password)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String) {

        when {

            checkErrors(email, password) -> return

            else -> {

                dialog.progress_text.text = getString(R.string.loggin_in_running)
                dialog.show()

                Common.api.loginUser(email, password).enqueue(object : Callback<APILoginResponse> {
                    override fun onFailure(call: Call<APILoginResponse>?, t: Throwable?) {

                        dialog.dismiss()

                        Toast.makeText(this@LoginActivity, t!!.message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<APILoginResponse>?, response: Response<APILoginResponse>?) {

                        when {
                            response!!.code() == 401 -> {
                                editText_email_sign_in_input?.error = getString(R.string.err_login_3)
                                editText_password_sign_in_input?.setText("")
                                dialog.dismiss()
                            }
                            response.isSuccessful -> {

                                Toast.makeText(this@LoginActivity, getString(R.string.user_logged_in), Toast.LENGTH_SHORT).show()

                                val user = response.body()!!.user
                                user.password = password

                                ViewModelProviders.of(this@LoginActivity).get(UserViewModel::class.java).insert(user)

                                Common.token = "Bearer " + response.body()!!.access_token
                                Common.userID = user.id
                                Common.login = true

                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                            }
                            else -> {
                                Toast.makeText(this@LoginActivity,
                                    getString(R.string.err_login_4),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        dialog.dismiss()
                    }
                })
            }
        }
    }

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