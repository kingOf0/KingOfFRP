package base

import manager.CharacterTypeManager

class Character(val id: String, var type: CharacterType, var name: String, var characterStats: MutableMap<CharacterStat, Int> = HashMap(), var maxHealth: Int, var level: Int) {

    var health: Int = maxHealth

    constructor(id: String, type: CharacterType, name: String) : this(id, type, name, HashMap(), type.baseHealth, 1)
    constructor(id: String, type: CharacterType) : this(id, type, "", HashMap(), type.baseHealth, 1)
    constructor(id: String) : this(id, CharacterTypeManager.default, "", HashMap(), CharacterTypeManager.default.baseHealth, 1)

    fun getCharacterStat(stat: CharacterStat): Int {
        return (characterStats[stat] ?: 0)
    }

    fun getStat(stat: CharacterStat): Int {
        return getCharacterStat(stat) + getBaseStat(stat)
    }

    fun getBaseStat(stat: CharacterStat): Int {
        return type.getStat(stat)
    }

    fun setStat(stat: CharacterStat, value: Int) {
        characterStats[stat] = value
    }

    fun resetHealth() {

    }

    fun setStat(stats: java.util.HashMap<CharacterStat, Int>) {
        this.characterStats.putAll(stats)
    }

    fun getStats(): Map<CharacterStat, Int> {
        return HashMap(characterStats).apply {
            for (stat in type.stats) {
                 this[stat.key] = stat.value + (this[stat.key] ?: 0)
            }
        }.toSortedMap(compareByDescending { it.getName() })
    }


}