package cz.cvut.fel.pda.tickduck.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<User>>

    @Insert
    suspend fun insert(vararg user: User)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getByUsername(username: String): User?

}