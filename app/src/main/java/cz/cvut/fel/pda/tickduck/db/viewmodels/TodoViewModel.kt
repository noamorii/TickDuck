package cz.cvut.fel.pda.tickduck.db.viewmodels

import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TodoViewModel(
    database: MainDB
) : ViewModel() {
    private val dao = database.getDao()

    val allCategories = dao.getAllCategoriesFlow().asLiveData()
    val allTodos = dao.getAllTodos().asLiveData()

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

    fun categoryExists(name: String): Boolean {
        return allCategories.value?.map { it.name }!!
            .contains(name)
    }
}

class TodoViewModelFactory(
    private val database: MainDB
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("Unchecked_cast")
            return TodoViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown_ViewModelClass")
    }
}