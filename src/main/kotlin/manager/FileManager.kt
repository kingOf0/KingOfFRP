package manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat

const val LOGGER_TEMPLATE: String = "[%s}] [KFRP]: %s\n"

object FileManager : IManager("FileManager") {

    private val dateFormat = SimpleDateFormat("hh:mm:ss")
    lateinit var config: YamlFile
    lateinit var privateLogger: FileWriter

    override fun load(): Boolean {
        config = YamlFile(File("config.yml"))
        File("log.txt").apply {
            if (!exists()) createNewFile()
            privateLogger = FileWriter(this, true)
        }
        return true
    }

    fun log(obj: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            privateLogger.write(LOGGER_TEMPLATE.format(dateFormat.format(System.currentTimeMillis()), obj.toString()))
            privateLogger.flush()
        }.invokeOnCompletion {
            it?.printStackTrace()
        }
    }

    fun reload() {
        config.load()
    }


}
