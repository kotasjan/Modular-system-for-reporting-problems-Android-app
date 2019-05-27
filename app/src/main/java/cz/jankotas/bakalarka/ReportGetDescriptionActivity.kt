package cz.jankotas.bakalarka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_report_get_description.*
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import cz.jankotas.bakalarka.common.Common.newReport

class ReportGetDescriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_description)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        description_progressBar.progress = 40

        report_title.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                title_letters.text = "$count/80"
            }
        })

        report_description.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                description_letters.text = "$count/255"
            }
        })

        btn_continue.setOnClickListener {
            if(checkInputs()){
                startNextActivity()
            }
        }

        btn_back.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, id -> run {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            dialog.cancel()
        }}

        builder1.setNegativeButton("Cancel") { dialog, id -> run {
            dialog.cancel()
        }}

        val alert11 = builder1.create()
        alert11.show()
    }


    private fun checkInputs(): Boolean {
        var okay = true

        if (report_title.length() == 0) {
            okay = false
            report_title.error = "Tohle pole je povinné"
        }
        if (report_description.length() == 0) {
            okay = false
            report_description.error = "Tohle pole je povinné"
        }
        return okay
    }

    private fun startNextActivity() {
        newReport.title = report_title.text.toString()
        newReport.userNote = report_description.text.toString()

        val intent = Intent(this, ReportGetPhotosActivity::class.java)
        startActivity(intent)
    }
}
