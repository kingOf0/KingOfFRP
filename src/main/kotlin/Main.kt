// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import base.Character
import kotlinx.coroutines.runBlocking
import manager.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileReader
import java.util.*
import java.util.logging.Logger
import kotlin.math.min

val LOGGER = Logger.getLogger("K FRP")

@Composable
@Preview
fun startApp() {
    var text by remember { mutableStateOf("Hello, World!") }

    CharacterBuilder("kingOf0", CharacterTypeManager.default)
        .setName("kingOf0")
        .setStat(CharacterStatManager.BaseStat.CHARISMA, 9)
        .setStat(CharacterStatManager.BaseStat.MAGIC_POWER, 10)
        .setHealth(120)
        .register()
    CharacterBuilder("hain", CharacterTypeManager.default)
        .setName("ERME")
        .setStat(CharacterStatManager.BaseStat.STRENGTH, 2)
        .setStat(CharacterStatManager.BaseStat.AGILITY, 5)
        .setHealth(90)
        .register()
    CharacterBuilder("ceno", CharacterTypeManager.default)
        .setName("Ömer")
        .setStat(CharacterStatManager.BaseStat.STRENGTH, 0)
        .setStat(CharacterStatManager.BaseStat.LUCK, -31)
        .setHealth(70)
        .register()
    CharacterBuilder("havalicocuk", CharacterTypeManager.default)
        .setName("H. Ç.")
        .setStat(CharacterStatManager.BaseStat.DEFENCE, 1)
        .setHealth(60)
        .register()

    MaterialTheme {
        tabs()
    }
}
@Composable
fun tabs() {
    var tabIndex by remember { mutableStateOf(0) } // 1.
    val tabTitles = listOf("Characters", "Dice", "Images")
    Column { // 2.
        TabRow(selectedTabIndex = tabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) })
            }
        }
        when (tabIndex) {
            0 -> buildCharacters()
            1 -> buildDices()
            2 -> buildImages()
        }
    }
}

@Composable
fun buildDices() {

}

@Composable
fun buildImages() {
    val root = File("images")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        root.list()?.forEach { name ->
            Card {
                LOGGER.info(name)
                val bitmap = loadImageBitmap(File(root, name).inputStream())
                Image(bitmap, name, modifier = Modifier.width(200.dp))
            }
        }
    }

}

@Composable
fun buildCharacters() {
    Card(modifier = Modifier.fillMaxSize().offset(10.dp, 10.dp)) {
        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (entry in CharacterManager.characters) {
                buildCharacter(entry.value)
            }
        }
    }
}

@Composable
fun buildCharacter(character: Character) {
    Card(modifier = Modifier
        .padding(5.dp),
        elevation = 10.dp) {
        Card(modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)) {
            Column {
                Text("Name: ${character.name}")
                Spacer(Modifier.padding(10.dp))
                Text("Health: ${character.health}")
                Spacer(Modifier.padding(20.dp))
                buildStats(character)
            }
        }
    }
}

@Composable
fun buildStats(character: Character) {
    Card(elevation = 10.dp) {
        Column {
            Text("Stats", fontFamily = FontFamily.Monospace)
            Spacer(Modifier.padding(2.dp))
            for (stat in character.getStats()) {
                val name = stat.key.getName().take(12)
                Text("- $name${" ".repeat(12 - name.length - stat.value.toString().length)}${stat.value}", fontFamily = FontFamily.Monospace)
            }
        }
    }
}

fun main() = application {
    FileManager.initialize()
    DatabaseManager.initialize()
    CharacterStatManager.initialize()
    CharacterTypeManager.initialize()
    CharacterManager.initialize()

    runBlocking {
        DatabaseManager.setup()
        DatabaseManager.loadAll()
    }


    Window(onCloseRequest = ::exitApplication, title = "KingOf FRP") {
        startApp()
    }
}
