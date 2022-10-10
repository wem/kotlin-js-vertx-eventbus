package ch.sourcemotion.vertx.kotlin.eventbus.server.json

import ch.sourcemotion.vertx.kotlin.eventbus.server.core.KotlinSerializationEngine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class JsonKotlinSerializationEngine(private val protobuf: Json = Json) : KotlinSerializationEngine {
    override fun <T> serialize(obj: T, serializer: KSerializer<T>): String = protobuf.encodeToString(serializer, obj)
    override fun <T> deserialize(serialized: String, deserializer: KSerializer<T>): T = protobuf.decodeFromString(deserializer, serialized)
}