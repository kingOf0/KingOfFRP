package base

class Character(
    val name: String,
    val type: CharacterType
) {
    private val stats = HashMap<CharacterStat, Int>()

    init {
    }

    fun getStat(stat: CharacterStat): Int {
        return type.getStat(stat) + (stats[stat] ?: 0)
    }

}