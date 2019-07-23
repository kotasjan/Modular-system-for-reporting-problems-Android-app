package cz.jankotas.bakalarka.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.jankotas.bakalarka.databases.converters.DateTypeConverter
import cz.jankotas.bakalarka.databases.daos.UserDao
import cz.jankotas.bakalarka.models.User

/**
 * Databáze v rámci knihovny Room. Tato abstraktní třída inicializuje databázi.
 */
@Database(entities = [User::class], version = 2, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase? {
            if (instance == null) {
                synchronized(UserDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        UserDatabase::class.java,
                        "database").fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }
    }
}