package com.bongos.todolist

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object TodoSerializer : Serializer<TodoList> {

    override val defaultValue: TodoList = TodoList()

    override suspend fun readFrom(input: InputStream): TodoList {
        return try {
            Json.decodeFromString(
                TodoList.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read TodoList", serialization)
        }
    }

    override suspend fun writeTo(t: TodoList, output: OutputStream) {
        output.write(
            Json.encodeToString(
                TodoList.serializer(),
                t
            ).encodeToByteArray()
        )
    }
}