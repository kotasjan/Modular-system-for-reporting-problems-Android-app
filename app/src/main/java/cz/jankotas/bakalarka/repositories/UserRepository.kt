package cz.jankotas.bakalarka.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import cz.jankotas.bakalarka.databases.UserDatabase
import cz.jankotas.bakalarka.databases.daos.UserDao
import cz.jankotas.bakalarka.models.User

class UserRepository(application: Application) {

    private var userDao: UserDao

    private var user: LiveData<User>

    init {
        val database: UserDatabase = UserDatabase.getInstance(
            application.applicationContext
        )!!
        userDao = database.userDao()
        user = userDao.getUser()
    }

    fun insert(user: User) {
        InsertUserAsyncTask(userDao).execute(user)
    }

    fun getUser(): LiveData<User> {
        return user
    }

    private class InsertUserAsyncTask(val userDao: UserDao) : AsyncTask<User, Unit, Unit>() {

        override fun doInBackground(vararg p0: User?) {
            userDao.insert(p0[0]!!)
        }
    }


    /*
    private class asyncTask internal constructor(private val userDao: UserDao) : AsyncTask<Void, Void, LiveData<User>>() {

        override fun doInBackground(vararg params: Void): LiveData<User> {
            return userDao.getUser()
        }
    }
    */
}