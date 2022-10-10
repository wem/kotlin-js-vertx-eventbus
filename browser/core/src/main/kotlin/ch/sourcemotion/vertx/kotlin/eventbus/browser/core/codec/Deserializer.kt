package ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec

fun interface Deserializer<T> {
    fun deserialize(value: dynamic): T
}