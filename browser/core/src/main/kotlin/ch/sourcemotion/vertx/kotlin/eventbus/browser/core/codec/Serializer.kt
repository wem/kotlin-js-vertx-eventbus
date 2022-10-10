package ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec

fun interface Serializer<T> {
    fun serialize(value: T): dynamic
}