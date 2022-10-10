package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.MessageJs
import kotlin.js.Promise

/**
 * Message, finally delivered to event bus consumers.
 */
class Message<V> private constructor(
    val address: Address,
    val replyAddress: Address?,
    val type: String,
    val body: V,
    val headers: Map<String, String>,
    @PublishedApi internal val eventBus: EventBus
) {
    companion object {
        internal fun <V> create(
            msgJs: MessageJs,
            headers: Map<String, String>,
            body: V,
            eventBus: EventBus,
        ): Message<V> {
            return Message(
                msgJs.address,
                msgJs.replyAddress,
                msgJs.type ?: "unknown",
                body,
                headers,
                eventBus,
            )
        }
    }

    /**
     * Executes a reply on a message.
     */
    inline fun <reified R : Any> reply(
        replyMsg: R? = null,
        headers: MessageHeaders? = null
    ) {
        if (replyAddress == null) {
            throw EventBusException("No reply expected for message on address: \"$address\"")
        }
        eventBus.send(replyAddress, replyMsg, headers)
    }

    /**
     * Executes a reply on a message and awaits on the reply of this reply.
     */
    inline fun <reified R : Any, reified RR : Any> replyAndRequest(
        replyMsg: R? = null,
        headers: MessageHeaders? = null,
    ): Promise<Message<RR>> {
        if (replyAddress == null) {
            throw EventBusException("No reply expected for message on address: \"$address\"")
        }

        return eventBus.request(replyAddress, replyMsg, headers)
    }
}
