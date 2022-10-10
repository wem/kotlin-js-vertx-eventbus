package ch.sourcemotion.vertx.kotlin.eventbus.browser.codec.protobuf

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodec
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf

class ProtobufEventBusCodec<T : Any>(
    private val protobuf: ProtoBuf,
    private val kSerializer: KSerializer<T>
) : EventBusCodec<T> {

    companion object {
        inline fun <reified T : Any> register(kSerializer: KSerializer<T>, protobuf: ProtoBuf = ProtoBuf) =
            EventBusCodecs.registerCodec(ProtobufEventBusCodec(protobuf, kSerializer))
    }

    override fun serialize(value: T): dynamic {
        return protobuf.encodeToHexString(kSerializer, value)
    }

    override fun deserialize(value: dynamic): T {
        return protobuf.decodeFromHexString(kSerializer, value.unsafeCast<String>())
    }
}
