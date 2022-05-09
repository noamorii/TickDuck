package cz.cvut.fel.pda.tickduck.db.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.db.viewmodels.TodoViewModel
import java.lang.IllegalArgumentException

class TodoViewModelFactory(
    private val database: MainDB
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("Unchecked_cast")
            return TodoViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown_ViewModelClass")
    }
}