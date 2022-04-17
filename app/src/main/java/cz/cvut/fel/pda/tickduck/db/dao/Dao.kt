package cz.cvut.fel.pda.tickduck.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.pda.tickduck.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query ("SELECT * FROM Categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category)
}