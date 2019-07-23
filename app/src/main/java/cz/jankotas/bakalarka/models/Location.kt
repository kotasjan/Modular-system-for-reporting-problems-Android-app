package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

/**
 * Datová třída pro uchování souřadnic (polohy)
 */
@Parcelize
data class Location constructor(@NonNull var lat: Double,
                                @NonNull var lng: Double) : Parcelable