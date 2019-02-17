package cz.jankotas.bakalarka

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.model.APIResponse
import cz.jankotas.bakalarka.remote.IMyAPI
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var mService: IMyAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mService = Common.api

        button_login.setOnClickListener {
            loginUser(
                editText_email_login_input.text.toString(),
                editText_password_login_input.text.toString()
            )
        }

        create_new_account_text.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String){

        when {
            !isEmailValid(email) -> {
                editText_email_login_input?.error = getString(R.string.err_login_1)
                editText_password_login_input?.setText("")
            }
            password.isEmpty() -> editText_password_login_input?.error = getString(R.string.err_login_2)
            else -> {

                mService.loginUser(email,password).enqueue(object: Callback<APIResponse> {
                    override fun onFailure(call: Call<APIResponse>?, t: Throwable?) {
                        Toast.makeText(this@LoginActivity, t!!.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<APIResponse>?, response: Response<APIResponse>?) {

                        if (response!!.code() == 401) {
                            editText_email_login_input?.error = getString(R.string.err_login_3)
                            editText_password_login_input?.setText("")
                        } else {

                            Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                        }
                    }
                })
            }
        }
    }
}