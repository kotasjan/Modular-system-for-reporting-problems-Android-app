package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

/**
 * Datová třída pro položky seznamu (SelectBoxu/spinneru)
 */
@Parcelize
data class Item constructor(@NonNull var id: Int,
                             @NonNull var text: String) : Parcelable