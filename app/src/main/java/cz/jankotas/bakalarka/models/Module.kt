package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Module constructor(@NonNull var id: Int,
                              @NonNull var name: String,
                              @NonNull var inputs: ArrayList<Input>) : Parcelable