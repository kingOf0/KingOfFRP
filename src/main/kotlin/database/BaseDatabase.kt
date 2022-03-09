package database

import LOGGER
import base.CharacterType
import util.KUtils.serialize
import manager.CharacterStatManager.BaseStat
import manager.CharacterTypeBuilder
import java.sql.*

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
            if (statement.executeUpdate("create table if not exists character_type(" +
                        "id text not null primary key," +
                        "name text not null," +
                        "stats text" +
                        ");") == 1) {
                instantiateDefaultCharacterTypes()
            }
            if (statement.executeUpdate("create table if not exists character(" +
                        "name text not null primary key," +
                        "type text not null," +
                        "stats text,"+
                        "level integer" +
                        ");") == 1) {
                instantiateDefaultCharacters()
            }
        } finally {
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

    }

    override suspend fun save(id: String, characterType: CharacterType) {
        var connection: Connection? = null
        var statement: Statement? = null
        try {
            connection = getConnection() ?: throw SQLException("Couldn't retrieve connection")
            statement = connection.createStatement() ?: throw SQLException("Couldn't retrieve statement")

            statement.executeUpdate("insert into character_type(id, name, stats) values('$id', '${characterType.getName()}', '${characterType.stats.serialize()}');")
        } finally {
            close(statement)
            close(connection)
        }
    }

}