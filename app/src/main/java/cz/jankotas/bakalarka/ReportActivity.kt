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
import kotlinx.android.synthetic.main.scrolling_layout.*
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    lateinit var report: Report

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
                val pom: Report? = getParcelable("report")
                if (pom != null) {
                    report = pom
                    Log.d(Common.APP_NAME, "Report: $report")
                } else finish()
            }
        }

        if (report.photos!!.isNotEmpty()) Glide.with(this).load(report.photos?.get(0)).into(header_image)
        fillLayout()

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

    private fun fillLayout() {
        report_headline.text = report.title
        report_city.text = report.address
        report_date.text = getDate(report.created_at)
    }

    private fun getDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyy", Locale.GERMANY)
        return format.format(date)
    }
}