package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import cz.cvut.fel.pda.tickduck.db.repository.UserRepository
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.User
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException

class UserViewModel(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    var loggedUser: User? = null

    init {
        val loggedInUserId = loggedInSharedPreferences.getInt(CURRENT_USER_ID, 0)
        if (loggedInUserId != 0) {
            runBlocking {
                loggedUser = userRepository.getByUserId(loggedInUserId)
            }
        }
    }

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

    fun updateUser() = viewModelScope.launch {
        userRepository.update(loggedUser!!)
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
                    CategoryRepository(mainApp.categoryDao),
                    context.getSharedPreferences(SharedPreferencesKeys.CURRENT_USER_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}