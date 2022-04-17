package cz.cvut.fel.pda.tickduck.model.enums

enum class FlagType(val color: Int, val priority: Int) {

    RED(0xED6C50, 1),

    YELLOW(0xBED50, 2),

    BLUE(0x00C2FF, 3)
}