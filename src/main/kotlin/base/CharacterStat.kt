package base

interface CharacterStat {

    fun getName(): String

}

class CustomCharacterStat(private val name: String) : CharacterStat {
    override fun getName(): String {
        return name
    }

}