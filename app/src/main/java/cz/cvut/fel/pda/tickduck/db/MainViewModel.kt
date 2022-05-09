package cz.cvut.fel.pda.tickduck.db

import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch

class MainViewModel(database: MainDB) : ViewModel() {
    private val dao = database.getDao()

    val allTodos: LiveData<List<Todo>> = dao.getAllTodos().asLiveData()
    val allCategories: LiveData<List<Category>> = dao.getAllCategories().asLiveData()

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        dao.insertTodo(todo)
    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        dao.insertCategory(category)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        dao.updateTodo(todo)
    }

    fun deleteTodo(id: Int) = viewModelScope.launch {
        dao.deleteTodo(id)
    }

    fun findCategory(name: String): Category? {
        if (allCategories.value != null) {
            for (category: Category in allCategories.value!!) {
                if (category.name == name) {
                    return category;
                }
            }
        }

        return null
    }

    class MainViewModelFactory(private val database: MainDB) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("Unchecked_cast")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }

    }
}