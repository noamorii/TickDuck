package cz.cvut.fel.pda.tickduck.db.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.tickduck.db.dao.UserDao
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    fun getAll(): Flow<List<User>> {
        return userDao.getAllUsersFlow()
    }

    @WorkerThread
    suspend fun insert(vararg user: User) {
        userDao.insert(*user)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        userDao.delete(id)
    }

    @WorkerThread
    suspend fun getByUsername(name: String): User? {
        return userDao.getByUsername(name)
    }
}