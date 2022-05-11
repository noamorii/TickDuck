package cz.cvut.fel.pda.tickduck.db.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.tickduck.db.dao.CategoryDao
import cz.cvut.fel.pda.tickduck.model.Category

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    val allCategories = categoryDao.getAllCategoriesFlow()

    @WorkerThread
    suspend fun insert(vararg category: Category) {
        categoryDao.insertCategory(*category)
    }

    @WorkerThread
    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }

}