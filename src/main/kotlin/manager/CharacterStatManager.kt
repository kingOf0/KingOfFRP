package manager

import base.CharacterStat

object CharacterStatManager : IManager("CharacterStatManager") {

    val stats = HashMap<String, CharacterStat>()

    override fun load(): Boolean {
        BaseStat.values()
        return true
    }

    enum class BaseStat(private val s: String) : CharacterStat {
        STRENGTH("Kuvvet"),
        DEFENCE("Defans"),
        INTELLIGENCE("Bilgi"),
        LUCK("Şans"),
        MAGIC_POWER("Sihir"),
        CHARISMA("Karizma"),
        AGILITY("Çeviklik");

        init {
            stats[this.name] = this
        }

        override fun getName(): String {
            return s;
        }
    }

}