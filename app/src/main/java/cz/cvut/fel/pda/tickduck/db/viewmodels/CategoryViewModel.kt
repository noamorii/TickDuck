package cz.cvut.fel.pda.tickduck.db.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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

    fun delete(category: Category) = viewModelScope.launch {
        categoryRepository.delete(category)
    }

}

class CategoryViewModelFactory(
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("Unchecked_cast")
            return CategoryViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown_ViewModelClass")
    }
}