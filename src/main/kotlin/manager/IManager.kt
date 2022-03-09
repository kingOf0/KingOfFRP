package manager

import LOGGER

abstract class IManager(private val name: String) {

    protected abstract fun load() : Boolean

    fun initialize() {
        if (load()) {
            LOGGER.info("+ $name | Successfully loaded.")
        } else {
            LOGGER.warning("- $name | Couldn't loaded successfully!")
        }
    }
}