package cz.cvut.fel.pda.tickduck.db.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.tickduck.db.dao.CategoryDao
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.User
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    fun getAll(userId: Int): Flow<List<Category>> {
        return categoryDao.getAllCategoriesFlow(userId)
    }

    @WorkerThread
    suspend fun insert(vararg category: Category) {
        categoryDao.insertCategory(*category)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        categoryDao.delete(id)
    }

    @WorkerThread
    fun existsByName(name: String): Boolean {
        return categoryDao.existsByName(name)
    }

    @WorkerThread
    suspend fun getById(id: Int): Category? {
        return categoryDao.getById(id)
    }
}