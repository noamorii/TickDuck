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

    var name: String,

    var description: String,

    var date: String?,

    val userId: Int,

    @ColumnInfo(name = "priority_enum")
    var priorityEnum: PriorityEnum,

    var categoryId: Int,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false
): Serializable