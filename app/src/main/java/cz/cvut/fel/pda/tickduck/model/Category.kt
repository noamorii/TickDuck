package cz.cvut.fel.pda.tickduck.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class Category(
    @PrimaryKey (autoGenerate = true) val id: Int?,
    val name: String,
    val todos: List<Todo> = ArrayList()
)
