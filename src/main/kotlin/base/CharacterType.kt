package base
class CharacterType(
    val id: String
) {
    var name = ""
    var baseHealth = 0
    var stats = HashMap<CharacterStat, Int>()

    fun getStat(stats: CharacterStat) : Int {
        return this.stats[stats] ?: 0
    }

    fun setStat(stat: CharacterStat, value: Int) {
        this.stats[stat] = value
    }

}