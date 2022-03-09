package database

import base.Character
import base.CharacterType
import java.sql.Connection

interface IDatabase {

    suspend fun initialize()
    suspend fun load()
    suspend fun setup()

    suspend fun getConnection(): Connection?

    suspend fun close(closeable: AutoCloseable?)

    suspend fun save(id: String, characterType: CharacterType)
    suspend fun save(character: Character)
    suspend fun loadAll()

}