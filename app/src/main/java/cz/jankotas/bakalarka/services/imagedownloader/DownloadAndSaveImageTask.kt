package cz.jankotas.bakalarka.services.imagedownloader

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import java.lang.ref.WeakReference
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import cz.jankotas.bakalarka.viewmodels.UserViewModel
import java.io.*


class DownloadAndSaveImageTask(context: Context) : AsyncTask<String, Unit, Unit>() {
    private var mContext: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg params: String?) {

        when {
            params[0] == "user_avatar" -> setUserAvatar(params[1]!!)
            else -> {
            }
        }
    }

    fun setUserAvatar(url: String) {
        val requestOptions = RequestOptions().override(100).downsample(DownsampleStrategy.CENTER_INSIDE).skipMemoryCache(
                true).diskCacheStrategy(DiskCacheStrategy.NONE)

        mContext.get()?.let {
            val bitmap = Glide.with(it).asBitmap().load(url).apply(requestOptions).submit().get()

            saveFile(it, bitmap, "profile.jpg")
        }
    }

    fun saveFile(context: Context, b: Bitmap, picName: String) {
        val file = File(context.filesDir, picName)

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress the bitmap
            b.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the output stream
            stream.flush()

            // Close the output stream
            stream.close()

            Log.d("Bakalarka", "Image saved successful.")

            Log.d("Bakalarka", Uri.parse(file.absolutePath).toString())

        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
            Log.d("Bakalarka", "Error to save image.")
        }
    }

    fun loadBitmap(context: Context, picName: String): Bitmap? {
        var b: Bitmap? = null
        var fis: FileInputStream
        try {
            fis = context.openFileInput(picName)
            b = BitmapFactory.decodeStream(fis)
            fis.close()
        } catch (e: FileNotFoundException) {
            Log.d("Bakalarka", "file not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d("Bakalarka", "io exception")
            e.printStackTrace()
        }
        return b
    }
}