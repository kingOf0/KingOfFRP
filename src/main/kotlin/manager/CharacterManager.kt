package manager

import base.Character
import base.CharacterStat
import base.CharacterType

object CharacterManager : IManager("CharacterManager") {

    val characters = HashMap<String, Character>()

    override fun load(): Boolean {
        return true
    }

    fun add(character: Character) {
        characters[character.id] = (character)
    }

}

class CharacterBuilder(id: String, type: CharacterType) {

    constructor(id: String) : this(id, CharacterTypeManager.default)

    private var character = Character(id, type)

    fun setStat(stat: CharacterStat, value: Int): CharacterBuilder {
        character.setStat(stat, value)
        return this
    }

    fun setHealth(health: Int): CharacterBuilder {
        character.health = health
        return this
    }

    fun setType(type: CharacterType) {
        character.type = type
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
        CharacterManager.characters[character.id] = character
        return this
    }

}