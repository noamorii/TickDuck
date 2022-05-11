package cz.cvut.fel.pda.tickduck.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query ("SELECT * FROM Categories ORDER BY clickCounter DESC")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category)

    @Query ("SELECT * FROM Todos")
    fun getAllTodos(): Flow<List<Todo>>

    @Insert
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query ("DELETE FROM todos WHERE id IS :id")
    suspend fun deleteTodo(id: Int)
}