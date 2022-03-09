package base
class CharacterType(
    private val name: String
) {

    var baseHealth = 0
    val stats = HashMap<CharacterStat, Int>()

    fun getStat(stats: CharacterStat) : Int {
        return this.stats[stats] ?: 0
    }

    fun setStat(stats: CharacterStat, value: Int) {
        this.stats[stats] = value
    }

    fun getName(): String {
        return name
    }

}