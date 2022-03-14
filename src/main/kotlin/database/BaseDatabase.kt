package database

import LOGGER
import base.Character
import base.CharacterType
import base.CustomCharacterStat
import manager.*
import manager.CharacterStatManager.BaseStat
import util.KUtils.getStats
import util.KUtils.serialize
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

abstract class BaseDatabase(name: String) : IDatabase {

    init {
        LOGGER.info("+ Using Database: '$name'")
    }

    override suspend fun initialize() {
        load()
        setup()
    }

    override suspend fun close(closeable: AutoCloseable?) {
        runCatching{ closeable?.close() }
    }

    abstract override suspend fun getConnection(): Connection?

    override suspend fun setup() {
        var connection: Connection? = null
        var statement: Statement? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")

            statement.executeUpdate("create table if not exists character_stat(" +
                    "id text not null primary key," +
                    "name text not null);")
            statement.executeUpdate("create table if not exists character_type(" +
                    "id text not null primary key," +
                    "name text not null," +
                    "stats text," +
                    "health integer" +
                    ");")
            statement.executeUpdate("create table if not exists character(" +
                    "id text not null primary key," +
                    "name text," +
                    "type text," +
                    "stats text,"+
                    "level integer," +
                    "exp integer," +
                    "maxHealth integer," +
                    "health integer" +
                    ");")

        } finally {
            close(statement)
            close(connection)
        }
    }

    override suspend fun loadAll() {
        if (SettingsManager.insantiateDefaults) {
            instantiateDefaultCharacterTypes()
            instantiateDefaultCharacters()
        }
        loadCharacterStats()
        loadCharacterTypes()
        loadCharacters()
    }

    private suspend fun loadCharacters() {
        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")
            resultSet = statement.executeQuery("select * from character") ?: throw SQLException("Couldn't retrieve result set")

            while (resultSet.next()) {
                val id = resultSet.getString("id")
                val name = resultSet.getString("name")
                val type = resultSet.getString("type")
                val level = resultSet.getInt("level")
                val maxHealth = resultSet.getInt("maxHealth")
                val health = resultSet.getInt("health")

                val character = Character(id, CharacterTypeManager.types.getOrDefault(type, CharacterTypeManager.default))
                character.characterStats = resultSet.getStats("stats")
                character.name = name
                character.level = level
                character.maxHealth = maxHealth
                character.health = health
                CharacterManager.add(character)
            }

        } finally {
            close(resultSet)
            close(statement)
            close(connection)
        }
    }

    private suspend fun loadCharacterStats() {
        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")
            resultSet = statement.executeQuery("select * from character_stat") ?: throw SQLException("Couldn't retrieve result set")
            while (resultSet.next()) {
                val id = resultSet.getString("id")
                val name = resultSet.getString("name")
                CharacterStatManager.stats[id] = CustomCharacterStat(name)
            }

        } finally {
            close(resultSet)
            close(statement)
            close(connection)
        }
    }

    private suspend fun loadCharacterTypes() {
        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")
            resultSet = statement.executeQuery("select * from character_type") ?: throw SQLException("Couldn't retrieve result set")
            while (resultSet.next()) {
                val id = resultSet.getString("id")
                val name = resultSet.getString("name")
                val stats = resultSet.getStats("stats")
                val health = resultSet.getInt("health")

                val type = CharacterType(id)
                type.stats = stats
                type.name = name
                type.baseHealth = health

                CharacterTypeManager.types[id] = type
            }

        } finally {
            close(resultSet)
            close(statement)
            close(connection)
        }
    }

    private suspend fun instantiateDefaultCharacterTypes() {
        CharacterTypeBuilder("mage", "Büyücü")
            .setStat(BaseStat.LUCK, 1)
            .setStat(BaseStat.INTELLIGENCE, 2)
            .setStat(BaseStat.AGILITY, 1)
            .setStat(BaseStat.MAGIC_POWER, 4)
            .register()
            .save()

        CharacterTypeBuilder("warrior", "Savaşçı")
            .setHealth(10)
            .setStat(BaseStat.STRENGTH, 5)
            .setStat(BaseStat.LUCK, 3)
            .register()
            .save()

        CharacterTypeBuilder("elf", "Elf")
            .setStat(BaseStat.LUCK, 1)
            .setStat(BaseStat.INTELLIGENCE, 2)
            .setStat(BaseStat.MAGIC_POWER, 2)
            .setStat(BaseStat.AGILITY, 2)
            .setStat(BaseStat.CHARISMA, 1)
            .register()
            .save()
    }

    private suspend fun instantiateDefaultCharacters() {
        CharacterBuilder("example", CharacterTypeManager.default)
            .setName("Example Character")
            .setHealth(20)
            .setStat(BaseStat.LUCK, 1)
            .setStat(BaseStat.INTELLIGENCE, 2)
            .setStat(BaseStat.MAGIC_POWER, 2)
            .setStat(BaseStat.AGILITY, 2)
            .setStat(BaseStat.CHARISMA, 1)
            .register()
            .save()
    }

    override suspend fun save(id: String, characterType: CharacterType) {
        var connection: Connection? = null
        var statement: Statement? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")

            statement.executeUpdate("insert into character_type(id, name, stats) values('$id', '${characterType.name}', '${characterType.stats.serialize()}');")
        } finally {
            close(statement)
            close(connection)
        }
    }

    override suspend fun save(character: Character) {
        var connection: Connection? = null
        var statement: Statement? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")

            statement.executeUpdate("insert into character(id, name, type, stats, level, maxHealth, health, exp) values('${character.id}', '${character.name}', '${character.type.id}', '${character.characterStats.entries.joinToString { "${it.key}:${it.value}" }}', ${character.level}, ${character.maxHealth}, ${character.health}, 0);")
        } finally {
            close(statement)
            close(connection)
        }
    }

}