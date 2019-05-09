package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APIResponse
import cz.jankotas.bakalarka.services.imagedownloader.DownloadAndSaveImageTask
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
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

        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)

        button_login_login.setOnClickListener {
            loginUser(editText_email_sign_in_input.text.toString(), editText_password_sign_in_input.text.toString())
        }

        create_new_account_text.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

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

                showDialog()

                Common.api.loginUser(email, password).enqueue(object : Callback<APIResponse> {
                    override fun onFailure(call: Call<APIResponse>?, t: Throwable?) {

                        hideDialog()

                        Toast.makeText(this@LoginActivity, t!!.message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<APIResponse>?, response: Response<APIResponse>?) {

                        when {
                            response!!.code() == 401 -> {

                                editText_email_sign_in_input?.error = getString(R.string.err_login_3)
                                editText_password_sign_in_input?.setText("")
                                hideDialog()

                            }
                            response.isSuccessful -> {

                                Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()

                                val user = response.body()!!.user
                                user.password = password

                                ViewModelProviders.of(this@LoginActivity).get(UserViewModel::class.java).insert(user)

                                Common.token = "Bearer " + response.body()!!.access_token

                                Common.login = true

                                DownloadAndSaveImageTask(this@LoginActivity).execute("user_avatar", user.avatarURL)

                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                                hideDialog()

                                if (Common.login) {
                                    Log.d(Common.APP_NAME, "Finishing app")
                                    finish()
                                }
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity,
                                    getString(R.string.err_login_4),
                                    Toast.LENGTH_SHORT).show()
                                hideDialog()
                            }
                        }
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

    private fun showDialog() {
        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun hideDialog() {
        dialog.dismiss()
    }
}