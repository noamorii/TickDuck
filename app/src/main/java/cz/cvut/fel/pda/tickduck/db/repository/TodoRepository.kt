package cz.cvut.fel.pda.tickduck.db.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.tickduck.db.dao.TodoDao
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch

class TodoRepository(
    private val todoDao: TodoDao
) {

    val allTodos = todoDao.getAllTodosFlow()

    @WorkerThread
    suspend fun insert(vararg todo: Todo) {
        todoDao.insert(*todo)
    }

    @WorkerThread
    suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        todoDao.delete(id)
    }
}