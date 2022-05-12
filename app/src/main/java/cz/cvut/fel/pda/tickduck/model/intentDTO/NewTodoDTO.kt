package cz.cvut.fel.pda.tickduck.model.intentDTO

import cz.cvut.fel.pda.tickduck.model.enums.FlagType
import java.io.Serializable

data class NewTodoDTO(
    val name: String,
    val description: String,
    val date: String?,
    val time: String?,
    val flagInfo: FlagType,
    val idCategory: Int
) : Serializable