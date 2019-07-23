package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

/**
 * Datová třída pro data získaná ze vstupů
 */
@Parcelize
data class InputData constructor(@NonNull var input_id: Int,
                                 @NonNull var value: String?) : Parcelable