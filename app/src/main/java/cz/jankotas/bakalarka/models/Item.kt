package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item constructor(@NonNull var id: Int,
                             @NonNull var text: String) : Parcelable