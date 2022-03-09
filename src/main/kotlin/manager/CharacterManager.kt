package manager

import base.Character

object CharacterManager : IManager("CharacterManager") {

    val characters = HashMap<String, Character>()

    override fun load(): Boolean {
        return true
    }

}