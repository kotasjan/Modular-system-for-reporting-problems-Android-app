package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull
import androidx.annotation.Nullable

data class APIReportResponse constructor(@NonNull val error: Boolean,
                                        @Nullable val reports: List<Report>)