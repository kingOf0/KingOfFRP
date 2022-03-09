package database

import LOGGER
import org.simpleyaml.configuration.ConfigurationSection
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level

class SQLiteDatabase(section: ConfigurationSection) :
    BaseDatabase("SQLite") {

    private val url: String

    init {
        val file = File(section.getString("file", "data") + ".db")
        file.createNewFile()
        url = "jdbc:sqlite:$file"
    }

    override suspend fun load() {
        Class.forName("org.sqlite.JDBC")
    }

    override suspend fun getConnection(): Connection? {
        try {
            return DriverManager.getConnection(url)
        } catch (e: SQLException) {
            LOGGER.log(Level.SEVERE, "SQL exception on initialize", e)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}