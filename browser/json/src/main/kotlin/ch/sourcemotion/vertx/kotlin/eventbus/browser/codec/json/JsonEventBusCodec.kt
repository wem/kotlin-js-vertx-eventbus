package ch.sourcemotion.vertx.kotlin.eventbus.browser.codec.json

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodec
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class JsonEventBusCodec<T : Any>(
    private val json: Json,
    private val kSerializer: KSerializer<T>
) : EventBusCodec<T> {

    companion object {
        inline fun <reified T : Any> register(kSerializer: KSerializer<T>, json: Json = Json) =
            EventBusCodecs.registerCodec(JsonEventBusCodec(json, kSerializer))
    }

    override fun serialize(value: T): dynamic {
        return json.encodeToString(kSerializer, value)
    }

    override fun deserialize(value: dynamic): T {
        return json.decodeFromString(kSerializer, value.unsafeCast<String>())
    }
}
