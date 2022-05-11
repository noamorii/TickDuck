package cz.cvut.fel.pda.tickduck

import android.app.Application
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.db.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApp : Application() {
    val database by lazy { MainDB.getDB(this) }
    val categoryRepository by lazy { CategoryRepository(database.getCategoryDao()) }

}