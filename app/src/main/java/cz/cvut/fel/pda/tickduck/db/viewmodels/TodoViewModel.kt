package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import cz.cvut.fel.pda.tickduck.db.repository.TodoRepository
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TodoViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val allCategoriesLiveData = categoryRepository.allCategories.asLiveData()
    val allTodosLiveData = todoRepository.allTodos.asLiveData()

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.insert(todo)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.update(todo)
    }

    fun deleteTodo(id: Int) = viewModelScope.launch {
        todoRepository.delete(id)
    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        categoryRepository.insert(category)
    }

    fun categoryExists(name: String) = viewModelScope.launch {
        categoryRepository.existsByName(name)
    }

    class TodoViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp

                @Suppress("Unchecked_cast")
                return TodoViewModel(
                    TodoRepository(mainApp.todoDao),
                    CategoryRepository(mainApp.categoryDao)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}