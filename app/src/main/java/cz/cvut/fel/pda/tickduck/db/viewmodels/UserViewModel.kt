package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.UserRepository
import cz.cvut.fel.pda.tickduck.model.User
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val userLiveData = userRepository.getAll().asLiveData()

    suspend fun insert(user: User) {
        userRepository.insert(user)
    }

    fun delete(id: Int) = viewModelScope.launch {
        userRepository.delete(id)
    }

    suspend fun getByUsername(username: String): User? {
        return userRepository.getByUsername(username)
    }

    class UserViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp

                @Suppress("Unchecked_cast")
                return UserViewModel(
                    UserRepository(mainApp.userDao)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}