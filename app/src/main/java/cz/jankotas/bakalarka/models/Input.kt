package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Input constructor(@NonNull var id: Int,
                             @NonNull var inputType: String,
                             @NonNull var title: String,
                             @Nullable var characters: Int?,
                             @Nullable var hint: String?,
                             @Nullable var items: ArrayList<Item>?) : Parcelable