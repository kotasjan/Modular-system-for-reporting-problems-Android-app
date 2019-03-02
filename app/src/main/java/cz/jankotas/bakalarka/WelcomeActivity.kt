package cz.jankotas.bakalarka

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    val PREFS_FILE = "cz.jankotas.bakalarka.prefs"

    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        prefs = this.getSharedPreferences(PREFS_FILE, 0)

        val email = prefs!!.getString("email", null)
        val password = prefs!!.getString("password", null)

        if  (email != null && password != null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            startActivity(intent)
            finish()
        }

        button_login_welcome.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_register_welcome.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
