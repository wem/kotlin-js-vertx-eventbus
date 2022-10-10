package ch.sourcemotion.vertx.kotlin.eventbus.server.core

import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.ext.bridge.BridgeEventType
import io.vertx.ext.web.handler.sockjs.BridgeEvent
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

class SockJsBridgeCodecs(private val serializationEngine: KotlinSerializationEngine) :
    Handler<BridgeEvent> {

    @PublishedApi
    internal val serializerByType = HashMap<KClass<*>, KSerializer<*>>()
    private val deserializerByAddr = HashMap<String, KSerializer<*>>()

    fun <T> registerJsDeserializer(address: String, serializer: KSerializer<T>) {
        deserializerByAddr[address] = serializer
    }

    inline fun <reified T : Any> registerJsSerializer(serializer: KSerializer<T>) {
        serializerByType[T::class] = serializer
    }

    override fun handle(event: BridgeEvent) {
        runCatching {
            when (event.type()) {
                BridgeEventType.SEND -> event.rawMessage.deserializeBody()
                BridgeEventType.PUBLISH -> event.rawMessage.deserializeBody()
                BridgeEventType.RECEIVE -> event.rawMessage.serializeBody()
                else -> null
            }
        }.onSuccess { event.complete(true) }
            .onFailure { event.fail(it) }
    }

    private fun JsonObject.serializeBody() {
        val dto = getValue("body")
        if (dto != null) {
            val serializer = serializerByType.getOrElse(dto::class) {
                throw IllegalStateException(
                    "No SockJs serializer found for event bus address \"${getAddress()}\" and type \"${dto::class.java.name}\""
                )
            } as KSerializer<Any>

            val encoded = serializationEngine.serialize(dto, serializer)
            put("body", encoded)
        }
    }

    private fun JsonObject.deserializeBody() {
        val address = getAddress()
        val encodedBody = getString("body")
        if (encodedBody != null) {
            val deserializer =
                deserializerByAddr.getOrElse(address) { throw IllegalStateException("No SockJs deserializer found for event bus address \"$address\"") }
            val dto = serializationEngine.deserialize(encodedBody, deserializer)
            put("body", dto)
        }
    }

    private fun JsonObject.getAddress() = getString("address")
}
