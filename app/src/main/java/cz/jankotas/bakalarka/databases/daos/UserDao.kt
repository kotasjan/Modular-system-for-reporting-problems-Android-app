package cz.jankotas.bakalarka.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.jankotas.bakalarka.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Query("SELECT * FROM users")
    fun getUser(): LiveData<User>
}