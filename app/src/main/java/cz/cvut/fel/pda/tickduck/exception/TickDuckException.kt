package cz.cvut.fel.pda.tickduck.exception

import java.lang.RuntimeException

open class TickDuckException : RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, throwable: Throwable): super(message, throwable)
}