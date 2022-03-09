package manager

import base.CharacterStat
import base.CharacterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CharacterTypeManager : IManager("CharacterTypeManager") {

    val types = HashMap<String, CharacterType>()
    val default = CharacterType("default")

    override fun load(): Boolean {
        default
        return true
    }

    fun save() {
        CoroutineScope(Dispatchers.IO).launch {
            for (type in types) {
                DatabaseManager.save(type.key, type.value)
            }
        }.invokeOnCompletion {
            it?.printStackTrace()
        }
    }

    fun addType(id: String, type: CharacterType) {
        types[id] = type
    }




}

class CharacterTypeBuilder(private val id: String, name: String) {

    private val type = CharacterType(name)

    fun setStat(stat: CharacterStat, value: Int): CharacterTypeBuilder {
        type.setStat(stat, value)
        return this
    }

    fun setHealth(health: Int): CharacterTypeBuilder {
        type.baseHealth = health
        return this
    }

    fun getType() : CharacterType {
        return type;
    }

    fun register(): CharacterTypeBuilder {
        CharacterTypeManager.addType(id, type)
        return this
    }

    suspend fun save() {
        DatabaseManager.save(id, type)
    }

}