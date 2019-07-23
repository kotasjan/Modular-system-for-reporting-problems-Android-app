package cz.jankotas.bakalarka

import android.app.Application
import com.cloudinary.android.MediaManager

/**
 * Aplikační třídu jsem musel použít, abych overridoval metodu onCreate() k inicializaci MediaManageru.
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
    }
}