package cz.jankotas.bakalarka.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.repositories.UserRepository

/**
 * Třída, která slouží jako ViewModel v architektuře MVVM. Cílem třídy je zprostředkovávat data uživatele mezi repository
 * a observery.
 */
class UserViewModel(application: Application) : AndroidViewModel(application)  {
    private val repository: UserRepository = UserRepository(application)
    private val userLiveData: LiveData<User> = repository.getUser()

    // vložit uživatele do DB
    fun insert(user: User) {
        repository.insert(user)
    }

    // získat záznam uživatele
    fun getUser(): LiveData<User> {
        return userLiveData
    }
}