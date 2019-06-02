package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewReport constructor(@Nullable var title: String?,
                                 @Nullable var userNote: String?,
                                 @Nullable var user_id: Int?,
                                 @Nullable var category_id: Int?,
                                 @Nullable var photos: ArrayList<String>,
                                 @Nullable var location: Location?,
                                 @Nullable var address: String?,
                                 @Nullable var moduleData: ArrayList<ModuleData>? ) : Parcelable {
    internal fun clearData() {
        title = null
        userNote = null
        user_id = null
        category_id = null
        photos = ArrayList()
        location = null
        address = null
        moduleData = null
    }
}