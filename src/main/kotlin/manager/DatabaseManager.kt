package manager

import LOGGER
import base.CharacterType
import database.IDatabase
import database.MySqlDatabase
import database.SQLiteDatabase
import manager.FileManager.config
import java.util.*

object DatabaseManager : IManager("DatabaseManager") {

    private lateinit var database: IDatabase

    public override fun load(): Boolean {
        val section = config.getConfigurationSection("database") ?: run {
            LOGGER.warning("Couldn't found 'database' section in config.yml")
            return false
        }
        database = when((section.getString("driver") ?: "").toLowerCase(Locale.ENGLISH)) {
            "mysql" -> {
                MySqlDatabase(section.getConfigurationSection("mysql") ?: run {
                    LOGGER.warning("Couldn't found 'mysql' section in config.yml")
                    return false
                })
            }
            "sqlite" -> {
                SQLiteDatabase(section.getConfigurationSection("sqlite") ?: run {
                    LOGGER.warning("Couldn't found 'sqlite' section in config.yml")
                    return false
                })
            }
            else -> {
                LOGGER.warning("Couldn't found valid database driver in config.yml! Please type: [sqlite, postgre, mysql]")
                return false
            }
        }
        return true
    }

    suspend fun save(id: String, characterType: CharacterType) {
        database.save(id, characterType)
    }

    internal suspend fun setup() {
        database.initialize()
    }

}