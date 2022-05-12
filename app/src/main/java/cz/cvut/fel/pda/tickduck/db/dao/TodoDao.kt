package cz.cvut.fel.pda.tickduck.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM Todos WHERE userId = :userId")
    fun getAllTodosFlow(userId: Int): Flow<List<Todo>>

    @Insert
    suspend fun insert(vararg todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("DELETE FROM todos WHERE id IS :id")
    suspend fun delete(id: Int)

}