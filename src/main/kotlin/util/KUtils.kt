package util

object KUtils {

    fun Map<*, *>.serialize(): String {
        return entries.joinToString(",") { "${it.key}:${it.value}" }
    }

}