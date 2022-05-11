package cz.cvut.fel.pda.tickduck.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cz.cvut.fel.pda.tickduck.db.dao.CategoryDao
import cz.cvut.fel.pda.tickduck.db.dao.TodoDao
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import cz.cvut.fel.pda.tickduck.model.User

@Database (entities = [Category::class, Todo::class, User::class], version = 1)
abstract class MainDB : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getTodoDao(): TodoDao

    companion object {
        @Volatile
        private var dbInstance: MainDB? = null
        fun getDB(context: Context): MainDB {
            return dbInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDB::class.java,
                    "tick_duck.db"
                ).build()
                instance
            }
        }
    }
}