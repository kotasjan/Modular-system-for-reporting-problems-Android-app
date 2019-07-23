package cz.jankotas.bakalarka.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import cz.jankotas.bakalarka.databases.UserDatabase
import cz.jankotas.bakalarka.databases.daos.UserDao
import cz.jankotas.bakalarka.models.User

/**
 * Repository je další část architektury, která slouží jako místo uchovávající data modelu s cílem poskytnout k nim přístup
 * z různých částí aplikace nebo jejích služeb. V tomto případě je třeba získat data uživatele z databáze a předat je ViewModelu.
 * Připadně naopak data do databáze vložit.
 */
class UserRepository(application: Application) {

    private var userDao: UserDao // Dao rozhraní knihovny Room

    private var user: LiveData<User> // LiveData objektu User

    init {
        // získání instance databáze
        val database: UserDatabase = UserDatabase.getInstance(
            application.applicationContext
        )!!
        // získání Dao rozhraní databáze potřebné pro komunikaci
        userDao = database.userDao()

        // získání uživatele skrze Dao rozhranní
        user = userDao.getUser()
    }

    fun insert(user: User) {
        InsertUserAsyncTask(userDao).execute(user)
    }

    // získání uživatele z ViewModelu
    fun getUser(): LiveData<User> {
        return user
    }

    // vložení uživatele do databáze musí proběhnout asynchronně (na pozadí)
    private class InsertUserAsyncTask(val userDao: UserDao) : AsyncTask<User, Unit, Unit>() {

        override fun doInBackground(vararg p0: User?) {
            userDao.deleteAll() // uživatel je pouze jeden, proto lze použít deleteAll()
            userDao.insert(p0[0]!!) // vložení uživatele do databáze
        }
    }
}