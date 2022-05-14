package cz.cvut.fel.pda.tickduck.model.intentDTO

import cz.cvut.fel.pda.tickduck.model.enums.PriorityEnum
import java.io.Serializable

data class NewTodoDTO(
    val name: String,
    val description: String,
    val date: String?,
    val flagInfo: PriorityEnum,
    val idCategory: Int
) : Serializable