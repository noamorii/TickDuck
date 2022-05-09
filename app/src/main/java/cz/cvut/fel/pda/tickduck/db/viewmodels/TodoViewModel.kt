package cz.cvut.fel.pda.tickduck.db.viewmodels

import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch

class TodoViewModel(
    database: MainDB
) : ViewModel() {
    private val dao = database.getDao()

    val allTodos: LiveData<List<Todo>> = dao.getAllTodos().asLiveData()

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        dao.insertTodo(todo)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        dao.updateTodo(todo)
    }

    fun deleteTodo(id: Int) = viewModelScope.launch {
        dao.deleteTodo(id)
    }
}