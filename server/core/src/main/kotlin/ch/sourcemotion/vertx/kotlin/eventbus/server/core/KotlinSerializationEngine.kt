package ch.sourcemotion.vertx.kotlin.eventbus.server.core

import kotlinx.serialization.KSerializer

interface KotlinSerializationEngine {
    fun <T> serialize(obj: T, serializer: KSerializer<T>) : String
    fun <T> deserialize(serialized: String, deserializer: KSerializer<T>) : T
}
