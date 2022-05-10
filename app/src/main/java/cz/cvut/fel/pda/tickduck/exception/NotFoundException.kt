package cz.cvut.fel.pda.tickduck.exception

import java.lang.RuntimeException

class NotFoundException : RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, throwable: Throwable): super(message, throwable)
}