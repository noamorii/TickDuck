package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    private val loggedInUserId = loggedInSharedPreferences.getInt(CURRENT_USER_ID, 0)

    val categoriesLiveData = categoryRepository.getAll(loggedInUserId).asLiveData()

    fun insert(categoryName: String) = viewModelScope.launch {
        val newCategory = Category(userId = loggedInUserId, name = categoryName)
        categoryRepository.insert(newCategory)
    }

    fun delete(id: Int) = viewModelScope.launch {
        categoryRepository.delete(id)
    }

    class CategoryViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp

                @Suppress("Unchecked_cast")
                return CategoryViewModel(
                    CategoryRepository(mainApp.categoryDao),
                    context.getSharedPreferences(SharedPreferencesKeys.CURRENT_USER_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}