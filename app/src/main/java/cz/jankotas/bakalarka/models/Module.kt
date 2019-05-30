package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

data class Module constructor(@NonNull var module_id: Int,
                              @NonNull var name: String,
                              @NonNull var inputs: ArrayList<Input>)