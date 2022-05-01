package cz.cvut.fel.pda.tickduck.db

import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(database: MainDB) : ViewModel() {
    private val dao = database.getDao()

    val allTodos: LiveData<List<Todo>> = dao.getAllTodos().asLiveData()

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        dao.insertTodo(todo)
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