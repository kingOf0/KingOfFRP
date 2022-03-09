package manager

import base.CharacterStat

object CharacterStatManager : IManager("CharacterStatManager") {

    private val stats = HashMap<String, CharacterStat>()

    override fun load(): Boolean {
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