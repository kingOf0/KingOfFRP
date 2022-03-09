// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import base.CharacterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import manager.*
import java.util.logging.Logger

val LOGGER = Logger.getLogger("K FRP")

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = { text = "Hello, Desktop!" }) {
            Text(text)
        }
    }
}

fun main() = application {
    FileManager.initialize()
    DatabaseManager.initialize()

    CoroutineScope(Dispatchers.IO).launch {
        DatabaseManager.setup()

        CharacterStatManager.initialize()
        CharacterTypeManager.initialize()
        CharacterManager.initialize()
    }.invokeOnCompletion {
        if (it != null) {
            it.printStackTrace()
            return@invokeOnCompletion
        }
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }

}
