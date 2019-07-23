package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.nguyenhoanglam.imagepicker.model.Image
import kotlinx.android.parcel.Parcelize

/**
 * Datová třída pro ukládání dat nového podnětu
 */
@Parcelize
data class NewReport constructor(@Nullable var title: String?,
                                 @Nullable var userNote: String?,
                                 @Nullable var category_id: Int?,
                                 @Nullable var photos: ArrayList<Image> = ArrayList(),
                                 @Nullable var location: Location?,
                                 @Nullable var address: String?,
                                 @Nullable var moduleData: ArrayList<ModuleData>? ) : Parcelable {
    internal fun clearData() {
        title = null
        userNote = null
        category_id = null
        photos = ArrayList()
        location = null
        address = null
        moduleData = null
    }
}

/**
 * Datová třída pro finálně připravený podnět k odeslání serveru
 */
data class NewReportToSend constructor(@NonNull val title: String,
                                       @NonNull val userNote: String,
                                       @NonNull val category_id: Int,
                                       @NonNull val photos: List<String>,
                                       @NonNull val location: Location,
                                       @NonNull val address: String,
                                       @Nullable val moduleData: ArrayList<ModuleData>?)