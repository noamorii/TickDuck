package cz.cvut.fel.pda.tickduck.db.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.MainApp
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import cz.cvut.fel.pda.tickduck.db.repository.TodoRepository
import cz.cvut.fel.pda.tickduck.db.repository.UserRepository
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.User
import cz.cvut.fel.pda.tickduck.model.intentDTO.NewTodoDTO
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_ID
import cz.cvut.fel.pda.tickduck.utils.SharedPreferencesKeys.CURRENT_USER_PREFERENCES
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException

class TodoViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    private val loggedInUserId = loggedInSharedPreferences.getInt(CURRENT_USER_ID, 0)

    val allCategoriesLiveData = categoryRepository.getAll(loggedInUserId).asLiveData()
    val allTodosLiveData = todoRepository.getAll(loggedInUserId).asLiveData()

    var loggedUser: User? = null
    init {
        val loggedInUserId = loggedInSharedPreferences.getInt(CURRENT_USER_ID, 0)
        if (loggedInUserId != 0) {
            runBlocking {
                loggedUser = userRepository.getByUserId(loggedInUserId)
            }
        }
    }

    fun insertTodo(newTodoDTO: NewTodoDTO) = viewModelScope.launch {
        val newTodo = Todo(
            name = newTodoDTO.name,
            description = newTodoDTO.description,
            date = newTodoDTO.date,
            time = newTodoDTO.time,
            userId = loggedInUserId,
            flagInfo = newTodoDTO.flagInfo,
            idCategory = newTodoDTO.idCategory
        )
        todoRepository.insert(newTodo)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.update(todo)
    }

    fun deleteTodo(id: Int) = viewModelScope.launch {
        todoRepository.delete(id)
    }

    fun insertCategory(categoryName: String) = viewModelScope.launch {
        val newCategory = Category(userId = loggedInUserId, name = categoryName)
        categoryRepository.insert(newCategory)
    }

    fun categoryExists(name: String): Boolean {
        return allCategoriesLiveData.value?.map { it.name }!!
            .contains(name)
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
                    CategoryRepository(mainApp.categoryDao),
                    UserRepository(mainApp.userDao),
                    context.getSharedPreferences(CURRENT_USER_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}