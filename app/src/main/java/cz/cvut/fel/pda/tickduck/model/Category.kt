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


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}