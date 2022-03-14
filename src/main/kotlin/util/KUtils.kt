package util

import base.CharacterStat
import manager.CharacterStatManager
import java.sql.ResultSet

object KUtils {

    fun Map<*, *>.serialize(): String {
        return entries.joinToString(",") { "${it.key}:${it.value}" }
    }

    fun ResultSet.getStats(column: String): HashMap<CharacterStat, Int> {
        val stats = getString(column)
        if (stats.isEmpty()) return hashMapOf()
        return HashMap(stats.split(",").associate {
            val split = it.split(":")
            Pair(CharacterStatManager.stats[split[0]]!!, split[1].toInt())
        })
    }

}