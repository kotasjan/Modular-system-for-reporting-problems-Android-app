package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location constructor(@NonNull var lat: Double,
                                @NonNull var lng: Double) : Parcelable