package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.models.User
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.scrolling_layout_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    private lateinit var report: Report

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
            val intent = Intent(this, ReportOnMapActivity::class.java)
            intent.putExtra("report", report)
            startActivity(intent)
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

    private fun fillLayout() {
        report_headline.text = report.title
        report_city.text = report.address
        report_date.text = getDate(report.created_at)
        report_description.text = report.userNote
        report_employee_note.text = report.employeeNote
        setState()
        setUser()
    }

    private fun setState() {
        report_state.text = when (report.state) {
            0 -> getString(R.string.report_state0)
            1 -> getString(R.string.report_state1)
            2 -> getString(R.string.report_state2)
            3 -> {
                report_refusal_divider.visibility = View.VISIBLE
                report_reason_for_refusal.visibility = View.VISIBLE
                report_employee_note.visibility = View.VISIBLE
                getString(R.string.report_state3)
            }
            else -> ""
        }
    }

    private fun setUser() {
        Common.api.getUser(Common.token, report.user_id).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d(Common.APP_NAME, "Report activity: Getting user's data failed.")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                Glide.with(application).load(response.body()!!.avatarURL).into(report_profile_image)
                report_user.text = response.body()!!.name
                Log.d(Common.APP_NAME, "Report activity: Getting user's data succeed.")
            }
        })
    }

    private fun getDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyy", Locale.GERMANY)
        return format.format(date)
    }
}