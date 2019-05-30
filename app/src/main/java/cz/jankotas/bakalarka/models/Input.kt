package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull
import androidx.annotation.Nullable

data class Input constructor(@NonNull var input_id: Int,
                             @NonNull var inputType: String,
                             @NonNull var title: String,
                             @Nullable var characters: Int?,
                             @Nullable var hint: String?,
                             @Nullable var items: ArrayList<Item>?)