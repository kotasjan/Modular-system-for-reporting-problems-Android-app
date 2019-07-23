package cz.jankotas.bakalarka.models

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

/**
 * Datová třída pro data modulů
 */
@Parcelize
data class ModuleData constructor(@NonNull var module_id: Int,
                                  @NonNull var inputData: ArrayList<InputData>?) : Parcelable