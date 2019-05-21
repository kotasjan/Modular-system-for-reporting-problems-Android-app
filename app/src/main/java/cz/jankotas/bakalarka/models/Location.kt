package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull

data class Location constructor(@NonNull var lat: Double,
                                @NonNull var lng: Double)