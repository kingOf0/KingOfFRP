package manager

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap

object SettingsManager : IManager("SettingsManager") {

    var backgroundImage = mutableStateOf(ImageBitmap(1, 1))
    var insantiateDefaults: Boolean = false
    var startStreamWindow: Boolean = false

    override fun load(): Boolean {

        return true
    }


}