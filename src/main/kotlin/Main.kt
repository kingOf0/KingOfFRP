import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import base.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import manager.*
import java.io.File
import java.util.*
import java.util.logging.Logger
import kotlin.random.Random

//todo: Empty This file. Move all the code to the other files.
//todo: Make it adaptive and responsive.
//todo: Make a Dice Creator Dialog.
//todo: Make Characters scrollable. (Use grids to show characters?)


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

    CharacterTypeBuilder("wood_elf")
        .setName("Wood Elf")
        .setHealth(20)
        .setStat(CharacterStatManager.BaseStat.STRENGTH, 1)
        .register()
    CharacterTypeBuilder("high_elf")
        .setName("High Elf")
        .setHealth(20)
        .setStat(CharacterStatManager.BaseStat.STRENGTH, 1)
        .register()
    CharacterTypeBuilder("human")
        .setName("Human")
        .setHealth(20)
        .setStat(CharacterStatManager.BaseStat.STRENGTH, 1)
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

val dices = listOf(3, 6, 20, 999)
val diceFormat = "Son Zar: %d"
val lastDice = mutableStateOf(diceFormat.format(0))
val openCharacterCreateDialog = mutableStateOf(false)

@Composable
fun buildLastDice() {
    val state = remember { lastDice }
    Card(elevation = 10.dp, modifier = Modifier.offset(30.dp, 30.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = state.value, modifier = Modifier.padding(15.dp, 15.dp))
        }
    }
}

@Composable
fun buildDices() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.offset(20.dp, 20.dp)) {
        for (dice in dices) {
            val state = remember { mutableStateOf("Zar at!") }
            Button(onClick = {
                val random = Random.nextInt(1, dice + 1)
                state.value = random.toString()
                lastDice.value = diceFormat.format(random)
            }) {
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text("$dice zar", modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.padding(20.dp))
                    Text(text = state.value, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }

}

@Composable
fun buildImages() {
    val root = File("images")

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.offset(20.dp, 20.dp)) {
        root.list()?.forEach { name ->
            Card() {
                LOGGER.info(name)
                val bitmap = loadImageBitmap(File(root, name).inputStream())
                Button(onClick = {
                    SettingsManager.backgroundImage.value = bitmap
                }, colors = buttonColors(backgroundColor = Color.Transparent)) {

                    Image(bitmap, name, modifier = Modifier.width(200.dp))
                }
            }
        }
    }

}

@Composable
fun buildCharacters() {
    Card(modifier = Modifier.fillMaxSize().offset(10.dp, 10.dp)) {
        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val characters = mutableStateOf(CharacterManager.characters)
            for (entry in characters.value) {
                buildCharacter(entry.value)
            }
        }
        Column(verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                openCharacterCreateDialog.value = true
            }, colors = buttonColors(backgroundColor = Color.Transparent)) {
                Text("Yeni Karakter Ekle")
            }
        }
    }

    buildCharacterCreateDialog()
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun buildCharacterCreateDialog() {
    if (openCharacterCreateDialog.value) {
        val builder = CharacterBuilder(UUID.randomUUID().toString())

        val name = remember { mutableStateOf("") }
        val type = remember { mutableStateOf(CharacterTypeManager.default) }
        AlertDialog(
            onDismissRequest = {

            },
            text = {
                Column {
                    TextField(name.value, onValueChange = {
                        name.value = it
                        builder.setName(it)
                    }, modifier = Modifier.padding(10.dp), label = {
                        Text("İsim Giriniz")
                    })


                    var lastState = remember { mutableStateOf(FontWeight.Normal) }
                    //todo: make this shit scrolalble
                    Column {
                        for (type1 in CharacterTypeManager.types) {

                            val fontWeightMutableState = remember { mutableStateOf(FontWeight.Normal) }
                            Card(onClick = {
                                lastState.value = FontWeight.Normal
                                lastState = fontWeightMutableState
                                fontWeightMutableState.value = FontWeight.Bold
                                type.value = type1.value
                            }) {
                                Text(text = type1.value.name, fontWeight = fontWeightMutableState.value)
                            }

                        }
                    }
                }
            },
            buttons = {
                Column(
                    modifier = Modifier.padding(all = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        builder.register()
                        CoroutineScope(Dispatchers.IO).launch {
                            builder.save()
                        }.invokeOnCompletion {
                            it?.printStackTrace()
                        }
                        openCharacterCreateDialog.value = false
                    }) {
                        Text("Karakter Oluştur!")
                    }
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        openCharacterCreateDialog.value = false
                    }) {
                        Text("İptal")
                    }
                }
            }
        )
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
    SettingsManager.initialize()
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
    if (SettingsManager.startStreamWindow) {
        Window(onCloseRequest = ::exitApplication, title = "KingOf FRP") {
            startStreamWindow()
        }
    }
}

@Composable
@Preview
fun startStreamWindow() {
    Image(SettingsManager.backgroundImage.value, "Background")
    buildLastDice()
}
