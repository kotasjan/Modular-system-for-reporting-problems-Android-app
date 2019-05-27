package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewReport constructor(@Nullable var title: String?,
                                 @Nullable var userNote: String?,
                                 @Nullable var user_id: Int?,
                                 @Nullable var category_id: Int?,
                                 @Nullable var photos: List<String>?,
                                 @Nullable var location: Location?,
                                 @Nullable var moduleData: String? ) : Parcelable