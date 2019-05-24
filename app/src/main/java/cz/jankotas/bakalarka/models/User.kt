package cz.jankotas.bakalarka.models

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "users")
data class User constructor(@PrimaryKey @NonNull var id: Int,
                            @Nullable var avatarURL: String?,
                            @NonNull var name: String,
                            @NonNull var email: String,
                            @Nullable var email_verified_at: Date?,
                            @Nullable var password: String?,
                            @NonNull var telephone: Int,
                            @NonNull var isSuperAdmin: Boolean = false,
                            @NonNull var isEmployee: Boolean = false,
                            @NonNull var isSupervisor: Boolean = false,
                            @NonNull var created_at: String?,
                            @NonNull var updated_at: String?)