package ch.sourcemotion.vertx.kotlin.eventbus.server.cbor

import ch.sourcemotion.vertx.kotlin.eventbus.server.core.KotlinSerializationEngine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString

class CborKotlinSerializationEngine(private val cbor: Cbor = Cbor) : KotlinSerializationEngine {
    override fun <T> serialize(obj: T, serializer: KSerializer<T>): String = cbor.encodeToHexString(serializer, obj)
    override fun <T> deserialize(serialized: String, deserializer: KSerializer<T>): T = cbor.decodeFromHexString(deserializer, serialized)
}