package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * Odpověď serveru na žádost o seznam podnětů se uloží v podobě objektu této třídy.
 */
data class APIReportsResponse constructor(@NonNull val error: Boolean,
                                        @Nullable val reports: List<Report>)