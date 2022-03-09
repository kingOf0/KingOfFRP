package manager

object SettingsManager : IManager("SettingsManager") {

    var insantiateDefaults: Boolean = false

    override fun load(): Boolean {

        return true
    }


}