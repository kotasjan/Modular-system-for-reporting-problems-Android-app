package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import cz.jankotas.bakalarka.common.SharedPrefs
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        SharedPrefs.initializeSharedPreferences(this)

        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this,
            androidx.lifecycle.Observer<User> { user ->
                if (user == null) return@Observer

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("email", user.email)
                intent.putExtra("password", user.password)

                startActivity(intent)
                finish()
            })

        button_login_welcome.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_register_welcome.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
