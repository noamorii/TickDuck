package cz.cvut.fel.pda.tickduck.model.enums

import android.content.Context
import androidx.core.content.ContextCompat
import cz.cvut.fel.pda.tickduck.R

enum class PriorityEnum(
    val color: Int,
    val priority: Int,
    val text: String
) {

    LOW(R.color.low_priority, 1, "low"),
    MEDIUM(R.color.medium_priority, 2, "medium"),
    HIGH(R.color.high_priority, 3, "high");

    fun toArgb(context: Context) = ContextCompat.getColor(context, this.color)
}