package base

import manager.CharacterTypeManager

class Character(val type: CharacterType, var name: String, var stats: MutableMap<CharacterStat, Int> = HashMap(), var maxHealth: Int, var level: Int) {

    var health: Int = maxHealth

    constructor(type: CharacterType, name: String) : this(type, name, HashMap(), type.baseHealth, 1)
    constructor(type: CharacterType) : this(type, "", HashMap(), type.baseHealth, 1)
    constructor() : this(CharacterTypeManager.default, "", HashMap(), CharacterTypeManager.default.baseHealth, 1)

    fun getCharacterStat(stat: CharacterStat): Int {
        return (stats[stat] ?: 0)
    }

    fun getStat(stat: CharacterStat): Int {
        return getCharacterStat(stat) + getBaseStat(stat)
    }

    fun getBaseStat(stat: CharacterStat): Int {
        return type.getStat(stat)
    }

    fun setStat(stat: CharacterStat, value: Int) {
        stats[stat] = value
    }

    fun resetHealth() {

    }

    fun setStat(stats: java.util.HashMap<CharacterStat, Int>) {
        this.stats.putAll(stats)
    }

}