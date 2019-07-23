package cz.jankotas.bakalarka.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.jankotas.bakalarka.models.User

/**
 * UserDao je rozhranní v rámci architektury knihovny Room pro komunikaci aplikace s SQLite databází.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User) // vložit záznam uživatele

    @Delete
    fun delete(user: User) // odstranit jednoho uživatele

    @Query("DELETE FROM users")
    fun deleteAll() // odstranit všechny uživatele

    @Query("SELECT * FROM users")
    fun getUser(): LiveData<User> // získat záznam uživatele
}