package ch.sourcemotion.vertx.kotlin.eventbus.server.protobuf

import ch.sourcemotion.vertx.kotlin.eventbus.server.core.KotlinSerializationEngine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf

class ProtobufKotlinSerializationEngine(private val protobuf: ProtoBuf = ProtoBuf) : KotlinSerializationEngine {
    override fun <T> serialize(obj: T, serializer: KSerializer<T>): String = protobuf.encodeToHexString(serializer, obj)
    override fun <T> deserialize(serialized: String, deserializer: KSerializer<T>): T = protobuf.decodeFromHexString(deserializer, serialized)
}