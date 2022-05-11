package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import cz.cvut.fel.pda.tickduck.model.Category
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categoriesLiveData = categoryRepository.allCategories.asLiveData()

    fun insert(vararg category: Category) = viewModelScope.launch {
        categoryRepository.insert(*category)
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
                return CategoryViewModel(CategoryRepository(mainApp.categoryDao)) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}