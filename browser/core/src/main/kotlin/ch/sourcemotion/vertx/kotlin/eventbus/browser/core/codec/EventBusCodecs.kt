@file:Suppress("UNCHECKED_CAST")

package ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.EventBusException
import kotlin.reflect.KClass

object EventBusCodecs {
    @PublishedApi
    internal val deserializers = HashMap<KClass<*>, Deserializer<*>>()

    @PublishedApi
    internal val serializers = HashMap<KClass<*>, Serializer<*>>()

    init {
        registerCodec(UnitCodec)
        registerCodec(StringCodec)
    }

    inline fun <reified T : Any> registerCodec(codec: EventBusCodec<T>) {
        registerSerializer(codec)
        registerDeserializer(codec)
    }

    inline fun <reified T : Any> registerDeserializer(deserializer: Deserializer<T>) {
        deserializers[T::class] = deserializer
    }

    inline fun <reified T : Any> registerSerializer(serializer: Serializer<T>) {
        serializers[T::class] = serializer
    }

    inline fun <reified T> getDeserializer(): Deserializer<T> =
        deserializers.getOrElse(T::class) { throw EventBusException("No deserializer found for type: \"${T::class}\"") } as Deserializer<T>

    inline fun <reified T> getSerializer(): Serializer<T> =
        serializers.getOrElse(T::class) { throw EventBusException("No serializer found for type: \"${T::class}\"") } as Serializer<T>

    fun <V> deserializeSafe(deserializer: Deserializer<V>, body: dynamic) = if (body == undefined) {
        null.unsafeCast<V>()
    } else {
        runCatching { deserializer.deserialize(body) }
            .getOrElse { throw EventBusException("Failed to deserialize message body \"$body\" with deserializer \"${deserializer::class}\"", it) }
    }

    inline fun <reified V> serializeSafe(body: V?) = if (body == null) {
        null
    } else {
        val serializer = runCatching { getSerializer<V>() }
            .getOrElse { throw EventBusException("Failed to lookup serializer for type \"${V::class}\"", it) }

        runCatching { serializer.serialize(body) }
            .getOrElse { throw EventBusException("Failed to serialize message body \"$body\" of type \"${V::class}\"", it) }
    }
}

object UnitCodec : EventBusCodec<Unit> {
    override fun deserialize(value: dynamic) {}
    override fun serialize(value: Unit) = Unit.asDynamic()
}

object StringCodec : EventBusCodec<String> {
    override fun serialize(value: String): dynamic = value
    override fun deserialize(value: dynamic) = value.unsafeCast<String>()
}
