package ch.sourcemotion.vertx.kotlin.eventbus.browser.codec.cbor

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodec
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString

class CborEventBusCodec<T : Any>(
    private val cbor: Cbor,
    private val kSerializer: KSerializer<T>
) : EventBusCodec<T> {

    companion object {
        inline fun <reified T : Any> register(kSerializer: KSerializer<T>, cbor: Cbor = Cbor) =
            EventBusCodecs.registerCodec(CborEventBusCodec(cbor, kSerializer))
    }

    override fun serialize(value: T): dynamic {
        return cbor.encodeToHexString(kSerializer, value)
    }

    override fun deserialize(value: dynamic): T {
        return cbor.decodeFromHexString(kSerializer, value.unsafeCast<String>())
    }
}
