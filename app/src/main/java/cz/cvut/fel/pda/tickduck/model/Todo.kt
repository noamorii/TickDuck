package cz.cvut.fel.pda.tickduck.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.cvut.fel.pda.tickduck.model.enums.FlagType
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Entity (tableName = "Todos")
data class Todo(
    @PrimaryKey (autoGenerate = true)
    val id: Int? = null,

    val name: String,

    val description: String,

    val date: String?,
    val time: String?,

    val userId: Int,

    @ColumnInfo(name = "flag_type")
    val flagInfo: FlagType,

    @ColumnInfo(name = "img_name")
    val imgName: String?, // Bitmap? (?)

    val idCategory: Int,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false
): Serializable