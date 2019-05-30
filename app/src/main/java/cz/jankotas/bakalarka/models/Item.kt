package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

data class Item constructor(@NonNull var item_id: Int,
                             @NonNull var name: String)