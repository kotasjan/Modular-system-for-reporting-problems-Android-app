package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import cz.jankotas.bakalarka.common.SharedPrefs
import cz.jankotas.bakalarka.models.Category
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        SharedPrefs.initializeSharedPreferences(this)

        Category.setCategories(this)

        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this,
            androidx.lifecycle.Observer<User> { user ->
                if (user == null) return@Observer

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("email", user.email)
                intent.putExtra("password", user.password)

                startActivity(intent)
            })

        button_login_welcome.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        button_register_welcome.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu. This adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_report_bug, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.*/
        return when (item.itemId) {
            R.id.action_report_bug -> {
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
