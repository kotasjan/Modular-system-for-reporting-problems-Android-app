package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

data class Bug constructor(@NonNull var description: String)

data class BugPOJO constructor(@NonNull var bug: Bug)