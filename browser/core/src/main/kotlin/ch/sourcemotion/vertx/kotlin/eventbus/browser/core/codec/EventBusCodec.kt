package ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec

interface EventBusCodec<T> : Serializer<T>, Deserializer<T>