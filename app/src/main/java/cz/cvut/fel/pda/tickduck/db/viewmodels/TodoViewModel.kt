package cz.cvut.fel.pda.tickduck.db.viewmodels

import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch

class TodoViewModel(
    database: MainDB
) : ViewModel() {
    private val dao = database.getDao()
    val allCategories: LiveData<List<Category>> = dao.getAllCategories().asLiveData()

    val allTodos: LiveData<List<Todo>> = dao.getAllTodos().asLiveData()

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
}