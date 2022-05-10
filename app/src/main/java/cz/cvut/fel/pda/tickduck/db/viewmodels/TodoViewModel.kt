package cz.cvut.fel.pda.tickduck.db.viewmodels

import androidx.lifecycle.*
import cz.cvut.fel.pda.tickduck.db.MainDB
import cz.cvut.fel.pda.tickduck.exception.NotFoundException
import cz.cvut.fel.pda.tickduck.model.Category
import cz.cvut.fel.pda.tickduck.model.Todo
import kotlinx.coroutines.launch

class TodoViewModel(
    database: MainDB
) : ViewModel() {
    private val dao = database.getDao()

    val allCategories = dao.getAllCategories().asLiveData()
    val allTodos = dao.getAllTodos().asLiveData()

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        dao.insertTodo(todo)
    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        dao.insertCategory(category)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        dao.updateTodo(todo)
    }

    fun deleteTodo(id: Int) = viewModelScope.launch {
        dao.deleteTodo(id)
    }

    // todo splnuje ocekavane chovaní?
    fun findCategory(name: String): Category {
        return allCategories.value?.stream()!!
            .filter { it.name == name }
            .findFirst()
            .orElseThrow { NotFoundException("Category not found.") }

//        if (allCategories.value != null) {
//            for (category: Category in allCategories.value!!) {
//                if (category.name == name) {
//                    return category;
//                }
//            }
//        }
//        return null
    }

    fun categoryExists(name: String): Boolean {
        return allCategories.value?.map { it.name }!!
            .contains(name)
    }
}