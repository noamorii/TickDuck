package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import cz.cvut.fel.pda.tickduck.db.repository.UserRepository
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.User
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class UserViewModel(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    suspend fun insert(user: User): User {
        userRepository.insert(user)
        val createdUser = userRepository.getByUsername(user.username)!!
        categoryRepository.insert(
            Category(
                userId = createdUser.id!!,
                name = "Inbox"
            )
        )
        return createdUser
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
                    UserRepository(mainApp.userDao),
                    CategoryRepository(mainApp.categoryDao)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}