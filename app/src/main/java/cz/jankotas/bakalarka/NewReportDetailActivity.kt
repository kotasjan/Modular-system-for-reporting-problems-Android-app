package cz.jankotas.bakalarka

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.preprocess.BitmapEncoder
import com.cloudinary.android.preprocess.ImagePreprocessChain
import com.nguyenhoanglam.imagepicker.model.Image
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.APIReportResponse
import cz.jankotas.bakalarka.models.NewReportToSend
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_new_report_detail.*
import kotlinx.android.synthetic.main.full_view_progress_bar.*
import kotlinx.android.synthetic.main.scrolling_layout_new_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewReportDetailActivity : AppCompatActivity() {

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report_detail)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        fillLayout()

        if (Common.newReport.photos.isNotEmpty()) Glide.with(this).load(Common.newReport.photos[0].path).placeholder(R.drawable.photo_placeholder).into(
            header_image)

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

        val alert = builder1.create()
        alert.show()
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
        uploadPhotos(Common.newReport.photos)
    }

    private fun uploadPhotos(photoList: List<Image>) {
        var photosUploaded = 0

        val view = this.layoutInflater.inflate(R.layout.full_view_progress_bar, null)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(view)
        dialog.progress_text.text = "Nahrávání fotografií: 1/${photoList.size}"
        dialog.show()

        val photoUrlList = ArrayList<String>()
        for (photo in photoList) {

            MediaManager.get().upload(photo.path).unsigned("u4w0w2mx").option("resource_type",
                "image").preprocess(ImagePreprocessChain.limitDimensionsChain(2000, 1500).saveWith(BitmapEncoder(BitmapEncoder.Format.WEBP, 40))).callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        photosUploaded++
                        photoUrlList.add(resultData["url"].toString())
                        if (photosUploaded == photoList.size) uploadReport(photoUrlList)
                        else dialog.progress_text.text = "Nahrávání fotografií: ${photosUploaded + 1}/${photoList.size}"
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        dialog.dismiss()
                        showErrorDialogPhotos("Nahrávání fotografií na server bylo neúspěšné.")
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        dialog.dismiss()
                        showErrorDialogPhotos("Nahrávání fotografií se nezdařilo.")
                    }
                }).dispatch(this)
        }
    }

    private fun uploadReport(photoUrlList: List<String>) {
        dialog.progress_text.text = "Nahrávání zbylých dat.."
        Log.d(Common.APP_NAME, photoUrlList.toString())

        if (!dialog.isShowing) dialog.show()

        val newReportToSend = NewReportToSend(Common.newReport.title!!, Common.newReport.userNote!!, Common.newReport.category_id!!, photoUrlList, Common.newReport.location!!, Common.newReport.address!!, Common.newReport.moduleData)

        Common.api.sendReport(Common.token, newReportToSend).enqueue(object : Callback<APIReportResponse> {
            override fun onResponse(call: Call<APIReportResponse>, response: Response<APIReportResponse>) {
                if (response.body() != null) {
                    if (!response.body()!!.error) finishActivity()
                    else showErrorDialogReportSent(photoUrlList)
                }
            }

            override fun onFailure(call: Call<APIReportResponse>, t: Throwable) {
                Log.d(Common.APP_NAME, "Failure during sending report.")
                dialog.dismiss()
                showErrorDialogReportSent(photoUrlList)
            }
        })
    }

    private fun finishActivity() {
        Toast.makeText(this, "Nahrávání úspěšně dokončeno", Toast.LENGTH_SHORT).show()

        Common.newReport.clearData()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun showErrorDialogPhotos(message: String) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(message)
        builder1.setCancelable(true)

        builder1.setPositiveButton("Opakovat") { dialog, _ ->
            run {
                dialog.cancel()
                sendReport()
            }
        }

        builder1.setNegativeButton("Zrušit") { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        val alert = builder1.create()
        alert.show()
    }

    private fun showErrorDialogReportSent(photoUrlList: List<String>) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Nahrávání reportu se nezdařilo.")
        builder1.setCancelable(true)

        builder1.setPositiveButton("Opakovat") { dialog, _ ->
            run {
                dialog.cancel()
                uploadReport(photoUrlList)
            }
        }

        builder1.setNegativeButton("Zrušit") { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        val alert = builder1.create()
        alert.show()
    }
}
