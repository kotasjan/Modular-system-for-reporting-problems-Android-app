package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

/**
 * Odpověď serveru na dotaz ohledně aktivních modulů samosprávy se uloží v podobě objektu této třídy.
 */
data class APIModuleResponse constructor(@NonNull val error:Boolean,
                                         @NonNull val modules: ArrayList<Module>)