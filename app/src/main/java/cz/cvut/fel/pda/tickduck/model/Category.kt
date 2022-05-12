package cz.cvut.fel.pda.tickduck.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class Category(
    @PrimaryKey (autoGenerate = true)
    val id: Int? = null,

    val userId: Int,

    val name: String,

    val clickCounter: Long = 0
)