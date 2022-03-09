package manager

import base.Character
import base.CharacterStat
import base.CharacterType

object CharacterManager : IManager("CharacterManager") {

    val characters = ArrayList<Character>()

    override fun load(): Boolean {
        return true
    }

    fun add(character: Character) {
        characters.add(character)
    }

}

class CharacterBuilder(type: CharacterType) {

    private var character = Character(type)

    fun setStat(stat: CharacterStat, value: Int): CharacterBuilder {
        character.setStat(stat, value)
        return this
    }

    fun setHealth(health: Int): CharacterBuilder {
        character.health = health
        return this
    }

    fun setName(name: String): CharacterBuilder {
        character.name = name
        return this
    }

    fun getCharacter(): Character {
        return character
    }

    suspend fun save(): CharacterBuilder {
        DatabaseManager.save(character)
        return this
    }

    fun register(): CharacterBuilder {
        CharacterManager.characters += character
        return this
    }

}