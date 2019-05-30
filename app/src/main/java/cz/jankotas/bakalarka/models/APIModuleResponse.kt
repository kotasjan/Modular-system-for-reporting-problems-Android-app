package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

data class APIModuleResponse constructor(@NonNull val error:Boolean,
                                         @NonNull val modules: ArrayList<Module>)