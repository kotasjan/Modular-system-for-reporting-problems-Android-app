package cz.jankotas.bakalarka

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.model.APIResponse
import cz.jankotas.bakalarka.remote.IMyAPI
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val PREFS_FILE = "cz.jankotas.bakalarka.prefs"

    val ACCESS_TOKEN = "access_token"

    var prefs: SharedPreferences? = null

    private lateinit var mService: IMyAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = this.getSharedPreferences(PREFS_FILE, 0)
        val token = prefs!!.getString(ACCESS_TOKEN, null)

        if (token == null) {
            Toast.makeText(this@MainActivity, "Chybějící token", Toast.LENGTH_SHORT).show()
            finish()
        }

        mService = Common.api

        logout.setOnClickListener {

            mService.logoutUser(token).enqueue(object :
                Callback<APIResponse> {
                override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {

                    if (response.code() == 200) {

                        Toast.makeText(this@MainActivity, getString(R.string.successful_logout), Toast.LENGTH_LONG).show()
                        finish()

                    } else {
                        Toast.makeText(this@MainActivity, getString(R.string.err_login_4), Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}