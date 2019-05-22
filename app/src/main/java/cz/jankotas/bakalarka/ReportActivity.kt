package cz.jankotas.bakalarka

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.Report
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    private var report : Report? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                report = getParcelable("report")
                if (report != null) {
                    Log.d(Common.APP_NAME, "Report: $report")
                } else finish()
            }
        }

        if (report != null) {
            if (report!!.photos!!.isNotEmpty()) Glide.with(this).load(report!!.photos?.get(0)).into(header_image)
        }

        fab.setOnClickListener {
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setIcon(category: Int, toolbar: Toolbar) {
        when (category) {
            1 -> toolbar.setLogo(R.drawable.ic_avatar_environment)
            2 -> toolbar.setLogo(R.drawable.ic_avatar_garbage)
            3 -> toolbar.setLogo(R.drawable.ic_avatar_traffic)
        }
    }
}