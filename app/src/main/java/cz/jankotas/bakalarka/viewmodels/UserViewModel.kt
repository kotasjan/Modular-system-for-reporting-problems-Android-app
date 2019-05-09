package cz.jankotas.bakalarka.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.jankotas.bakalarka.models.User
import cz.jankotas.bakalarka.repositories.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application)  {
    private val repository: UserRepository = UserRepository(application)
    private val userLiveData: LiveData<User> = repository.getUser()

    fun insert(user: User) {
        repository.insert(user)
    }

    fun getUser(): LiveData<User> {
        return userLiveData
    }
}