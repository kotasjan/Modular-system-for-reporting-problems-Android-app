package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

/**
 * Odpověď serveru na požadavek na konkrétní podnět se uloží v podobě objektu této třídy.
 */
data class APIReportResponse(@NonNull val error: Boolean)