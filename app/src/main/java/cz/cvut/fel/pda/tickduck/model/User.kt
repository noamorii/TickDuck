package cz.cvut.fel.pda.tickduck.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val username: String,
    val password: String
)