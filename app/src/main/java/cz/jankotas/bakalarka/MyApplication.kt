package cz.jankotas.bakalarka

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
    }
}