package cz.jankotas.bakalarka

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.Report
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_new_report_detail.*
import kotlinx.android.synthetic.main.scrolling_layout_new_report.*
import java.text.SimpleDateFormat
import java.util.*

class NewReportDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report_detail)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        fillLayout()

        if (Common.newReport.photos.isNotEmpty()) Glide.with(this).load(Common.newReport.photos[0].path).placeholder(R.drawable.photo_placeholder).into(header_image)

        fab.setOnClickListener {
            val intent = Intent(this, ReportOnMapActivity::class.java)
            intent.putExtra("location", Common.newReport.location)
            startActivity(intent)
        }

        btn_send.setOnClickListener {
            sendReport()
        }

        btn_back.setOnClickListener {
            finish()
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
        showDialog()
        return true
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, _ ->
            run {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                Common.newReport.clearData()
                dialog.cancel()
            }
        }

        builder1.setNegativeButton("Cancel") { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        val alert11 = builder1.create()
        alert11.show()
    }

    private fun fillLayout() {
        report_headline.text = Common.newReport.title
        report_city.text = Common.newReport.address
        report_date.text = getDate(Date())
        report_description.text = Common.newReport.userNote
        report_state.text = getString(R.string.report_state0)

        ViewModelProviders.of(this).get(UserViewModel::class.java).getUser().observe(this,
            androidx.lifecycle.Observer<User> { user ->
                Glide.with(application).load(user.avatarURL).into(report_profile_image)
                report_user.text = user.name
            })
    }

    private fun getDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyy", Locale.GERMANY)
        return format.format(date)
    }

    private fun sendReport() {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        Common.newReport.clearData()
    }
}
