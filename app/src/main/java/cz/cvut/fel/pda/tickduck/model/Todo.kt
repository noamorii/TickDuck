package cz.cvut.fel.pda.tickduck.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.cvut.fel.pda.tickduck.model.enums.PriorityEnum
import java.io.Serializable

@Entity (tableName = "Todos")
data class Todo(
    @PrimaryKey (autoGenerate = true)
    val id: Int? = null,

    val name: String,

    val description: String,

    val date: String?,

    val userId: Int,

    @ColumnInfo(name = "flag_type")
    val flagInfo: PriorityEnum,

    val categoryId: Int,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false
): Serializable