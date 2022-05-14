package cz.cvut.fel.pda.tickduck.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Categories WHERE userId = :userId ORDER BY clickCounter DESC")
    fun getAllCategoriesFlow(userId: Int): Flow<List<Category>>

    @Query("SELECT EXISTS(SELECT * FROM categories WHERE name = :name)")
    fun existsByName(name : String) : Boolean

    @Insert
    suspend fun insertCategory(vararg category: Category)

    @Query ("DELETE FROM categories WHERE id IS :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Int): Category?

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getByName(name: String): Category?
}