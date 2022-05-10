package cz.cvut.fel.pda.tickduck.db.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.db.viewmodels.MainViewModel
import java.lang.IllegalArgumentException

class MainViewModelFactory(
    private val database: MainDB
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("Unchecked_cast")
            return MainViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown_ViewModelClass")
    }
}