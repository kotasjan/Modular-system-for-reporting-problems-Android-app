package cz.jankotas.bakalarka.models

import androidx.annotation.Nullable

/**
 * Odpověď serveru na nahlášenou chybu se uloží v podobě objektu této třídy.
 */
data class APIBugResponse(@Nullable val error: Boolean)