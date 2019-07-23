package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

/**
 * Datová třída pro hlášené chyby aplikace
 */
data class Bug constructor(@NonNull var description: String)

/**
 * POJO třídy Bug
 */
data class BugPOJO constructor(@NonNull var bug: Bug)