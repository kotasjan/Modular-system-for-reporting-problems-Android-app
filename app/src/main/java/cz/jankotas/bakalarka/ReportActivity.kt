package cz.jankotas.bakalarka

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.Report

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_login)

        val bundle: Bundle? = intent.extras

        bundle?.let {
            bundle.apply {
                //Parcelable Data
                val report: Report? = getParcelable("report")
                if (report != null) {
                    Log.d(Common.APP_NAME, "Report: $report")
                }
            }
        }
    }
}