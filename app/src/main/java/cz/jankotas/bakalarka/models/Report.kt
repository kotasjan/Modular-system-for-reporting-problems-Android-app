package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.util.*

data class Report constructor(@NonNull val id: Int,
                              @NonNull val created_at: Date,
                              @NonNull val updated_at: Date,
                              @NonNull val title: String,
                              @NonNull val state: Int,
                              @Nullable val userNote: String?,
                              @Nullable val employeeNote: String?,
                              @NonNull val address: String,
                              @NonNull val user_id: Int,
                              @Nullable val responsible_user_id: Int?,
                              @NonNull val category_id: Int,
                              @NonNull val territory_id: Int,
                              @NonNull val distance: Double,
                              @Nullable val photos: List<String>?,
                              @NonNull val location: Location,
                              @NonNull val comments: Int,
                              @NonNull val likes: Int,
                              @NonNull val userLike: Boolean)