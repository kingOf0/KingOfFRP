package manager

import LOGGER

abstract class IManager(private val name: String) {

    internal abstract fun load() : Boolean

    fun initialize() {
        if (load()) {
            LOGGER.info("+ $name | Successfully loaded.")
        } else {
            throw IllegalStateException("- $name | Couldn't loaded successfully!").apply {
                stackTrace = stackTrace.take(5).toTypedArray()
            }
        }
    }
}