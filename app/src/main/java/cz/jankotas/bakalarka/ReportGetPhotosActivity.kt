package cz.jankotas.bakalarka

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nguyenhoanglam.imagepicker.model.Config.EXTRA_IMAGES
import com.nguyenhoanglam.imagepicker.model.Config.RC_PICK_IMAGES
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import cz.jankotas.bakalarka.adapters.ImageGridAdapter
import cz.jankotas.bakalarka.common.Common.newReport
import kotlinx.android.synthetic.main.activity_report_get_photos.*
import kotlinx.android.synthetic.main.photos_recycler_view.*

class ReportGetPhotosActivity : AppCompatActivity() {

    var selectedImages : ArrayList<Image> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_get_photos)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        btn_continue.setOnClickListener {

        }

        btn_back.setOnClickListener {
            finish()
        }

        photo_fab.setOnClickListener {
            getImage()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null)
            pushDataToAdapter(data.getParcelableArrayListExtra(EXTRA_IMAGES))

        super.onActivityResult(requestCode, resultCode, data)  // You MUST have this line to be here
        // so ImagePicker can work with fragment mode
    }

    private fun getImage() {
        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
            .setToolbarColor(getColorAsString(R.color.colorPrimaryDark))         //  Toolbar color
            .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
            .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
            .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
            .setProgressBarColor("#4CAF50")     //  ProgressBar color
            .setBackgroundColor("#212121")      //  Background color
            .setCameraOnly(false)               //  Camera mode
            .setMultipleMode(true)              //  Select multiple images or single image
            .setFolderMode(true)                //  Folder mode
            .setShowCamera(true)                //  Show camera button
            .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
            .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
            .setDoneTitle("Done")               //  Done button title
            .setLimitMessage("You have reached selection limit")    // Selection limit message
            .setMaxSize(5)                     //  Max images can be selected
            .setSavePath("ImagePicker")         //  Image capture folder name
            .setSelectedImages(selectedImages)          //  Selected images
            .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
            .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
            .setKeepScreenOn(true)              //  Keep screen on when selecting images
            .start()                            //  Start ImagePicker
    }

    private fun getColorAsString(resource: Int) : String {
        return "#"+Integer.toHexString(ContextCompat.getColor(this, resource))
    }

    private fun pushDataToAdapter(imageList: ArrayList<Image>) {
        photos_recycler_view.adapter = ImageGridAdapter(this, imageList)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        photos_recycler_view.layoutManager = layoutManager
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.warning_closing_report))
        builder1.setCancelable(true)

        builder1.setPositiveButton("OK") { dialog, id -> run {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            newReport.clearData()
            dialog.cancel()
        }}

        builder1.setNegativeButton("Cancel") { dialog, id -> run {
            dialog.cancel()
        }}

        val alert11 = builder1.create()
        alert11.show()
    }
}
