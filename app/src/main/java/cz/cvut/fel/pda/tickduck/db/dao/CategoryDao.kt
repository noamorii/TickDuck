package cz.cvut.fel.pda.tickduck.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.pda.tickduck.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Categories ORDER BY clickCounter DESC")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(vararg category: Category)

    @Delete
    suspend fun delete(category: Category)

}