package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.model.APIResponse
import cz.jankotas.bakalarka.model.User
import cz.jankotas.bakalarka.remote.IMyAPI
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    val PREFS_FILE = "cz.jankotas.bakalarka.prefs"

    val ACCESS_TOKEN = "access_token"

    var prefs: SharedPreferences? = null

    lateinit var dialog: Dialog

    private lateinit var mService: IMyAPI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefs = this.getSharedPreferences(PREFS_FILE, 0)

        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)

        mService = Common.api

        button_login_login.setOnClickListener {
            loginUser(editText_email_sign_in_input.text.toString(), editText_password_sign_in_input.text.toString())
        }

        create_new_account_text.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        if (intent.extras != null) {
            editText_email_sign_in_input.setText(intent.getStringExtra("email"))
            editText_password_sign_in_input.setText(intent.getStringExtra("password"))
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

                mService.loginUser(email, password).enqueue(object : Callback<APIResponse> {
                    override fun onFailure(call: Call<APIResponse>?, t: Throwable?) {

                        hideDialog()

                        Toast.makeText(this@LoginActivity, t!!.message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<APIResponse>?, response: Response<APIResponse>?) {

                        hideDialog()

                        when {
                            response!!.code() == 401 -> {

                                editText_email_sign_in_input?.error = getString(R.string.err_login_3)
                                editText_password_sign_in_input?.setText("")

                            }
                            response.isSuccessful -> {

                                Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()

                                if (response.body()!!.access_token != null) {
                                    saveToken("Bearer " + response.body()!!.access_token!!)

                                    saveUser(response.body()!!.user!!)

                                    val editor = prefs!!.edit()
                                    editor.putString("email", email)
                                    editor.putString("password", password)
                                    editor.apply()

                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                }
                            }
                            else -> Toast.makeText(this@LoginActivity,
                                getString(R.string.err_login_4),
                                Toast.LENGTH_SHORT).show()
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

    fun saveToken(token: String) {
        val editor = prefs!!.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
        Log.d("Bakalarka", "Token saved: $token")
    }

    fun saveUser(user: User) {
        val prefsEditor = prefs!!.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor.putString("User", json)
        prefsEditor.apply()
        Log.d("Bakalarka", "User saved: $json")
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